package com.example.smartexpensecalendar.new_sms_engine.common.merchant.normalization

import com.example.smartexpensecalendar.new_sms_engine.common.merchant.repository.MerchantCatalog
import com.example.smartexpensecalendar.new_sms_engine.common.regex.CorporateSuffixRegex.CORPORATE_SUFFIX_REGEX

class DefaultMerchantNormalizer : MerchantNormalizer {

    override fun normalize(
        merchant: String?
    ): String? {

        if (merchant.isNullOrBlank()) {
            return null
        }

        var candidate = sanitize(
            merchant.trim().uppercase()
        )

        lookupMerchant(candidate)?.let {
            return it
        }

        if (isUpiIdentifier(candidate)) {

            candidate = normalizeUpi(candidate)

            lookupMerchant(candidate)?.let {
                return it
            }
        }

        if (hasCorporateSuffix(candidate)) {

            candidate = removeCorporateSuffixes(candidate)

            lookupMerchant(candidate)?.let {
                return it
            }
        }

        return normalizeAlias(candidate)
    }

    /**
     * Basic text cleanup.
     */
    private fun sanitize(
        merchant: String
    ): String {

        return merchant
            .trim()
            .replace(Regex("""\s{2,}"""), " ")
    }

    /**
     * Merchant catalog lookup.
     *
     * Returns canonical merchant if found.
     */
    private fun lookupMerchant(
        merchant: String
    ): String? {

        MerchantCatalog.forEach { definition ->

            definition.keywords.forEach { keyword ->

                if (merchant.contains(keyword.uppercase())) {
                    return definition.id.toString()
                }
            }
        }

        return null
    }

    /**
     * Stage 2
     *
     * Normalize UPI merchant identifiers.
     *
     * Examples:
     *
     * PAYTM.D19087214515@PTY   -> PAYTM.D19087214515
     * PAYTMQR63VWB4@PTYS       -> PAYTMQR63VWB4
     * Q638103246@YBL           -> Q638103246
     */
    private fun normalizeUpi(
        merchant: String
    ): String {

        val atIndex = merchant.indexOf('@')

        if (atIndex == -1) {
            return merchant
        }

        return merchant
            .substring(0, atIndex)
            .trim()
    }

    /**
     * Removes common corporate suffixes.
     *
     * Examples:
     *
     * SWIGGY PRIVATE LIMITED -> SWIGGY
     * ABC PVT LTD            -> ABC
     * XYZ LIMITED            -> XYZ
     */
    private fun removeCorporateSuffixes(
        merchant: String
    ): String {

        return merchant
            .replace(CORPORATE_SUFFIX_REGEX, "")
            .replace(Regex("""\s{2,}"""), " ")
            .trim()
    }

    /**
     * Alias normalization.
     */
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

    private fun isUpiIdentifier(
        merchant: String
    ): Boolean {

        return merchant.contains("@")
    }

    private fun hasCorporateSuffix(
        merchant: String
    ): Boolean {

        return CORPORATE_SUFFIX_REGEX.containsMatchIn(
            merchant
        )
    }
}