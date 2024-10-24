package com.faviansa.storyapp.views.auth

import androidx.lifecycle.ViewModel
import com.faviansa.storyapp.data.AuthRepository
import com.faviansa.storyapp.data.preferences.StoryAppPreferences

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferences: StoryAppPreferences
) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        authRepository.register(name, email, password)

    fun login(email: String, password: String) = authRepository.login(email, password)
}