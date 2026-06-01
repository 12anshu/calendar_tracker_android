package com.example.smartexpensecalendar.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val SELECTED_MONTH = stringPreferencesKey("selected_month")
        val SYNCED_MONTHS = stringSetPreferencesKey("synced_months")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val AUTH_TYPE = stringPreferencesKey("auth_type") // "GOOGLE", "EMAIL", "LOCAL"
        val AUTO_SYNC_ENABLED = androidx.datastore.preferences.core.booleanPreferencesKey("auto_sync_enabled")
        val HAS_COMPLETED_ONBOARDING = androidx.datastore.preferences.core.booleanPreferencesKey("has_completed_onboarding")
    }

    val selectedMonth: Flow<YearMonth?> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_MONTH]?.let { YearMonth.parse(it) }
    }

    val syncedMonths: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SYNCED_MONTHS] ?: emptySet()
    }

    val currencySymbol: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENCY_SYMBOL] ?: "₹"
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            name = preferences[USER_NAME] ?: "Guest User",
            email = preferences[USER_EMAIL] ?: "",
            authType = preferences[AUTH_TYPE] ?: "LOCAL"
        )
    }

    val autoSyncEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_SYNC_ENABLED] ?: false
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[HAS_COMPLETED_ONBOARDING] ?: false
    }

    suspend fun saveSelectedMonth(yearMonth: YearMonth) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_MONTH] = yearMonth.toString()
        }
    }

    suspend fun markMonthAsSynced(yearMonth: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[SYNCED_MONTHS] ?: emptySet()
            preferences[SYNCED_MONTHS] = current + yearMonth
        }
    }

    suspend fun setCurrencySymbol(symbol: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_SYMBOL] = symbol
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = profile.name
            preferences[USER_EMAIL] = profile.email
            preferences[AUTH_TYPE] = profile.authType
        }
    }

    suspend fun setAutoSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_SYNC_ENABLED] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun clearSyncStatus() {
        context.dataStore.edit { preferences ->
            preferences.remove(SYNCED_MONTHS)
        }
    }

    suspend fun clearSyncStatusForMonth(yearMonth: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[SYNCED_MONTHS] ?: emptySet()
            preferences[SYNCED_MONTHS] = current - yearMonth
        }
    }
}

data class UserProfile(
    val name: String,
    val email: String,
    val authType: String
)
