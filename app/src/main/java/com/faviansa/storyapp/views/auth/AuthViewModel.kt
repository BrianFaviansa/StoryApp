package com.faviansa.storyapp.views.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faviansa.storyapp.data.AuthRepository
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.remote.response.auth.LoginResponse
import com.faviansa.storyapp.data.remote.response.auth.RegisterResponse
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferences: StoryAppPreferences,
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Result<LoginResponse>>()
    val loginResponse: LiveData<Result<LoginResponse>> = _loginResponse

    private val _registerResponse = MutableLiveData<Result<RegisterResponse>>()
    val registerResponse: LiveData<Result<RegisterResponse>> = _registerResponse

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { result ->
                _loginResponse.value = result
                if (result is Result.Success) {
                    result.data.loginResult?.let { loginResult ->
                        preferences.saveToken(
                            token = loginResult.token ?: "",
                            name = loginResult.name ?: "",
                            userId = loginResult.userId ?: ""
                        )
                    }
                }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(name, email, password).collect { result ->
                _registerResponse.value = result
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferences.clearToken()
        }
    }
}