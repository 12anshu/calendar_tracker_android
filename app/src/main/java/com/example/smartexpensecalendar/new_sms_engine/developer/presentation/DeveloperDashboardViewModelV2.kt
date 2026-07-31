package com.example.smartexpensecalendar.new_sms_engine.developer.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.new_sms_engine.developer.DeveloperSmsProviderV2
import com.example.smartexpensecalendar.new_sms_engine.developer.model.DeveloperSmsResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode
import com.example.smartexpensecalendar.new_sms_engine.developer.export.AnalyzerExportRow
import com.example.smartexpensecalendar.new_sms_engine.developer.export.CsvExporter
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

import java.text.SimpleDateFormat
import java.util.Locale

sealed class DeveloperDashboardEffect {
    data class ExportCsv(val content: String, val fileName: String) : DeveloperDashboardEffect()
}

@HiltViewModel
class DeveloperDashboardViewModelV2 @Inject constructor(
    private val smsProvider: DeveloperSmsProviderV2
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeveloperDashboardUiState())
    val uiState: StateFlow<DeveloperDashboardUiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _effect = MutableSharedFlow<DeveloperDashboardEffect>()
    val effect: SharedFlow<DeveloperDashboardEffect> = _effect.asSharedFlow()

    private var pendingCsvContent: String? = null
    private var allSms = mutableListOf<DeveloperSmsResult>()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var refreshJob: Job? = null
    private var batchCounter = 0

    init {
        loadSms()
    }

    private fun loadSms() {
        viewModelScope.launch {
            smsProvider.fetchSms(
                onBatchReady = { batch ->
                    allSms.addAll(batch)
                    batchCounter++
                    
                    // Throttled Refresh: Only refresh UI every 500 messages (5 batches) 
                    // to avoid O(N^2) complexity slowdown during initial load
                    if (batchCounter % 5 == 0) {
                        refreshUi()
                    }
                },
                onProgress = { p ->
                    _progress.value = p
                    if (p >= 1f) {
                        refreshUi() // Guaranteed final refresh
                    }
                }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        refreshUi()
    }

    fun onFilterClicked(filter: DashboardFilter) {
        val currentFilters = _uiState.value.selectedFilters.toMutableSet()
        if (currentFilters.contains(filter)) {
            currentFilters.remove(filter)
        } else {
            currentFilters.add(filter)
        }
        _uiState.value = _uiState.value.copy(selectedFilters = currentFilters)
        refreshUi()
    }

    fun clearFilter() {
        _searchQuery.value = ""
        _uiState.value = _uiState.value.copy(selectedFilters = emptySet())
        refreshUi()
    }

    fun exportToCsv() {
        viewModelScope.launch {
            val rows = allSms.map { sms ->
                AnalyzerExportRow(
                    date = java.util.Date(sms.timestamp).toString(),
                    sender = sms.sender,
                    message = sms.message,
                    qualified = sms.qualified,
                    qualificationReason = sms.qualificationReason ?: "N/A",
                    tokens = sms.tokens.joinToString(", ") { it.text },
                    tokenCategories = sms.tokens
                        .filter { it.categories.isNotEmpty() && !it.has(com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory.UNKNOWN) }
                        .joinToString("; ") { t -> "${t.text} -> ${t.categories.joinToString(",")}" },
                    matchedPatterns = sms.matchedPatterns.joinToString(", ") { it.patternName },
                    evidence = sms.evidence.joinToString("; ") { e -> "[${e.type.name}] ${e.strength} | ${e.source} | \"${e.matchedText}\"" },
                    messageType = sms.messageType.name,
                    amount = sms.amount?.let { "${it.currency.symbol}${it.amount}" } ?: "",
                    amountConfidence = "${(sms.amountConfidence * 100).toInt()}%",
                    direction = sms.direction.name,
                    directionConfidence = "${(sms.directionConfidence * 100).toInt()}%",
                    paymentMode = sms.paymentMode.name,
                    paymentModeConfidence = "${(sms.paymentModeConfidence * 100).toInt()}%",
                    merchant = sms.merchant ?: "",
                    merchantSourceSegment = sms.merchantSourceSegment,
                    merchantConfidence = "${(sms.merchantConfidence * 100).toInt()}%",
                    merchantAnchorUsed = sms.merchantAnchorUsed ?: "",
                    category = sms.category.name,
                    categoryConfidence = sms.categoryConfidence.toString(),
                    categoryEvidence = sms.categoryEvidence,
                    account = sms.account ?: "",
                    reference = sms.reference ?: "",
                    messageSegments = sms.messageSegments.joinToString(" | ") {
                        "[${it.relation}] ${it.text}"
                    }
                )
            }
            pendingCsvContent = CsvExporter.export(rows)
            _effect.emit(DeveloperDashboardEffect.ExportCsv(pendingCsvContent!!, "new_engine_full_report_${System.currentTimeMillis()}.csv"))
        }
    }

    fun writeCsvToUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            pendingCsvContent?.let { content ->
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(content.toByteArray())
                }
            }
            pendingCsvContent = null
        }
    }

    private fun refreshUi() {
        val filters = _uiState.value.selectedFilters
        val query = _searchQuery.value
        
        // Take a snapshot of the current list to avoid ConcurrentModificationException
        val currentAllSms = allSms.toList()

        refreshJob?.cancel()
        refreshJob = viewModelScope.launch(Dispatchers.Default) {
            var filteredSms = currentAllSms

            if (query.isNotBlank()) {
                val q = query.uppercase()
                filteredSms = filteredSms.filter { sms ->
                    sms.sender.uppercase().contains(q) ||
                    sms.message.uppercase().contains(q) ||
                    sms.merchant?.uppercase()?.contains(q) == true ||
                    sms.tokens.any { it.text.uppercase().contains(q) } ||
                    sms.matchedPatterns.any { it.patternName.uppercase().contains(q) } ||
                    sms.evidence.any { it.type.name.uppercase().contains(q) } ||
                    sms.tokens.flatMap { it.categories }.any { it.name.uppercase().contains(q) }
                }
            }

            if (filters.isNotEmpty()) {
                filters.forEach { filter ->
                    filteredSms = when (filter) {
                        DashboardFilter.QUALIFIED -> filteredSms.filter { it.qualified }
                        DashboardFilter.NOT_QUALIFIED -> filteredSms.filter { !it.qualified }
                        
                        is DashboardFilter.MESSAGE_TYPE -> filteredSms.filter { it.qualified && it.messageType == filter.type }
                        is DashboardFilter.EVIDENCE_TYPE -> filteredSms.filter { it.qualified && it.evidence.any { ev -> ev.type == filter.type } }
                        is DashboardFilter.PATTERN -> filteredSms.filter { it.qualified && it.matchedPatterns.any { p -> p.patternName == filter.name } }
                        is DashboardFilter.TOKEN_CATEGORY -> filteredSms.filter { it.qualified && it.tokens.any { t -> t.has(filter.category) } }

                        DashboardFilter.AMOUNT_FOUND -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.amount != null }
                        DashboardFilter.AMOUNT_MISSING -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.amount == null }
                        DashboardFilter.MERCHANT_FOUND -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && !it.merchant.isNullOrBlank() }
                        DashboardFilter.MERCHANT_MISSING -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.merchant.isNullOrBlank() }
                        DashboardFilter.DIRECTION_FOUND -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.direction != Direction.UNKNOWN }
                        DashboardFilter.DIRECTION_MISSING -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.direction == Direction.UNKNOWN }
                        DashboardFilter.MODE_FOUND -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.paymentMode != PaymentMode.UNKNOWN }
                        DashboardFilter.MODE_MISSING -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.paymentMode == PaymentMode.UNKNOWN }

                        DashboardFilter.ACCOUNT_FOUND, DashboardFilter.ACCOUNT_MISSING,
                        DashboardFilter.REFERENCE_FOUND, DashboardFilter.REFERENCE_MISSING -> filteredSms 

                        DashboardFilter.DEBIT -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.direction == Direction.DEBIT }
                        DashboardFilter.CREDIT -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.direction == Direction.CREDIT }
                        DashboardFilter.UNKNOWN_DIRECTION -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.direction == Direction.UNKNOWN }

                        is DashboardFilter.PAYMENT_MODE -> filteredSms.filter { it.qualified && it.messageType == MessageType.TRANSACTION && it.paymentMode == filter.mode }
                    }
                }
            }

            val summary = buildSummary(currentAllSms)
            
            // Explicitly sort latest first and group
            val sortedFiltered = filteredSms.sortedByDescending { it.timestamp }
            val grouped = groupSmsByMonth(sortedFiltered)

            withContext(Dispatchers.Main) {
                _uiState.update { 
                    it.copy(
                        sms = currentAllSms,
                        filteredSms = filteredSms,
                        groupedFilteredSms = grouped,
                        summary = summary
                    )
                }
            }
        }
    }

    private fun groupSmsByMonth(sms: List<DeveloperSmsResult>): Map<String, List<DeveloperSmsResult>> {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return sms.groupBy { dateFormat.format(java.util.Date(it.timestamp)) }
    }

    private fun buildSummary(
        sms: List<DeveloperSmsResult>
    ): DashboardSummary {

        var total = 0
        var qualified = 0
        var transaction = 0
        var obligation = 0
        var information = 0
        var unknownType = 0
        
        var amountFound = 0
        var merchantFound = 0
        var directionFound = 0
        var modeFound = 0
        
        var debit = 0
        var credit = 0
        var unknownDir = 0
        
        val modeCounts = mutableMapOf<String, Int>()
        val evidenceCounts = mutableMapOf<String, Int>()
        val patternCounts = mutableMapOf<String, Int>()
        val tokenCategoryCounts = mutableMapOf<String, Int>()

        sms.forEach { item ->
            total++
            if (item.qualified) {
                qualified++
                
                when (item.messageType) {
                    MessageType.TRANSACTION -> {
                        transaction++
                        if (item.amount != null) amountFound++
                        if (!item.merchant.isNullOrBlank()) merchantFound++
                        if (item.direction != Direction.UNKNOWN) directionFound++
                        if (item.paymentMode != PaymentMode.UNKNOWN) modeFound++
                        
                        when (item.direction) {
                            Direction.DEBIT -> debit++
                            Direction.CREDIT -> credit++
                            Direction.UNKNOWN -> unknownDir++
                        }
                        
                        val modeName = item.paymentMode.name
                        modeCounts[modeName] = (modeCounts[modeName] ?: 0) + 1
                    }
                    MessageType.OBLIGATION -> obligation++
                    MessageType.INFORMATION -> information++
                    MessageType.UNKNOWN -> unknownType++
                }
                
                item.evidence.forEach { ev ->
                    val name = ev.type.name
                    evidenceCounts[name] = (evidenceCounts[name] ?: 0) + 1
                }
                
                item.matchedPatterns.forEach { p ->
                    patternCounts[p.patternName] = (patternCounts[p.patternName] ?: 0) + 1
                }
                
                item.tokens.forEach { t ->
                    t.categories.forEach { cat ->
                        tokenCategoryCounts[cat.name] = (tokenCategoryCounts[cat.name] ?: 0) + 1
                    }
                }
            }
        }

        return DashboardSummary(
            totalSms = total,
            qualified = qualified,
            notQualified = total - qualified,

            transactionCount = transaction,
            obligationCount = obligation,
            informationCount = information,
            unknownTypeCount = unknownType,

            evidenceStats = evidenceCounts,
            patternStats = patternCounts,
            tokenCategoryStats = tokenCategoryCounts,

            amountFound = amountFound,
            amountMissing = transaction - amountFound,
            merchantFound = merchantFound,
            merchantMissing = transaction - merchantFound,
            directionFound = directionFound,
            directionMissing = transaction - directionFound,
            modeFound = modeFound,
            modeMissing = transaction - modeFound,

            debit = debit,
            credit = credit,
            unknownDirection = unknownDir,

            paymentMode = modeCounts
        )
    }
}
