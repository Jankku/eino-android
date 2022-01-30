package com.jankku.eino.ui.movie

import androidx.lifecycle.*
import com.jankku.eino.data.MovieRepository
import com.jankku.eino.data.enums.MovieStatus
import com.jankku.eino.data.enums.Sort
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MovieViewModel"

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {
    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private var movieId = "null"

    private val _movie: MutableLiveData<Movie> = MutableLiveData()
    val movie: LiveData<Movie> get() = _movie

    private val _detailItemList: MutableLiveData<Result<MutableList<DetailItem>>> =
        MutableLiveData()
    val detailItemList: LiveData<Result<MutableList<DetailItem>>> get() = _detailItemList

    val movieList: MediatorLiveData<List<Movie>> = MediatorLiveData()

    private val _movieListState: MutableLiveData<Result<MovieListResponse>> = MutableLiveData()
    val movieListState: LiveData<Result<MovieListResponse>> = _movieListState

    private val _ascTitleAscScoreMovieList: LiveData<List<Movie>?> =
        _movieListState.map { response ->
            response.data?.results?.sortedWith(compareBy<Movie> { it.title }.thenBy { it.score })
        }
    private val _ascTitleDescScoreMovieList: LiveData<List<Movie>?> =
        _movieListState.map { response ->
            response.data?.results?.sortedWith(compareBy<Movie> { it.title }.thenByDescending { it.score })
        }

    private val _descTitleAscScoreMovieList: LiveData<List<Movie>?> =
        _movieListState.map { response ->
            response.data?.results?.sortedWith(compareByDescending<Movie> { it.title }.thenBy { it.score })
        }

    private val _descTitleDescScoreMovieList: LiveData<List<Movie>?> =
        _movieListState.map { response ->
            response.data?.results?.sortedWith(compareBy<Movie> { it.title }.thenBy { it.score })
                ?.asReversed()
        }

    private val _selectedTitleSort: MutableLiveData<Sort> = MutableLiveData(Sort.ASCENDING)
    val selectedTitleSort: LiveData<Sort> get() = _selectedTitleSort

    private val _selectedStatusSort: MutableLiveData<MovieStatus> = MutableLiveData(MovieStatus.ALL)
    val selectedStatusSort: LiveData<MovieStatus> get() = _selectedStatusSort

    private val _selectedScoreSort: MutableLiveData<Sort> = MutableLiveData(Sort.ASCENDING)
    val selectedScoreSort: LiveData<Sort> get() = _selectedScoreSort

    init {
        setupMovieListSources()
    }

    fun getMoviesByStatus() = viewModelScope.launch {
        repository
            .getMoviesByStatus(selectedStatusSort.value!!.value)
            .onStart { _movieListState.value = Result.Loading() }
            .catch { e -> _movieListState.value = Result.Error(e.message) }
            .collect { response ->
                _movieListState.value = response
            }
    }

    fun getMovieById() = viewModelScope.launch {
        repository
            .getMovieById(movieId)
            .onStart { _detailItemList.value = Result.Loading() }
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
            .catch { e -> sendEvent(Event.AddItemError(e.message.toString())) }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent(Event.AddItemSuccess(response.data.results[0].message))
                } else {
                    sendEvent(Event.AddItemError(response.message.toString()))
                }
                getMoviesByStatus()
            }
    }

    fun editMovie(movie: MovieRequest) = viewModelScope.launch {
        repository
            .editMovie(movieId, movie)
            .catch { e -> sendEvent(Event.EditItemError(e.message.toString())) }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent(Event.EditItemSuccess(response.data.results[0].message))
                } else {
                    sendEvent(Event.EditItemError(response.message.toString()))
                }
                getMovieById()
            }
    }

    fun deleteMovie() = viewModelScope.launch {
        repository
            .deleteMovie(movieId)
            .catch { e -> sendEvent(Event.DeleteItemError(e.message.toString())) }
            .collect { response ->
                if (response.data?.results?.get(0) != null) {
                    sendEvent(Event.DeleteItemSuccess(response.data.results[0].message))
                } else {
                    sendEvent(Event.DeleteItemError(response.message.toString()))
                }
                getMoviesByStatus()
            }
    }

    fun setTitleSort(status: Sort) {
        _selectedTitleSort.value = status
    }

    fun setStatusSort(status: MovieStatus) {
        _selectedStatusSort.value = status
    }

    fun setScoreSort(status: Sort) {
        _selectedScoreSort.value = status
    }

    fun setMovieId(id: String) {
        movieId = id
    }

    fun sendEvent(event: Event) = viewModelScope.launch {
        _eventChannel.trySend(event)
    }

    private fun setupMovieListSources() {
        movieList.addSource(_ascTitleAscScoreMovieList) { result ->
            result?.let {
                if (_selectedTitleSort.value == Sort.ASCENDING &&
                    _selectedScoreSort.value == Sort.ASCENDING
                ) {
                    movieList.value = result
                }
            }
        }

        movieList.addSource(_ascTitleDescScoreMovieList) { result ->
            result?.let {
                if (_selectedTitleSort.value == Sort.ASCENDING &&
                    _selectedScoreSort.value == Sort.DESCENDING
                ) {
                    movieList.value = result
                }
            }
        }

        movieList.addSource(_descTitleAscScoreMovieList) { result ->
            result?.let {
                if (_selectedTitleSort.value == Sort.DESCENDING &&
                    _selectedScoreSort.value == Sort.ASCENDING
                ) {
                    movieList.value = result
                }
            }
        }

        movieList.addSource(_descTitleDescScoreMovieList) { result ->
            result?.let {
                if (_selectedTitleSort.value == Sort.DESCENDING &&
                    _selectedScoreSort.value == Sort.DESCENDING
                ) {
                    movieList.value = result
                }
            }
        }
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