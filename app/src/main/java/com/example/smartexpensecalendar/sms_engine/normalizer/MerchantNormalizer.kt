package com.example.smartexpensecalendar.sms_engine.normalizer

import com.example.smartexpensecalendar.sms.config.MerchantRegistry

object MerchantNormalizer {

    /**
     * Normalizes a messy merchant name to its clean canonical name using the MerchantRegistry.
     * e.g. "PYU*ZEPTO" -> "Zepto"
     */
    fun normalize(merchant: String?): String? {
        if (merchant.isNullOrBlank()) return null

        val value = merchant.uppercase()

        // 1. Try to find a match in the registry
        val match = MerchantRegistry.merchants.find { definition ->
            value.contains(definition.canonicalName.uppercase()) || 
            definition.aliases.any { value.contains(it) }
        }

        if (match != null) {
            return match.canonicalName
        }

        // 2. Fallback to basic cleaning if no registry match found
        return merchant.trim()
            .split(" ")
            .take(3)
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
    }
}
