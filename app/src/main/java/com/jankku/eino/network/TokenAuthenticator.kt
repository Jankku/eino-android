package com.jankku.eino.network

import android.util.Log
import com.jankku.eino.BuildConfig
import com.jankku.eino.data.DataStoreManager
import com.jankku.eino.network.request.RefreshTokenRequest
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

private const val TAG = "TokenAuthenticator"

class TokenAuthenticator @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    moshi: Moshi
) : Authenticator {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

    private val api: EinoApiInterface = Retrofit.Builder()
        .baseUrl(BuildConfig.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(EinoApiInterface::class.java)

    override fun authenticate(route: Route?, response: Response): Request? {
        var shouldGetNewToken = false

        runBlocking(Dispatchers.IO) {
            val accessTokenExpirationTime = dataStoreManager.getAccessTokenExpirationTime()
            val refreshTokenExpirationTime = dataStoreManager.getRefreshTokenExpirationTime()
            val currentUnixTime = System.currentTimeMillis()

            if (currentUnixTime > refreshTokenExpirationTime) return@runBlocking
            if (currentUnixTime > accessTokenExpirationTime) shouldGetNewToken = true
        }

        if (!shouldGetNewToken) return null

        var newRequest: Request?

        runBlocking(Dispatchers.IO) {
            val updatedToken = getNewAccessToken(dataStoreManager.getRefreshToken())

            newRequest = updatedToken?.let {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $it")
                    .build()
            }
        }

        return newRequest
    }

    private suspend fun getNewAccessToken(refreshToken: String): String? {
        var newAccessToken: String? = null
        try {
            val request = RefreshTokenRequest(refreshToken)
            newAccessToken = api.refreshToken(request).accessToken
            dataStoreManager.setAccessToken(newAccessToken)
        } catch (e: Exception) {
            Log.d(TAG, "getNewAccessToken: $e")
        }

        return newAccessToken
    }
}