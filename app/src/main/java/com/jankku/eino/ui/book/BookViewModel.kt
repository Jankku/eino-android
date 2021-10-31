package com.jankku.eino.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.BookRepository
import com.jankku.eino.data.enums.BookStatus
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BookViewModel"

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    private var bookId = "null"

    private val _book: MutableLiveData<Book> = MutableLiveData()
    val book: LiveData<Book> get() = _book

    private val _detailItemList: MutableLiveData<Result<MutableList<DetailItem>>> =
        MutableLiveData()
    val detailItemList: LiveData<Result<MutableList<DetailItem>>> get() = _detailItemList

    private val _bookList: MutableLiveData<Result<BookListResponse>> = MutableLiveData()
    val bookList: LiveData<Result<BookListResponse>> = _bookList

    private val _selectedStatus: MutableLiveData<BookStatus> = MutableLiveData(BookStatus.ALL)
    val selectedStatus: LiveData<BookStatus> get() = _selectedStatus

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        getBooksByStatus()
    }

    fun getBooksByStatus() = viewModelScope.launch {
        _bookList.postValue(Result.Loading())
        repository
            .getBooksByStatus(selectedStatus.value!!.value)
            .catch { e -> _bookList.value = Result.Error(e.message) }
            .collect { response ->
                _bookList.postValue(response)
            }
    }

    fun getBookById() = viewModelScope.launch {
        _detailItemList.value = Result.Loading()
        repository
            .getBookById(bookId)
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
            .catch { e -> sendEvent { Event.AddBookErrorEvent(e.message.toString()) } }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent { Event.AddBookSuccessEvent(response.data.results[0].message) }
                } else {
                    sendEvent { Event.AddBookErrorEvent(response.message.toString()) }
                }
                getBooksByStatus()
            }
    }

    fun editBook(book: BookRequest) = viewModelScope.launch {
        repository
            .editBook(bookId, book)
            .catch { e -> sendEvent { Event.EditBookErrorEvent(e.message.toString()) } }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent { Event.EditBookSuccessEvent(response.data.results[0].message) }
                } else {
                    sendEvent { Event.EditBookErrorEvent(response.message.toString()) }
                }
                getBookById()
            }
    }

    fun deleteBook() = viewModelScope.launch {
        repository
            .deleteBook(bookId)
            .catch { e -> sendEvent { Event.DeleteBookErrorEvent(e.message.toString()) } }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent { Event.DeleteBookSuccessEvent(response.data.results[0].message) }
                } else {
                    sendEvent { Event.DeleteBookErrorEvent(response.message.toString()) }
                }
                getBooksByStatus()
            }
    }

    fun setStatus(status: BookStatus) {
        _selectedStatus.value = status
    }

    fun setBookId(id: String) {
        bookId = id
    }

    fun sendEvent(event: () -> Event) = viewModelScope.launch {
        _eventChannel.trySend(event())
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