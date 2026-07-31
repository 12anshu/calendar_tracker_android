package com.example.smartexpensecalendar.new_sms_engine.common.merchant.normalization

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.repository.MerchantCatalog

class DefaultMerchantNormalizer : MerchantNormalizer {

    override fun normalize(
        merchant: String?
    ): String? {

        if (merchant.isNullOrBlank()) {
            return null
        }

        val normalizedMerchant =
            merchant
                .trim()
                .uppercase()

        /*
         * Stage 1
         *
         * Try catalog keyword normalization.
         */
        MerchantCatalog.forEach { definition ->

            definition.keywords.forEach { keyword ->

                if (normalizedMerchant.contains(keyword.uppercase())) {
                    return definition.keywords.first()
                }
            }
        }

        /*
         * Stage 2
         *
         * Alias normalization.
         */

        return normalizeAlias(normalizedMerchant)
    }

    private fun normalizeAlias(
        merchant: String
    ): String {

        return when {

            merchant.startsWith("BUNDL") ->
                "SWIGGY"

            merchant.startsWith("ONE97") ->
                "PAYTM"

            else ->
                merchant
        }
    }
}