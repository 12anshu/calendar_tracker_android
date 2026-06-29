package com.example.smartexpensecalendar.sms_engine.merchant.providers

import com.example.smartexpensecalendar.sms_engine.model.ExtractionContext
import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantWindow
import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantWindowSource

class StandaloneWindowProvider : MerchantWindowProvider {

    override val source = MerchantWindowSource.PATTERN

    override fun detect(
        text: String,
        context: ExtractionContext
    ): List<MerchantWindow> {

        val windows = mutableListOf<MerchantWindow>()

        var currentIndex = 0

        text.lines().forEach { line ->

            val trimmed = line.trim()

            if (isValidStandaloneWindow(trimmed)) {

                val start = text.indexOf(trimmed, currentIndex)

                val end = start + trimmed.length

                windows.add(
                    MerchantWindow(
                        text = trimmed,
                        source = source,
                        startIndex = start,
                        endIndex = end
                    )
                )

                currentIndex = end
            }
        }

        return windows
    }

    private fun isValidStandaloneWindow(
        text: String
    ): Boolean {

        if (text.length < 3)
            return false

        if (text.length > 60)
            return false

        return true
    }
}