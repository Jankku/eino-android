package com.jankku.eino.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.AuthRepository
import com.jankku.eino.data.ProfileRepository
import com.jankku.eino.network.response.profile.ProfileResponse
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

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _profile = MutableLiveData<Result<ProfileResponse>>()
    val profile get() = _profile

    init {
        getProfile()
    }

    fun getProfile() = viewModelScope.launch {
        profileRepository
            .getProfile()
            .onStart { _profile.value = Result.Loading() }
            .catch { e -> sendEvent(Event.ProfileError(e.message.toString())) }
            .collect { response -> _profile.value = response }
    }

    fun logOut() = viewModelScope.launch {
        authRepository
            .logOut()
            .catch { e -> sendEvent(Event.LogoutError(e.message.toString())) }
            .collect { response -> sendEvent(Event.LogoutSuccess(response.data.toString())) }
    }

    fun sendEvent(event: Event) = viewModelScope.launch {
        _eventChannel.trySend(event)
    }
}