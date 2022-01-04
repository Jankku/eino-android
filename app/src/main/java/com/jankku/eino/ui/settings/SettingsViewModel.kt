package com.jankku.eino.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.AuthRepository
import com.jankku.eino.util.Event
import com.jankku.eino.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private var _username: MutableLiveData<Result<String>> = MutableLiveData()
    val username: LiveData<Result<String>> get() = _username

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        getUsername()
    }

    private fun getUsername() = viewModelScope.launch {
        repository
            .getUsername()
            .catch { e -> _username.value = Result.Error(e.message.toString()) }
            .collect { response -> _username.value = Result.Success(response.data.toString()) }
    }

    fun logOut() = viewModelScope.launch {
        repository
            .logOut()
            .catch { e -> sendEvent(Event.LogoutErrorEvent(e.message.toString())) }
            .collect { response -> sendEvent(Event.LogoutSuccessEvent(response.data.toString())) }
    }

    private fun sendEvent(event: Event) = viewModelScope.launch {
        _eventChannel.trySend(event)
    }
}