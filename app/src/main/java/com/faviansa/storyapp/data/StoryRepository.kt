package com.faviansa.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.faviansa.storyapp.data.local.remotemediator.StoryRemoteMediator
import com.faviansa.storyapp.data.local.room.StoryDatabase
import com.faviansa.storyapp.data.remote.response.story.ListStoryItem
import com.faviansa.storyapp.data.remote.retrofit.story.StoryApiService

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: StoryApiService,
) {
    fun getAllStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        ).liveData
    }
}