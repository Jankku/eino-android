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

        try {
            if (isAuthRoute(oldRequest)) return chain.proceed(oldRequest)

            runBlocking {
                if (dataStoreManager.tokensExist()) {
                    val accessToken = dataStoreManager.getAccessToken()
                    newRequest = oldRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                }
            }
            return if (newRequest != null) chain.proceed(newRequest!!) else buildErrorResponse(
                oldRequest,
                Exception("Error")
            )
        } catch (e: Exception) {
            return buildErrorResponse(oldRequest, e)
        }
    }
}

private fun isAuthRoute(request: Request) = request.url.pathSegments.contains("auth")