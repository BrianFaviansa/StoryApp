package com.faviansa.storyapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "storyapp_preferences")

class StoryAppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val LANGUAGE_KEY = stringPreferencesKey("language_setting")
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val NAME_KEY = stringPreferencesKey("name")


    suspend fun saveToken(token: String, name: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[NAME_KEY] = name
        }
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