package com.faviansa.storyapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "storyapp_preferences")

class StoryAppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val NAME_KEY = stringPreferencesKey("name")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

    suspend fun saveToken(token: String, name: String, userId: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[NAME_KEY] = name
            preferences[USER_ID_KEY] = userId
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    fun getToken(): Flow<String> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY] ?: ""
    }

    fun getName(): Flow<String> = dataStore.data.map { preferences ->
        preferences[NAME_KEY] ?: ""
    }

    fun getUserId(): Flow<String> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY] ?: ""
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(NAME_KEY)
            preferences.remove(USER_ID_KEY)
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }

    fun isLoggedIn(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryAppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): StoryAppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryAppPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}