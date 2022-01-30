package com.jankku.eino.ui.book

import androidx.lifecycle.*
import com.jankku.eino.data.BookRepository
import com.jankku.eino.data.enums.BookStatus
import com.jankku.eino.data.enums.Sort
import com.jankku.eino.data.model.Book
import com.jankku.eino.data.model.DetailItem
import com.jankku.eino.network.request.BookRequest
import com.jankku.eino.network.response.book.BookListResponse
import com.jankku.eino.util.Event
import com.jankku.eino.util.Result
import com.jankku.eino.util.utcToLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BookViewModel"

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private var bookId = "null"

    private val _book: MutableLiveData<Book> = MutableLiveData()
    val book: LiveData<Book> get() = _book

    private val _detailItemList: MutableLiveData<Result<MutableList<DetailItem>>> =
        MutableLiveData()
    val detailItemList: LiveData<Result<MutableList<DetailItem>>> get() = _detailItemList

    val bookList: MediatorLiveData<List<Book>> = MediatorLiveData()

    private val _bookListState: MutableLiveData<Result<BookListResponse>> = MutableLiveData()
    val bookListState: LiveData<Result<BookListResponse>> = _bookListState

    private val _ascTitleAscScoreBookList: LiveData<List<Book>?> = _bookListState.map { response ->
        response.data?.results?.sortedWith(compareBy<Book> { it.title }.thenBy { it.score })
    }
    private val _ascTitleDescScoreBookList: LiveData<List<Book>?> = _bookListState.map { response ->
        response.data?.results?.sortedWith(compareBy<Book> { it.title }.thenByDescending { it.score })
    }

    private val _descTitleAscScoreBookList: LiveData<List<Book>?> = _bookListState.map { response ->
        response.data?.results?.sortedWith(compareByDescending<Book> { it.title }.thenBy { it.score })
    }

    private val _descTitleDescScoreBookList: LiveData<List<Book>?> =
        _bookListState.map { response ->
            response.data?.results?.sortedWith(compareBy<Book> { it.title }.thenBy { it.score })
                ?.asReversed()
        }

    private val _selectedTitleSort: MutableLiveData<Sort> = MutableLiveData(Sort.ASCENDING)
    val selectedTitleSort: LiveData<Sort> get() = _selectedTitleSort

    private val _selectedStatusSort: MutableLiveData<BookStatus> = MutableLiveData(BookStatus.ALL)
    val selectedStatusSort: LiveData<BookStatus> get() = _selectedStatusSort

    private val _selectedScoreSort: MutableLiveData<Sort> = MutableLiveData(Sort.ASCENDING)
    val selectedScoreSort: LiveData<Sort> get() = _selectedScoreSort

    init {
        setupBookListSources()
    }

    fun getBooksByStatus() = viewModelScope.launch {
        repository
            .getBooksByStatus(_selectedStatusSort.value!!.value)
            .onStart { _bookListState.value = Result.Loading() }
            .catch { e -> _bookListState.value = Result.Error(e.message) }
            .collect { response ->
                _bookListState.value = response
            }
    }

    fun getBookById() = viewModelScope.launch {
        repository
            .getBookById(bookId)
            .onStart { _detailItemList.value = Result.Loading() }
            .catch { e -> _detailItemList.value = Result.Error(e.message) }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    _book.value = response.data.results[0]
                    bookToDetailItemList(response.data.results[0])
                } else {
                    _detailItemList.value = Result.Error(response.message)
                }
            }
    }

    fun addBook(book: BookRequest) = viewModelScope.launch {
        repository
            .addBook(book)
            .catch { e -> sendEvent(Event.AddItemError(e.message.toString())) }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent(Event.AddItemSuccess(response.data.results[0].message))
                } else {
                    sendEvent(Event.AddItemError(response.message.toString()))
                }
                getBooksByStatus()
            }
    }

    fun editBook(book: BookRequest) = viewModelScope.launch {
        repository
            .editBook(bookId, book)
            .catch { e -> sendEvent(Event.EditItemError(e.message.toString())) }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent(Event.EditItemSuccess(response.data.results[0].message))
                } else {
                    sendEvent(Event.EditItemError(response.message.toString()))
                }
                getBookById()
            }
    }

    fun deleteBook() = viewModelScope.launch {
        repository
            .deleteBook(bookId)
            .catch { e -> sendEvent(Event.DeleteItemError(e.message.toString())) }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent(Event.DeleteItemSuccess(response.data.results[0].message))
                } else {
                    sendEvent(Event.DeleteItemError(response.message.toString()))
                }
                getBooksByStatus()
            }
    }

    fun setTitleSort(status: Sort) {
        _selectedTitleSort.value = status
    }

    fun setStatusSort(status: BookStatus) {
        _selectedStatusSort.value = status
    }

    fun setScoreSort(status: Sort) {
        _selectedScoreSort.value = status
    }

    fun setBookId(id: String) {
        bookId = id
    }

    fun sendEvent(event: Event) = viewModelScope.launch {
        _eventChannel.trySend(event)
    }

    private fun setupBookListSources() {
        bookList.addSource(_ascTitleAscScoreBookList) { result ->
            result?.let {
                if (_selectedTitleSort.value == Sort.ASCENDING &&
                    _selectedScoreSort.value == Sort.ASCENDING
                ) {
                    bookList.value = result
                }
            }
        }

        bookList.addSource(_ascTitleDescScoreBookList) { result ->
            result?.let {
                if (_selectedTitleSort.value == Sort.ASCENDING &&
                    _selectedScoreSort.value == Sort.DESCENDING
                ) {
                    bookList.value = result
                }
            }
        }

        bookList.addSource(_descTitleAscScoreBookList) { result ->
            result?.let {
                if (_selectedTitleSort.value == Sort.DESCENDING &&
                    _selectedScoreSort.value == Sort.ASCENDING
                ) {
                    bookList.value = result
                }
            }
        }

        bookList.addSource(_descTitleDescScoreBookList) { result ->
            result?.let {
                if (_selectedTitleSort.value == Sort.DESCENDING &&
                    _selectedScoreSort.value == Sort.DESCENDING
                ) {
                    bookList.value = result
                }
            }
        }
    }

    private fun bookToDetailItemList(book: Book) {
        with(book) {
            val list = mutableListOf<DetailItem>()
            with(list) {
                add(DetailItem("Title", title))
                add(DetailItem("Author", author))
                add(DetailItem("Publisher", publisher))
                add(DetailItem("Isbn", isbn))
                add(DetailItem("Pages", pages.toString()))
                add(DetailItem("Year", year.toString()))
                add(DetailItem("Status", status.replaceFirstChar { it.uppercase() }))
                add(DetailItem("Score", score.toString()))
                add(DetailItem("Start date", utcToLocal(start_date)))
                add(DetailItem("End date", utcToLocal(end_date)))
            }
            _detailItemList.value = Result.Success(list)
        }
    }
}