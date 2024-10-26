package com.faviansa.storyapp.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.faviansa.storyapp.data.StoryRepository

class WidgetViewModelFactory(
    private val storyRepository: StoryRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WidgetViewModel::class.java)) {
            return WidgetViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}