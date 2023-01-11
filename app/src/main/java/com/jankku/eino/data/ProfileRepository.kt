package com.jankku.eino.data

import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.request.DeleteAccountRequest
import com.jankku.eino.util.handleResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: EinoApi,
    private val moshi: Moshi
) {
    suspend fun getProfile() = handleResponse(api.getProfile(), moshi).flowOn(Dispatchers.IO)

    suspend fun generateShare() = handleResponse(api.generateShare(), moshi).flowOn(Dispatchers.IO)

    suspend fun deleteAccount(body: DeleteAccountRequest) =
        handleResponse(api.deleteAccount(body), moshi).flowOn(Dispatchers.IO)
}