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
    }

    val selectedMonth: Flow<YearMonth?> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_MONTH]?.let { YearMonth.parse(it) }
    }

    val syncedMonths: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SYNCED_MONTHS] ?: emptySet()
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
}
