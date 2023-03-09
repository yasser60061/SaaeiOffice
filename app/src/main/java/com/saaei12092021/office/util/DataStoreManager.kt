package com.saaei12092021.office.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(content: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("office_pref")
    private val mDataStore: DataStore<Preferences> = content.dataStore

    companion object {
        val TOKEN = stringPreferencesKey("TOKEN")
    }

    suspend fun saveToken(tokenValue: String) {
        mDataStore.edit { preferences ->
            preferences[TOKEN] = tokenValue
        }
    }

    val readToken : Flow<String> = mDataStore.data.map {
        it[TOKEN] ?: ""
    }

}