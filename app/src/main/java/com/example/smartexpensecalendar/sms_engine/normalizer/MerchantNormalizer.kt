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

        // 1. Try to find a match in the registry using word boundaries to avoid 
        // partial matches like "VI" matching "VICTORIA"
        val match = MerchantRegistry.merchants.find { definition ->
            val canonical = definition.canonicalName.uppercase()
            
            // Rule: For very short names (<= 2 chars), always use strict word boundaries.
            // For longer names, we can be slightly more flexible if needed, 
            // but strict matching is safer overall.
            val pattern = if (canonical.length <= 2) {
                Regex("\\b${Regex.escape(canonical)}\\b")
            } else {
                Regex(Regex.escape(canonical))
            }

            pattern.containsMatchIn(value) || 
            definition.aliases.any { alias ->
                val upperAlias = alias.uppercase()
                val aliasPattern = if (upperAlias.length <= 2) {
                    Regex("\\b${Regex.escape(upperAlias)}\\b")
                } else {
                    Regex(Regex.escape(upperAlias))
                }
                aliasPattern.containsMatchIn(value)
            }
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
