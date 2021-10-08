package com.jankku.eino.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
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

class DataStoreManager @Inject constructor(@ApplicationContext private val appContext: Context) {
    private val dataStore = appContext.dataStore

    suspend fun getString(key: Preferences.Key<String>): String? {
        val preferences: Preferences = dataStore.data.first()
        return preferences[key]
    }

    suspend fun putString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { settings ->
            settings[key] = value
        }
    }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
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