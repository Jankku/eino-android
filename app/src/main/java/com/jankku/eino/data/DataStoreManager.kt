package com.jankku.eino.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jankku.eino.util.Constant.DATASTORE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val dataStore = context.dataStore

    suspend fun tokensExist(): Boolean {
        return try {
            val accessTokenExists = dataStore.data.first()[ACCESS_TOKEN] != null
            val refreshTokenExists = dataStore.data.first()[REFRESH_TOKEN] != null
            accessTokenExists && refreshTokenExists
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAccessToken(): String =
        dataStore.data.first()[ACCESS_TOKEN] ?: throw Exception("No access token found")

    suspend fun setAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun getRefreshToken(): String =
        dataStore.data.first()[REFRESH_TOKEN] ?: throw Exception("No refresh token found")

    suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clear() = dataStore.edit { it.clear() }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}