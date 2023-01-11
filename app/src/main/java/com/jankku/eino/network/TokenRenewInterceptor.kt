package com.jankku.eino.network

import android.util.Log
import com.jankku.eino.BuildConfig
import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.network.request.RefreshTokenRequest
import com.jankku.eino.network.response.ApiError
import com.jankku.eino.network.response.ApiErrorResponse
import com.jankku.eino.util.buildErrorResponse
import com.jankku.eino.util.isJWTExpired
import com.jankku.eino.util.isJWTNotExpired
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

private const val TAG = "TokenRenewInterceptor"

class TokenRenewInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val moshi: Moshi
) : Interceptor {
    private val okHttpClient = OkHttpClient.Builder().build()
    private val api: EinoApi = Retrofit.Builder()
        .baseUrl(BuildConfig.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(EinoApi::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val originalResponse = chain.proceed(chain.request())
            if (originalResponse.isSuccessful) return originalResponse

            if (getError(originalResponse).name != "authorization_error") return originalResponse

            var newResponse: Response? = null
            runBlocking {
                if (!shouldGetNewAccessToken()) return@runBlocking originalResponse

                val refreshToken = dataStoreManager.getRefreshToken()
                val newAccessToken = getNewAccessToken(refreshToken)
                if (newAccessToken?.isBlank() == true) return@runBlocking originalResponse

                val newRequest = originalResponse.request.newBuilder().build()
                newResponse = chain.proceed(newRequest)
            }

            newResponse ?: originalResponse
        } catch (e: Exception) {
            buildErrorResponse(chain.request(), e)
        }
    }

    private fun getError(response: Response): ApiError {
        return try {
            val body = response.peekBody(2048).string()
            val apiError =
                moshi.adapter(ApiErrorResponse::class.java).lenient().fromJson(body)!!.errors[0]
            apiError
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            ApiError("Error", e.message ?: e.localizedMessage)
        }

    }

    private suspend fun shouldGetNewAccessToken(): Boolean {
        return try {
            val accessToken = dataStoreManager.getAccessToken()
            val refreshToken = dataStoreManager.getRefreshToken()
            isJWTExpired(accessToken) && isJWTNotExpired(refreshToken)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }
    }

    private suspend fun getNewAccessToken(refreshToken: String): String? {
        return try {
            val request = RefreshTokenRequest(refreshToken)
            val token = api.refreshToken(request).accessToken
            dataStoreManager.setAccessToken(token)
            token
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }

}