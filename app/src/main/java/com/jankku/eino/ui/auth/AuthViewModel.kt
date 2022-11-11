package com.jankku.eino.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jankku.eino.data.AuthRepository
import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.request.RegisterRequest
import com.jankku.eino.network.response.auth.LoginResponse
import com.jankku.eino.network.response.auth.RegisterResponse
import com.jankku.eino.util.Result
import com.jankku.eino.util.isJWTNotExpired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    private val _registerResponse: MutableLiveData<Result<RegisterResponse>> = MutableLiveData()
    val registerResponse: LiveData<Result<RegisterResponse>> get() = _registerResponse

    private val _loginResponse: MutableLiveData<Result<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Result<LoginResponse>> get() = _loginResponse

    private val _isLoggedIn: MutableLiveData<Boolean> = MutableLiveData()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    init {
        isAlreadyLoggedIn()
    }

    private fun isAlreadyLoggedIn() = viewModelScope.launch {
        try {
            val tokensExist = dataStoreManager.tokensExist()
            val refreshToken = dataStoreManager.getRefreshToken()
            val isValidTokens = tokensExist && isJWTNotExpired(refreshToken)
            _isLoggedIn.value = isValidTokens
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
        val body = LoginRequest(username, password)
        repository
            .login(body)
            .onStart { _loginResponse.value = Result.Loading() }
            .collect { response ->
                try {
                    if (response.data == null) {
                        _loginResponse.value = Result.Error(response.message.toString())
                        return@collect
                    }

                    dataStoreManager.run {
                        setAccessToken(response.data.accessToken)
                        setRefreshToken(response.data.refreshToken)
                    }

                    _loginResponse.value = Result.Success(response.data)
                } catch (e: Exception) {
                    _loginResponse.value = Result.Error(e.toString())
                }
            }
    }
}