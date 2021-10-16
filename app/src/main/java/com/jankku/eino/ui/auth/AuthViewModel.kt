package com.jankku.eino.ui.auth

import android.util.Log
import androidx.lifecycle.*
import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.data.DataStoreManager.Companion.ACCESS_TOKEN
import com.jankku.eino.data.DataStoreManager.Companion.REFRESH_TOKEN
import com.jankku.eino.network.EinoApiInterface
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
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: EinoApiInterface,
    networkStatusTracker: NetworkStatusTracker,
    private val dataStoreManager: DataStoreManager
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

    private fun isAlreadyLoggedIn() {
        viewModelScope.launch {
            try {
                val accessToken = dataStoreManager.getString(ACCESS_TOKEN)
                val refreshToken = dataStoreManager.getString(REFRESH_TOKEN)
                _isLoggedIn.postValue(accessToken.isNotBlank() && refreshToken.isNotBlank())
            } catch (e: Exception) {
                _isLoggedIn.postValue(false)
            }
        }
    }

    fun register(username: String, password: String, password2: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _registerResponse.postValue(Result.Loading())
                val body = RegisterRequest(username, password, password2)
                val response = api.register(body)
                _registerResponse.postValue(Result.Success(response))
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                _registerResponse.postValue(Result.Error(e.message))
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loginResponse.postValue(Result.Loading())
                val body = LoginRequest(username, password)
                val response = api.login(body)
                _loginResponse.postValue(Result.Success(response))

                dataStoreManager.putString(ACCESS_TOKEN, response.accessToken)
                dataStoreManager.putString(REFRESH_TOKEN, response.refreshToken)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                _loginResponse.postValue(Result.Error(e.message))
            }
        }
    }
}