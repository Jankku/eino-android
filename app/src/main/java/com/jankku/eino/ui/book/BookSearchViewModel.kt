package com.jankku.eino.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.BookRepository
import com.jankku.eino.network.response.book.BookSearchResponse
import com.jankku.eino.util.Event
import com.jankku.eino.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _searchResults: MutableLiveData<Result<BookSearchResponse>> = MutableLiveData()
    val searchResults: LiveData<Result<BookSearchResponse>> = _searchResults

    fun search(query: String) = viewModelScope.launch {
        repository
            .searchBooks(query)
            .onStart { _searchResults.value = Result.Loading() }
            .catch { e -> _searchResults.value = Result.Error(e.message) }
            .collect { response -> _searchResults.value = response }
    }

    fun sendEvent(event: Event) = viewModelScope.launch {
        _eventChannel.trySend(event)
    }
}