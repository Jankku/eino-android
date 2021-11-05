package com.jankku.eino.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.jankku.eino.util.Constant.DATASTORE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val dataStore = context.dataStore

    suspend fun getAccessToken(): String =
        dataStore.data.first()[ACCESS_TOKEN] ?: throw Exception("No access token found")

    suspend fun getRefreshToken(): String =
        dataStore.data.first()[REFRESH_TOKEN] ?: throw Exception("No refresh token found")

    suspend fun setTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = "Bearer $accessToken"
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

    suspend fun setExpirationTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[EXPIRATION_TIME] = time
        }
    }

    suspend fun clear() = dataStore.edit { it.clear() }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USERNAME = stringPreferencesKey("username")
        val EXPIRATION_TIME = longPreferencesKey("expiration_time")
    }
}

fun <V> DataStore<Preferences>.getValueFlow(
    key: Preferences.Key<V>,
    value: V
): Flow<V> {
    return this.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[key] ?: value
        }
}

suspend fun <Value> DataStore<Preferences>.setValue(key: Preferences.Key<Value>, value: Value) {
    this.edit { preferences ->
        preferences[key] = value
    }
}