package com.faviansa.storyapp.data

import com.faviansa.storyapp.data.remote.retrofit.story.StoryApiService
import kotlinx.coroutines.flow.flow

class StoryRepository private constructor(private val apiService: StoryApiService) {
    fun getAllStories() = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories(1, 10, 1)
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
        private var instance: StoryRepository? = null

        fun getInstance(apiService: StoryApiService): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService).also { instance = it }
            }
        }
    }
}