package com.jankku.eino.ui.auth

import androidx.lifecycle.*
import com.jankku.eino.data.AuthRepository
import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.request.RegisterRequest
import com.jankku.eino.network.response.LoginResponse
import com.jankku.eino.network.response.RegisterResponse
import com.jankku.eino.util.NetworkStatus
import com.jankku.eino.util.NetworkStatusTracker
import com.jankku.eino.util.Result
import com.jankku.eino.util.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val dataStoreManager: DataStoreManager,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    private val _registerResponse: MutableLiveData<Result<RegisterResponse>> = MutableLiveData()
    val registerResponse: LiveData<Result<RegisterResponse>> get() = _registerResponse

    private val _loginResponse: MutableLiveData<Result<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Result<LoginResponse>> get() = _loginResponse

    private val _isLoggedIn: MutableLiveData<Boolean> = MutableLiveData()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    @ExperimentalCoroutinesApi
    val networkStatus = networkStatusTracker.networkStatus.map(
        onUnavailable = { NetworkStatus.Unavailable },
        onAvailable = { NetworkStatus.Available },
    ).asLiveData(Dispatchers.IO)

    init {
        isAlreadyLoggedIn()
    }

    private fun isAlreadyLoggedIn() = viewModelScope.launch {
        try {
            val accessToken = dataStoreManager.getAccessToken()
            val refreshToken = dataStoreManager.getRefreshToken()
            _isLoggedIn.value = accessToken.isNotBlank() && refreshToken.isNotBlank()
        } catch (e: Exception) {
            _isLoggedIn.value = false
        }
    }

    fun register(username: String, password: String, password2: String) =
        viewModelScope.launch {
            _registerResponse.value = Result.Loading()
            val body = RegisterRequest(username, password, password2)
            repository
                .register(body)
                .catch { e -> _registerResponse.value = Result.Error(e.message) }
                .collect { response -> _registerResponse.value = response }
        }

    fun login(username: String, password: String) = viewModelScope.launch {
        _loginResponse.value = Result.Loading()
        val body = LoginRequest(username, password)
        repository
            .login(body)
            .catch { e -> _loginResponse.value = Result.Error(e.message) }
            .collect { response ->
                response.data?.let { dataStoreManager.setTokens(it.accessToken, it.refreshToken) }
                _loginResponse.value = response
            }
    }
}