package com.jankku.eino.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.BookRepository
import com.jankku.eino.network.response.BookListResponse
import com.jankku.eino.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BookListViewModel"

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _bookListResponse: MutableLiveData<Result<BookListResponse>> = MutableLiveData()
    val bookListResponse: LiveData<Result<BookListResponse>> = _bookListResponse

    init {
        getAllBooks()
    }

    private fun getAllBooks() {
        viewModelScope.launch {
            _bookListResponse.postValue(Result.Loading())
            repository
                .getAllBooks()
                .distinctUntilChanged()
                .catch { e -> _bookListResponse.postValue(Result.Error(e.message)) }
                .collect { response -> _bookListResponse.postValue(Result.Success(response)) }
        }
    }
}