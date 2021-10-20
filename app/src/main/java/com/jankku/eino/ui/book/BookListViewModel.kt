package com.jankku.eino.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.BookRepository
import com.jankku.eino.network.request.AddBookRequest
import com.jankku.eino.network.response.BookListResponse
import com.jankku.eino.util.Event
import com.jankku.eino.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BookListViewModel"

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    private val _bookListResponse: MutableLiveData<Result<BookListResponse>> = MutableLiveData()
    val bookListResponse: LiveData<Result<BookListResponse>> = _bookListResponse

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        getAllBooks()
    }

    private fun getAllBooks() = viewModelScope.launch {
        _bookListResponse.postValue(Result.Loading())
        repository
            .getAllBooks()
            .catch { e -> _bookListResponse.postValue(Result.Error(e.message)) }
            .collect { response -> _bookListResponse.postValue(response) }
    }

    fun addBook(book: AddBookRequest) = viewModelScope.launch {
        repository
            .addBook(book)
            .catch { e -> sendAddBookErrorEvent(e.message.toString()) }
            .collect { response ->
                sendAddBookSuccessEvent(response.data!!.results[0].message)
                getAllBooks()
            }
    }

    private fun sendAddBookSuccessEvent(message: String) = viewModelScope.launch {
        _eventChannel.send(Event.AddBookSuccessEvent(message))
    }

    private fun sendAddBookErrorEvent(message: String) = viewModelScope.launch {
        _eventChannel.send(Event.AddBookErrorEvent(message))
    }
}