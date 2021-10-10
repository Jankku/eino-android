package com.jankku.eino.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.MovieRepository
import com.jankku.eino.network.response.MovieListResponse
import com.jankku.eino.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MovieListViewModel"

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movieListResponse: MutableLiveData<Result<MovieListResponse>> = MutableLiveData()
    val movieListResponse: LiveData<Result<MovieListResponse>> get() = _movieListResponse

    init {
        getAllMovies()
    }

    private fun getAllMovies() {
        viewModelScope.launch {
            _movieListResponse.postValue(Result.Loading())
            repository
                .getAllMovies()
                .distinctUntilChanged()
                .catch { e -> _movieListResponse.postValue(Result.Error(e.message)) }
                .collect { response -> _movieListResponse.postValue(Result.Success(response)) }
        }
    }
}