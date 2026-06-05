package com.example.smartexpensecalendar.sms.merchant

import com.example.smartexpensecalendar.sms.config.NoiseWordRegistry

object MerchantCleanupEngine {

    private val noiseWords = NoiseWordRegistry.words

    fun cleanup(
        merchant: String?
    ): String {

        if (merchant.isNullOrBlank())
            return ""

        var cleaned =
            merchant.uppercase()

        cleaned =
            cleaned.replace("[^A-Z0-9 ]".toRegex(), " ")

        noiseWords.forEach {

            cleaned =
                cleaned.replace(it, " ")
        }

        return cleaned
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
}