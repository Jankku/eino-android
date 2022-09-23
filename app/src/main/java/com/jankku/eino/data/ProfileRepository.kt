package com.jankku.eino.data

import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.request.DeleteAccountRequest
import com.jankku.eino.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: EinoApi,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getProfile() = flowOf(
        try {
            Result.Success(api.getProfile(dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun deleteAccount(body: DeleteAccountRequest) = flowOf(
        try {
            Result.Success(api.deleteAccount(body, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)
}