package com.jankku.eino.data

import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.request.RegisterRequest
import com.jankku.eino.util.Result
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: EinoApi,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun register(body: RegisterRequest) = flowOf(
        try {
            Result.Success(api.register(body))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun login(body: LoginRequest) = flowOf(
        try {
            Result.Success(api.login(body))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun logOut() = flowOf(
        try {
            dataStoreManager.clear()
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun getUsername() = flowOf(
        try {
            Result.Success(dataStoreManager.getUsername())
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )
}