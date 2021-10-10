package com.jankku.eino.ui.book

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.data.DataStoreManager.Companion.ACCESS_TOKEN
import com.jankku.eino.network.EinoApiInterface
import com.jankku.eino.network.response.BookListResponse
import com.jankku.eino.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BookListViewModel"

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val api: EinoApiInterface,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _bookListResponse: MutableLiveData<Result<BookListResponse>> = MutableLiveData()
    val bookListResponse: LiveData<Result<BookListResponse>> get() = _bookListResponse

    init {
        getAllBooks()
    }

    private fun getAllBooks() {
        viewModelScope.launch {
            try {
                _bookListResponse.postValue(Result.Loading())
                val accessToken = "Bearer ${dataStoreManager.getString(ACCESS_TOKEN)}"
                val response = api.getAllBooks(accessToken)
                _bookListResponse.postValue(Result.Success(response))
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                _bookListResponse.postValue(Result.Error(e.message))
            }
        }
    }
}