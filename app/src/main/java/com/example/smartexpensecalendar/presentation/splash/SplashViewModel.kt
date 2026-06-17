package com.example.smartexpensecalendar.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.data.local.DataStoreManager
import com.example.smartexpensecalendar.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent = _navigationEvent.asStateFlow()

    fun checkDestination() {
        viewModelScope.launch {
            val hasCompletedOnboarding = dataStoreManager.hasCompletedOnboarding.first()
            if (hasCompletedOnboarding) {
                _navigationEvent.value = Screen.Home.route
            } else {
                _navigationEvent.value = Screen.Auth.route
            }
        }
    }
}
