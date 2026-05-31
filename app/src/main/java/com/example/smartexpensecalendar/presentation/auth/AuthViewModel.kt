package com.example.smartexpensecalendar.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.data.local.DataStoreManager
import com.example.smartexpensecalendar.data.local.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isChoiceMade: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // We moved the check to AuthScreen's LaunchedEffect to support "forceShow"
    }

    fun checkInitialChoice() {
        viewModelScope.launch {
            val completed = dataStoreManager.hasCompletedOnboarding.first()
            if (completed) {
                _uiState.value = AuthUiState(isChoiceMade = true)
            }
        }
    }

    fun continueWithGoogle() {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            // DEVELOPMENT MOCK:
            // This is a placeholder profile. To enable real Google Login:
            // 1. Setup Firebase Project and add 'google-services.json'.
            // 2. Use Credential Manager API or Google Sign-In SDK.
            val mockProfile = UserProfile(
                name = "Demo User (Google Mock)",
                email = "demo.google@example.com",
                authType = "GOOGLE"
            )
            dataStoreManager.saveUserProfile(mockProfile)
            dataStoreManager.setOnboardingCompleted(true)
            _uiState.value = AuthUiState(isChoiceMade = true)
        }
    }

    fun signUpWithEmail(name: String, email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val profile = UserProfile(
                name = name,
                email = email,
                authType = "EMAIL"
            )
            dataStoreManager.saveUserProfile(profile)
            dataStoreManager.setOnboardingCompleted(true)
            _uiState.value = AuthUiState(isChoiceMade = true)
        }
    }

    fun skipAuth() {
        viewModelScope.launch {
            val profile = UserProfile(
                name = "Guest User",
                email = "Offline Mode",
                authType = "LOCAL"
            )
            dataStoreManager.saveUserProfile(profile)
            dataStoreManager.setOnboardingCompleted(true)
            _uiState.value = AuthUiState(isChoiceMade = true)
        }
    }
}
