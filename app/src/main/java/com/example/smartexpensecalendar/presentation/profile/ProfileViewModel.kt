package com.example.smartexpensecalendar.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.data.local.DataStoreManager
import com.example.smartexpensecalendar.data.local.UserProfile
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: UserProfile = UserProfile("Guest User", "Offline", "LOCAL"),
    val currencySymbol: String = "₹",
    val autoSyncEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val backupStatus: BackupStatus = BackupStatus.Idle
)

sealed class BackupStatus {
    object Idle : BackupStatus()
    object InProgress : BackupStatus()
    object Success : BackupStatus()
    data class Error(val message: String) : BackupStatus()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _backupStatus = MutableStateFlow<BackupStatus>(BackupStatus.Idle)
    val backupStatus = _backupStatus.asStateFlow()

    val uiState: StateFlow<ProfileUiState> = combine(
        dataStoreManager.userProfile,
        dataStoreManager.currencySymbol,
        dataStoreManager.autoSyncEnabled,
        _backupStatus
    ) { profile, symbol, autoSync, backup ->
        ProfileUiState(profile, symbol, autoSync, backupStatus = backup)
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

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            val currentProfile = uiState.value.profile
            
            if (currentProfile.authType == "LOCAL") {
                // For guest user, clear all local data
                expenseRepository.clearAllData()
                dataStoreManager.clearSyncStatus()
            } else {
                // For logged in users, we might want to do a final sync/backup here
                // For now, we just perform the logout
            }

            // Reset profile to Guest
            dataStoreManager.saveUserProfile(UserProfile("Guest User", "Offline", "LOCAL"))
            onComplete()
        }
    }

    fun performBackupBeforeLogout(onComplete: () -> Unit) {
        viewModelScope.launch {
            _backupStatus.value = BackupStatus.InProgress
            try {
                // Simulate backup logic for now
                kotlinx.coroutines.delay(2000)
                _backupStatus.value = BackupStatus.Success
                logout(onComplete)
            } catch (e: Exception) {
                _backupStatus.value = BackupStatus.Error(e.message ?: "Backup failed")
            }
        }
    }
}
