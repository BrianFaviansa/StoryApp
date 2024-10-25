package com.faviansa.storyapp.data.remote.retrofit.story

import com.faviansa.storyapp.data.remote.response.story.GetAllStoriesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StoryApiService {
    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : Response<GetAllStoriesResponse>
}