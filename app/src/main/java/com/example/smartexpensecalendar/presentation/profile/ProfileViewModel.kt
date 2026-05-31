package com.example.smartexpensecalendar.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.data.local.DataStoreManager
import com.example.smartexpensecalendar.data.local.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: UserProfile = UserProfile("Guest User", "Offline", "LOCAL"),
    val currencySymbol: String = "₹",
    val autoSyncEnabled: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        dataStoreManager.userProfile,
        dataStoreManager.currencySymbol,
        dataStoreManager.autoSyncEnabled
    ) { profile, symbol, autoSync ->
        ProfileUiState(profile, symbol, autoSync)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun updateCurrency(symbol: String) {
        viewModelScope.launch {
            dataStoreManager.setCurrencySymbol(symbol)
        }
    }

    fun toggleAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setAutoSyncEnabled(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.saveUserProfile(UserProfile("Guest User", "Offline", "LOCAL"))
        }
    }
}
