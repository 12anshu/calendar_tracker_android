package com.example.smartexpensecalendar.sms.merchant

object MerchantNormalizer {

    fun normalize(
        merchant: String?
    ): String? {

        val result =
            MerchantMatcher.match(
                merchant
            )

        return result.canonicalName
            ?: merchant
    }
}