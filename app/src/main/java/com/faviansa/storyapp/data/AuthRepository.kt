package com.faviansa.storyapp.data

import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.remote.response.auth.LoginResponse
import com.faviansa.storyapp.data.remote.response.auth.RegisterResponse
import com.faviansa.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val preferences: StoryAppPreferences,
) {
    fun register(name: String, email: String, password: String): Flow<Result<RegisterResponse>> =
        flow {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                when {
                    response.isSuccessful -> {
                        response.body()?.let { data ->
                            emit(Result.Success(data))
                        }
                    }
                    else -> emit(Result.Error(response.errorBody()?.string() ?: "Unknown error occurred"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "Unknown error occurred"))
            }
        }

    fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            when {
                response.isSuccessful -> {
                    val data = response.body()?.loginResult
                    if (data != null) {
                        preferences.saveToken(data.token!!, data.name!!)
                    }
                    emit(Result.Success(response.body()!!))
                }
                else -> emit(Result.Error(response.errorBody()?.string() ?: "Unknown error occurred"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            preferences: StoryAppPreferences,
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService, preferences).also { instance = it }
        }
    }
}