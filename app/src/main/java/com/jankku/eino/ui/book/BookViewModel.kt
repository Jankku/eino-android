package com.jankku.eino.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.BookRepository
import com.jankku.eino.data.model.Book
import com.jankku.eino.data.model.DetailItem
import com.jankku.eino.network.request.AddBookRequest
import com.jankku.eino.network.response.BookListResponse
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
    // Detail screen
    private var bookId: String? = null
    val detailItemList = mutableListOf<DetailItem>()

    // Book list
    private val _books: MutableLiveData<Result<BookListResponse>> = MutableLiveData()
    val books: LiveData<Result<BookListResponse>> = _books

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        getAllBooks()
    }

    private fun getAllBooks() = viewModelScope.launch {
        _books.postValue(Result.Loading())
        repository
            .getAllBooks()
            .catch { e -> _books.postValue(Result.Error(e.message)) }
            .collect { response -> _books.postValue(response) }
    }

    fun addBook(book: AddBookRequest) = viewModelScope.launch {
        repository
            .addBook(book)
            .catch { e ->
                sendEvent { Event.AddBookErrorEvent(e.message.toString()) }
            }
            .collect { response ->
                sendEvent { Event.AddBookSuccessEvent(response.data!!.results[0].message) }
                getAllBooks()
            }
    }

    fun deleteBook() = viewModelScope.launch {
        bookId?.let {
            repository
                .deleteBook(it)
                .catch { e ->
                    sendEvent { Event.DeleteBookErrorEvent(e.message.toString()) }
                }
                .collect { response ->
                    sendEvent { Event.DeleteBookSuccessEvent(response.data!!.results[0].message) }
                    getAllBooks()
                }
        }
    }

    fun bookToDetailItemList(book: Book) {
        bookId = book.book_id
        with(book) {
            with(detailItemList) {
                clear()
                add(DetailItem("Title", title))
                add(DetailItem("Author", author))
                add(DetailItem("Publisher", publisher))
                add(DetailItem("ISBN", isbn))
                add(DetailItem("Pages", pages.toString()))
                add(DetailItem("Year", year.toString()))
                add(DetailItem("Status", status.replaceFirstChar { it.uppercase() }))
                add(DetailItem("Score", score.toString()))
                add(DetailItem("Start date", utcToLocal(start_date)))
                add(DetailItem("End date", utcToLocal(end_date)))
            }
        }
    }

    private fun sendEvent(event: () -> Event) = viewModelScope.launch {
        _eventChannel.send(event())
    }
}