package com.example.smartexpensecalendar.presentation.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensecalendar.domain.model.DefaultCategories
import com.example.smartexpensecalendar.domain.model.MerchantMapping
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import com.example.smartexpensecalendar.sms.SMSCategorizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RuleSource {
    SYSTEM,   // Built-in mapping
    CUSTOM,   // Specifically created by user
    ACTIVE    // Found in SMS but no mapping exists yet
}

data class MerchantRule(
    val keyword: String,
    val category: String,
    val source: RuleSource,
    val frequency: Int = 0
)

@HiltViewModel
class MerchantRulesViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val categorizer: SMSCategorizer
) : ViewModel() {

    val rules: StateFlow<List<MerchantRule>> = combine(
        repository.getAllMerchantMappings(),
        repository.getActiveMerchantStats()
    ) { custom, active ->
        val systemRules = com.example.smartexpensecalendar.sms.config.MerchantRegistry.merchants.map { 
            MerchantRule(it.canonicalName, it.category, RuleSource.SYSTEM) 
        }
        
        val customRules = custom.map { 
            MerchantRule(it.merchantKeyword, it.category, RuleSource.CUSTOM) 
        }
        
        val activeMerchants = active.map { 
            MerchantRule(it.merchant, it.category, RuleSource.ACTIVE, it.frequency) 
        }
        
        val mappedKeywords = (customRules + systemRules).map { it.keyword.lowercase() }.toSet()
        
        val unmappedActive = activeMerchants.filter { it.keyword.lowercase() !in mappedKeywords }
        
        (customRules + systemRules + unmappedActive)
            .sortedWith(compareByDescending<MerchantRule> { it.frequency }
                .thenBy { it.source }
                .thenBy { it.keyword })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = repository.getCustomCategories()
        .map { custom -> DefaultCategories.list + custom }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultCategories.list)

    val unmappedMerchants: StateFlow<List<String>> = combine(
        repository.getActiveMerchantStats(),
        repository.getAllMerchantMappings()
    ) { active, rules ->
        val mappedKeywords = rules.map { it.merchantKeyword.lowercase() }.toSet()
        active.map { it.merchant }
            .filter { it.lowercase() !in mappedKeywords }
            .distinct()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteRule(rule: MerchantRule) {
        if (rule.source == RuleSource.CUSTOM) {
            viewModelScope.launch {
                repository.deleteMerchantMapping(MerchantMapping(rule.keyword.lowercase(), rule.category))
            }
        }
    }

    fun updateRule(keyword: String, newCategory: String) {
        viewModelScope.launch {
            repository.saveMerchantMapping(MerchantMapping(keyword.lowercase(), newCategory))
        }
    }

    fun addCustomCategory(name: String) {
        viewModelScope.launch {
            repository.addCustomCategory(name)
        }
    }
}
