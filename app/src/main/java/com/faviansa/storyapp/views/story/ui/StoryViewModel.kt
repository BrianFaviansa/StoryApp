package com.faviansa.storyapp.views.story.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.data.StoryRepository
import com.faviansa.storyapp.data.remote.response.story.DetailStoryResponse
import com.faviansa.storyapp.data.remote.response.story.ListStoryItem
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<Result<List<ListStoryItem>>>()
    val stories: LiveData<Result<List<ListStoryItem>>> = _stories

    private val _story = MutableLiveData<Result<DetailStoryResponse>>()
    val story: LiveData<Result<DetailStoryResponse>> = _story

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

    fun createNewStory(description: RequestBody, photo: MultipartBody.Part) {
        viewModelScope.launch {
            storyRepository.createNewStory(description, photo).collect { result ->
                when (result) {
                    is Result.Loading -> _stories.value = Result.Loading
                    is Result.Success -> _stories.value = Result.Success(emptyList())
                    is Result.Error -> _stories.value = Result.Error(result.error)
                }
            }
        }
    }

    fun getStoryById(id: String) {
        viewModelScope.launch {
            storyRepository.getStoryById(id).collect { result ->
                when (result) {
                    is Result.Loading -> _story.value = Result.Loading
                    is Result.Success -> _story.value = Result.Success(result.data)
                    is Result.Error -> _story.value = Result.Error(result.error)
                }
            }
        }
    }
}