package com.faviansa.storyapp.views.story.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.data.remote.response.story.ListStoryItem
import kotlinx.coroutines.launch

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<Result<List<ListStoryItem>>>()
    val stories: LiveData<Result<List<ListStoryItem>>> = _stories

    fun getAllStories(page: Int, size: Int, location: Int) {
        viewModelScope.launch {
            storyRepository.getAllStories(page, size, location).collect { result ->
                when (result) {
                    is Result.Loading -> _stories.value = Result.Loading
                    is Result.Success -> {
                        val storyList = result.data.listStory?.filterNotNull() ?: emptyList()
                        _stories.value = Result.Success(storyList)
                    }
                    is Result.Error -> _stories.value = Result.Error(result.error)
                }
            }
        }
    }
}