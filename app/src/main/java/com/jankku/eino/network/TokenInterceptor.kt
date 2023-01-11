package com.jankku.eino.network

import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.util.buildErrorResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "TokenInterceptor"

class TokenInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        var newRequest: Request? = null

        return try {
            runBlocking {
                if (dataStoreManager.tokensExist()) {
                    val accessToken = dataStoreManager.getAccessToken()
                    newRequest = oldRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                }
            }
            val response =
                if (newRequest != null) chain.proceed(newRequest!!) else buildErrorResponse(
                    oldRequest,
                    Exception("Error")
                )
            response
        } catch (e: Exception) {
            buildErrorResponse(oldRequest, e)
        }
    }
}