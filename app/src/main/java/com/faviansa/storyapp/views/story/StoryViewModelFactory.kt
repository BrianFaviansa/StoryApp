package com.faviansa.storyapp.views.story

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.data.di.Injection
import com.faviansa.storyapp.views.story.ui.StoryViewModel

@Suppress("UNCHECKED_CAST")
class StoryViewModelFactory(
    private val storyRepository: StoryRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null

        fun getInstance(
            context: Context,
        ): StoryViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: StoryViewModelFactory(
                    Injection.provideStoryRepository(context),
                )
            }
        }
    }
}