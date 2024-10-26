package com.faviansa.storyapp.widget

import androidx.lifecycle.ViewModel
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.data.preferences.StoryAppPreferences

class WidgetViewModel(
    private val storyRepository: StoryRepository,
    private val preferences: StoryAppPreferences
) : ViewModel() {

    fun getStories(page: Int, pageSize: Int, location: Int) = storyRepository.getAllStories(page, pageSize, location)
}

