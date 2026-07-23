package com.example.smartexpensecalendar.new_sms_engine.developer.presentation

import com.example.smartexpensecalendar.new_sms_engine.developer.model.DeveloperSmsResult

data class DeveloperDashboardUiState(

    val sms: List<DeveloperSmsResult> = emptyList(),

    val filteredSms: List<DeveloperSmsResult> = emptyList(),

    val groupedFilteredSms: Map<String, List<DeveloperSmsResult>> = emptyMap(),

    val selectedFilters: Set<DashboardFilter> = emptySet(),

    val summary: DashboardSummary = DashboardSummary()
) {
    // Keep for backward compatibility if needed in UI during transition
    val selectedFilter: DashboardFilter? get() = selectedFilters.firstOrNull()
}