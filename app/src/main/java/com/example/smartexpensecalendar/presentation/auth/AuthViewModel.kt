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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.smartexpensecalendar.domain.repository.AuthRepository
import javax.inject.Inject
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import android.content.Context
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isChoiceMade: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TENANT_SLUG = "smart-expense"
    }

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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.login(email, password, TENANT_SLUG)
                .onSuccess { response ->
                    val user = response.data?.user
                    val tokens = response.data?.tokens

                    if (user != null && tokens != null) {

                        val profile = UserProfile(
                            name = "${user.firstName} ${user.lastName}",
                            email = user.email,
                            authType = user.authType.uppercase(),
                            subscriptionTier = com.example.smartexpensecalendar.domain.model.SubscriptionTier.FREE
                        )

                        // Save user profile
                        dataStoreManager.saveUserProfile(profile)

                        // Save JWT session
                        dataStoreManager.saveSession(
                            userId = user.id,
                            accessToken = tokens.accessToken,
                            refreshToken = tokens.refreshToken
                        )

                        dataStoreManager.setOnboardingCompleted(true)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isChoiceMade = true
                            )
                        }

                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "User or token data missing"
                            )
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Login failed") }
                }
        }
    }

    fun continueWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val credentialManager = CredentialManager.create(context)
            val serverClientId = "175250766905-2n02q395h6e5ch1qh3hd3fur8u5hug0r.apps.googleusercontent.com"
            
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val idToken = googleIdTokenCredential.idToken

                if (idToken.isNotBlank()) {
                    authRepository.googleLogin(
                        id_token = idToken,
                        tenantSlug = TENANT_SLUG
                    )
                        .onSuccess { response ->
                            // Use response user data if available, else fallback to google credential info
                            val user = response.data?.user
                            val profile = if (user != null) {
                                UserProfile(
                                    name = "${user.firstName} ${user.lastName}",
                                    email = user.email,
                                    authType = "GOOGLE",
                                    subscriptionTier = com.example.smartexpensecalendar.domain.model.SubscriptionTier.FREE
                                )
                            } else {
                                UserProfile(
                                    name = googleIdTokenCredential.displayName ?: "Google User",
                                    email = googleIdTokenCredential.id,
                                    authType = "GOOGLE"
                                )
                            }
                            dataStoreManager.saveUserProfile(profile)
                            dataStoreManager.setOnboardingCompleted(true)
                            _uiState.update { it.copy(isLoading = false, isChoiceMade = true) }
                        }
                        .onFailure { e ->
                            _uiState.update { it.copy(isLoading = false, error = "Backend Google Auth failed: ${e.message}") }
                        }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to get Google Auth Code") }
                }

            } catch (e: GetCredentialException) {
                _uiState.update { it.copy(isLoading = false, error = "Google Sign-In failed: ${e.message}") }
            }
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
