package com.jankku.eino.ui.auth

import android.util.Log
import androidx.lifecycle.*
import com.jankku.eino.network.EinoApiInterface
import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.response.LoginResponse
import com.jankku.eino.util.NetworkStatus
import com.jankku.eino.util.NetworkStatusTracker
import com.jankku.eino.util.Result
import com.jankku.eino.util.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: EinoApiInterface,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {

    private val _response: MutableLiveData<Result<LoginResponse>> = MutableLiveData()
    val response: LiveData<Result<LoginResponse>> get() = _response

    @ExperimentalCoroutinesApi
    val networkStatus = networkStatusTracker.networkStatus.map(
        onUnavailable = { NetworkStatus.Unavailable },
        onAvailable = { NetworkStatus.Available },
    ).asLiveData(Dispatchers.IO)

    fun register() {}

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _response.postValue(Result.Loading())
                val body = LoginRequest(username, password)
                val response = api.login(body)
                _response.postValue(Result.Success(response))
                Log.d(TAG, "login: $response")
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                _response.postValue(Result.Error(e.message))
            }
        }
    }
}