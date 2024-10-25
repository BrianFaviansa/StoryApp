package com.faviansa.storyapp.data.di

import android.content.Context
import com.faviansa.storyapp.data.AuthRepository
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.data.remote.retrofit.auth.AuthApiConfig
import com.faviansa.storyapp.data.remote.retrofit.story.StoryApiConfig
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val authApiService = AuthApiConfig.getApiService()
        return AuthRepository.getInstance(authApiService)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val preferences = StoryAppPreferences.getInstance(context.dataStore)
        val userToken = runBlocking { preferences.getToken() }
        val storyApiService = StoryApiConfig.getApiService(userToken.toString())
        return StoryRepository.getInstance(storyApiService)
    }
}