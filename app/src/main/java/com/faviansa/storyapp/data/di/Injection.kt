package com.faviansa.storyapp.data.di

import android.content.Context
import com.faviansa.storyapp.data.AuthRepository
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val preferences = StoryAppPreferences.getInstance(context.dataStore)
        return AuthRepository.getInstance(apiService, preferences)
    }
}