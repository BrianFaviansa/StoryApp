package com.faviansa.storyapp.views.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faviansa.storyapp.data.AuthRepository
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.remote.response.auth.LoginResponse
import com.faviansa.storyapp.data.remote.response.auth.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferences: StoryAppPreferences,
) : ViewModel() {

    private val _loginResponse =
        MutableStateFlow<Result<LoginResponse>>(Result.Loading)
    val loginResponse = _loginResponse.asStateFlow()

    private val _registerResponse =
        MutableStateFlow<Result<RegisterResponse>>(Result.Loading)
    val registerResponse = _registerResponse.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password)
                .collect { result ->
                    _loginResponse.value = result
                }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(name, email, password)
                .collect { result ->
                    _registerResponse.value = result
                }
        }
    }
}