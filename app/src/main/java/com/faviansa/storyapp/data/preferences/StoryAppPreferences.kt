package com.faviansa.storyapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "storyapp_preferences")

class StoryAppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun saveToken(token: Any) {

    }

    companion object {
        @Volatile
        private var instance: StoryAppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): StoryAppPreferences =
            instance ?: synchronized(this) {
                instance ?: StoryAppPreferences(dataStore).also { instance = it }
            }
    }
}