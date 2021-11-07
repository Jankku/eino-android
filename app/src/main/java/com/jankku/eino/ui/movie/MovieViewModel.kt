package com.jankku.eino.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.MovieRepository
import com.jankku.eino.data.enums.MovieStatus
import com.jankku.eino.data.model.DetailItem
import com.jankku.eino.data.model.Movie
import com.jankku.eino.network.request.MovieRequest
import com.jankku.eino.network.response.movie.MovieListResponse
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

private const val TAG = "MovieViewModel"

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {
    private var movieId = "null"

    private val _movie: MutableLiveData<Movie> = MutableLiveData()
    val movie: LiveData<Movie> get() = _movie

    private val _detailItemList: MutableLiveData<Result<MutableList<DetailItem>>> =
        MutableLiveData()
    val detailItemList: LiveData<Result<MutableList<DetailItem>>> get() = _detailItemList

    private val _movieList: MutableLiveData<Result<MovieListResponse>> = MutableLiveData()
    val movieList: LiveData<Result<MovieListResponse>> = _movieList

    private val _selectedStatus: MutableLiveData<MovieStatus> = MutableLiveData(MovieStatus.ALL)
    val selectedStatus: LiveData<MovieStatus> get() = _selectedStatus

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        getMoviesByStatus()
    }

    fun getMoviesByStatus() = viewModelScope.launch {
        _movieList.postValue(Result.Loading())
        repository
            .getMoviesByStatus(selectedStatus.value!!.value)
            .catch { e -> _movieList.value = Result.Error(e.message) }
            .collect { response ->
                _movieList.postValue(response)
            }
    }

    fun getMovieById() = viewModelScope.launch {
        _detailItemList.value = Result.Loading()
        repository
            .getMovieById(movieId)
            .catch { e -> _detailItemList.value = Result.Error(e.message) }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    _movie.value = response.data.results[0]
                    movieToDetailItemList(response.data.results[0])
                } else {
                    _detailItemList.value = Result.Error(response.message)
                }
            }
    }

    fun addMovie(movie: MovieRequest) = viewModelScope.launch {
        repository
            .addMovie(movie)
            .catch { e -> sendEvent { Event.AddItemErrorEvent(e.message.toString()) } }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent { Event.AddItemSuccessEvent(response.data.results[0].message) }
                } else {
                    sendEvent { Event.AddItemErrorEvent(response.message.toString()) }
                }
                getMoviesByStatus()
            }
    }

    fun editMovie(movie: MovieRequest) = viewModelScope.launch {
        repository
            .editMovie(movieId, movie)
            .catch { e -> sendEvent { Event.EditItemError(e.message.toString()) } }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent { Event.EditItemSuccess(response.data.results[0].message) }
                } else {
                    sendEvent { Event.EditItemError(response.message.toString()) }
                }
                getMovieById()
            }
    }

    fun deleteMovie() = viewModelScope.launch {
        repository
            .deleteMovie(movieId)
            .catch { e -> sendEvent { Event.DeleteItemError(e.message.toString()) } }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent { Event.DeleteItemSuccess(response.data.results[0].message) }
                } else {
                    sendEvent { Event.DeleteItemError(response.message.toString()) }
                }
                getMoviesByStatus()
            }
    }

    fun setStatus(status: MovieStatus) {
        _selectedStatus.value = status
    }

    fun setMovieId(id: String) {
        movieId = id
    }

    fun sendEvent(event: () -> Event) = viewModelScope.launch {
        _eventChannel.trySend(event())
    }

    private fun movieToDetailItemList(movie: Movie) {
        with(movie) {
            val list = mutableListOf<DetailItem>()
            with(list) {
                add(DetailItem("Title", title))
                add(DetailItem("Studio", studio))
                add(DetailItem("Director", director))
                add(DetailItem("Writer", writer))
                add(DetailItem("Duration", duration.toString()))
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