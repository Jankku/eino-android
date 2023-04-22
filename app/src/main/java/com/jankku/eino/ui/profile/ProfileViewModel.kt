package com.jankku.eino.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.AuthRepository
import com.jankku.eino.data.ProfileRepository
import com.jankku.eino.network.request.DeleteAccountRequest
import com.jankku.eino.network.request.ExportAccountRequest
import com.jankku.eino.network.response.profile.ExportAccountResponse
import com.jankku.eino.network.response.profile.ProfileResponse
import com.jankku.eino.network.response.profile.ShareProfileResponse
import com.jankku.eino.util.Event
import com.jankku.eino.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
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

    private val _share = MutableLiveData<Result<ShareProfileResponse>>()
    val share get() = _share

    private val _accountData = MutableLiveData<Result<ExportAccountResponse>>()
    val accountData get() = _accountData

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

    fun generateShare() = viewModelScope.launch {
        profileRepository
            .generateShare()
            .onStart { _share.value = Result.Loading() }
            .catch { e -> sendEvent(Event.ShareError(e.message.toString())) }
            .collect { response -> _share.value = response }
    }

    fun logOut() = viewModelScope.launch {
        authRepository
            .logOut()
            .catch { e -> sendEvent(Event.LogoutError(e.message.toString())) }
            .collect { response -> sendEvent(Event.LogoutSuccess(response.data.toString())) }
    }

    fun deleteAccount(password: String) = viewModelScope.launch {
        val body = DeleteAccountRequest(password)
        profileRepository
            .deleteAccount(body)
            .catch { e -> sendEvent(Event.DeleteAccountError(e.message.toString())) }
            .collect { response ->
                if (response.data != null) {
                    sendEvent(Event.DeleteAccountSuccess(response.data.results[0].message))
                } else {
                    sendEvent(Event.DeleteAccountError(response.message.toString()))
                }
            }
    }

    fun exportAccount(password: String) = viewModelScope.launch {
        val body = ExportAccountRequest(password)
        profileRepository
            .exportAccount(body)
            .catch { e -> sendEvent(Event.ExportAccountError(e.message.toString())) }
            .collect { response -> _accountData.value = response }
    }

    fun sendEvent(event: Event) = viewModelScope.launch {
        _eventChannel.trySend(event)
    }
}
