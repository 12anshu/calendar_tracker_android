package com.example.smartexpensecalendar.new_sms_engine.common.merchant.repository

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantDefinition

class DefaultMerchantRepository : MerchantRepository {

    override fun findMerchant(
        merchant: String?
    ): MerchantDefinition? {

        if (merchant.isNullOrBlank()) {
            return null
        }

        val normalized = merchant.trim().uppercase()

        return MerchantCatalog.firstOrNull { definition ->
            definition.keywords.any { keyword ->
                normalized.contains(keyword, ignoreCase = true)
            }
        }
    }
}