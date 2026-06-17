package com.example.smartexpensecalendar.features.beta_audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import com.example.smartexpensecalendar.features.developer_tools.data.SMSAnalysisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BetaAuditViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val smsAnalysisRepository: SMSAnalysisRepository,
    private val betaAuditExporter: BetaAuditExporter
) : ViewModel() {

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus = _exportStatus.asStateFlow()

    fun runBetaAudit() {
        viewModelScope.launch {
            _exportStatus.value = "Exporting Audit Package..."
            val result = betaAuditExporter.exportAuditPackage(
                expenseRepository,
                smsAnalysisRepository
            )
            _exportStatus.value = result
        }
    }
    
    fun clearStatus() {
        _exportStatus.value = null
    }
}
