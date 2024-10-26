package com.faviansa.storyapp.data

import com.faviansa.storyapp.data.remote.retrofit.story.StoryApiService
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(private val apiService: StoryApiService) {
    fun getAllStories(page: Int, size: Int, location: Int) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories(page, size, location)
            when {
                response.isSuccessful -> {
                    val data = response.body()
                    if (data != null) {
                        emit(Result.Success(data))
                    } else {
                        emit(Result.Error("Response body is null"))
                    }
                }

                else -> emit(
                    Result.Error(
                        response.errorBody()?.string() ?: "Unknown error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun createNewStory(description: RequestBody, photo: MultipartBody.Part) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.createNewStory(description, photo)
            when {
                response.isSuccessful -> {
                    val data = response.body()
                    if (data != null) {
                        emit(Result.Success(data))
                    } else {
                        emit(Result.Error("Response body is null"))
                    }
                }

                else -> emit(
                    Result.Error(
                        response.errorBody()?.string() ?: "Unknown error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getStoryById(id: String) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getStoryById(id)
            when {
                response.isSuccessful -> {
                    val data = response.body()
                    if (data != null) {
                        emit(Result.Success(data))
                    } else {
                        emit(Result.Error("Response body is null"))
                    }
                }

                else -> emit(
                    Result.Error(
                        response.errorBody()?.string() ?: "Unknown error occurred"
                    )
                )
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: StoryApiService,
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService).also { instance = it }
        }
    }
}