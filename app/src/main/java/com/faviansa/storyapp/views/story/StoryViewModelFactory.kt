package com.faviansa.storyapp.views.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.views.story.ui.StoryViewModel

class StoryViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null

        fun getInstance(storyRepository: StoryRepository): StoryViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: StoryViewModelFactory(storyRepository)
            }
        }
    }
}