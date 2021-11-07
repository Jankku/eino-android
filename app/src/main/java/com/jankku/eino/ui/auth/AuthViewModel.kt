package com.jankku.eino.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.jankku.eino.data.AuthRepository
import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.request.RegisterRequest
import com.jankku.eino.network.response.auth.LoginResponse
import com.jankku.eino.network.response.auth.RegisterResponse
import com.jankku.eino.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
                try {
                    if (response.data != null) {
                        val accessToken = JWT(response.data.accessToken)
                        val refreshToken = JWT(response.data.refreshToken)
                        val jwtUsername = accessToken.claims["username"]?.asString()
                        val accessTokenExpirationTime = accessToken.expiresAt?.time
                        val refreshTokenExpirationTime = refreshToken.expiresAt?.time

                        if (jwtUsername != null &&
                            accessTokenExpirationTime != null &&
                            refreshTokenExpirationTime != null
                        ) {
                            dataStoreManager.run {
                                setUsername(jwtUsername)
                                setAccessToken(response.data.accessToken)
                                setAccessTokenExpirationTime(accessTokenExpirationTime)
                                setRefreshToken(response.data.refreshToken)
                                setRefreshTokenExpirationTime(refreshTokenExpirationTime)
                            }

                            _loginResponse.value = Result.Success(response.data)
                        } else {
                            _loginResponse.value =
                                Result.Error("Username or token expiration time is null")
                        }
                    } else {
                        _loginResponse.value = Result.Error(response.message)
                    }
                } catch (e: Exception) {
                    _loginResponse.value = Result.Error(e.message)
                }
            }
    }
}