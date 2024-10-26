package com.faviansa.storyapp.widget

import androidx.lifecycle.ViewModel
import com.faviansa.storyapp.data.StoryRepository

class WidgetViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun getStories(page: Int, pageSize: Int, location: Int) = storyRepository.getAllStories(page, pageSize, location)
}

