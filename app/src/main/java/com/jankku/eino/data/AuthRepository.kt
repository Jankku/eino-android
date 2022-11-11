package com.jankku.eino.data

import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.request.RegisterRequest
import com.jankku.eino.util.Result
import com.jankku.eino.util.handleResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: EinoApi,
    private val dataStoreManager: DataStoreManager,
    private val moshi: Moshi
) {
    suspend fun register(body: RegisterRequest) =
        handleResponse(api.register(body), moshi).flowOn(Dispatchers.IO)

    suspend fun login(body: LoginRequest) =
        handleResponse(api.login(body), moshi).flowOn(Dispatchers.IO)

    suspend fun logOut() = flowOf(
        try {
            dataStoreManager.clear()
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)
}