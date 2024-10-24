package com.faviansa.storyapp.views.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.faviansa.storyapp.data.AuthRepository
import com.faviansa.storyapp.data.di.Injection
import com.faviansa.storyapp.data.preferences.StoryAppPreferences

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val preferences: StoryAppPreferences,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null

        fun getInstance(
            context: Context,
            preferences: StoryAppPreferences,
        ): AuthViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: AuthViewModelFactory(
                    Injection.provideAuthRepository(context),
                    preferences
                )
            }
        }
    }
}