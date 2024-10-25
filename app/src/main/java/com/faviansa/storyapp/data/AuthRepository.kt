package com.faviansa.storyapp.data

import com.faviansa.storyapp.data.remote.retrofit.auth.AuthApiService
import kotlinx.coroutines.flow.flow

class AuthRepository private constructor(
    private val apiService: AuthApiService,
) {
    fun register(name: String, email: String, password: String) = flow {
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

    fun login(email: String, password: String) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
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

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: AuthApiService,
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService).also { instance = it }
        }
    }
}