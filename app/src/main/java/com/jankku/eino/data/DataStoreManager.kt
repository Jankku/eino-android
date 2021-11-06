package com.jankku.eino.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jankku.eino.util.Constant.DATASTORE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val dataStore = context.dataStore

    suspend fun getAccessToken(): String =
        dataStore.data.first()[ACCESS_TOKEN] ?: throw Exception("No access token found")

    suspend fun setAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = "Bearer $accessToken"
        }
    }

    suspend fun getRefreshToken(): String =
        dataStore.data.first()[REFRESH_TOKEN] ?: throw Exception("No refresh token found")

    suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getUsername(): String =
        dataStore.data.first()[USERNAME] ?: throw Exception("No username found")

    suspend fun setUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    suspend fun getAccessTokenExpirationTime(): Long =
        dataStore.data.first()[ACCESS_TOKEN_EXPIRATION_TIME]
            ?: throw Exception("No expiration time found")

    suspend fun setAccessTokenExpirationTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_EXPIRATION_TIME] = time
        }
    }

    suspend fun getRefreshTokenExpirationTime(): Long =
        dataStore.data.first()[REFRESH_TOKEN_EXPIRATION_TIME]
            ?: throw Exception("No expiration time found")

    suspend fun setRefreshTokenExpirationTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_EXPIRATION_TIME] = time
        }
    }

    suspend fun clear() = dataStore.edit { it.clear() }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USERNAME = stringPreferencesKey("username")
        val ACCESS_TOKEN_EXPIRATION_TIME = longPreferencesKey("access_token_expiration_time")
        val REFRESH_TOKEN_EXPIRATION_TIME = longPreferencesKey("refresh_token_expiration_time")
    }
}