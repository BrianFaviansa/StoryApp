package com.faviansa.storyapp.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faviansa.storyapp.data.remote.response.story.ListStoryItem
import com.faviansa.storyapp.data.remote.retrofit.story.StoryApiConfig
import kotlinx.coroutines.withContext

class WidgetViewModel : ViewModel() {
    suspend fun getStories(
        token: String,
        page: Int,
        pageSize: Int,
        location: Int,
    ): List<ListStoryItem>? {
        return withContext(viewModelScope.coroutineContext) {
            try {
                val response =
                    StoryApiConfig.getApiService(token).getAllStories(page, pageSize, location)
                if (response.isSuccessful) {
                    response.body()?.listStory
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}



