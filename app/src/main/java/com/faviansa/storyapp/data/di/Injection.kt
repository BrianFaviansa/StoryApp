package com.faviansa.storyapp.data.di

import android.content.Context
import com.faviansa.storyapp.data.AuthRepository
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.data.remote.retrofit.auth.AuthApiConfig
import com.faviansa.storyapp.data.remote.retrofit.story.StoryApiConfig

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = AuthApiConfig.getApiService()
        val preferences = StoryAppPreferences.getInstance(context.dataStore)
        return AuthRepository.getInstance(apiService, preferences)
    }
    fun provideStoryRepository(context: Context, token: String): StoryRepository {
        val apiService = StoryApiConfig.getApiService(token)
        return StoryRepository.getInstance(apiService)
    }
}