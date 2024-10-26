package com.faviansa.storyapp.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.data.preferences.StoryAppPreferences

class WidgetViewModelFactory(
    private val storyRepository: StoryRepository,
    private val preferences: StoryAppPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WidgetViewModel::class.java)) {
            return WidgetViewModel(storyRepository, preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}