package com.faviansa.storyapp.data.di

import android.content.Context
import com.faviansa.storyapp.data.AuthRepository
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.data.local.room.StoryDatabase
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.data.remote.retrofit.auth.AuthApiConfig
import com.faviansa.storyapp.data.remote.retrofit.story.StoryApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val authApiService = AuthApiConfig.getApiService()
        return AuthRepository.getInstance(authApiService)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val userToken = provideUserToken(context)
        val storyApiService = StoryApiConfig.getApiService(userToken)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository(database, storyApiService)
    }
    fun providePreferences(context: Context): StoryAppPreferences {
        return StoryAppPreferences.getInstance(context.dataStore)
    }
    fun provideUserToken(context: Context): String {
        val preferences = StoryAppPreferences.getInstance(context.dataStore)
        val userToken: String = runBlocking { preferences.getToken().first() }
        return userToken
    }
}