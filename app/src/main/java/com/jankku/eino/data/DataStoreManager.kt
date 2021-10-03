package com.jankku.eino.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.jankku.eino.util.Constant.DATASTORE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

class DataStoreManager @Inject constructor(@ApplicationContext private val appContext: Context) {
    private val dataStore = appContext.dataStore
}