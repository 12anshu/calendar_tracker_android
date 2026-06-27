package com.example.smartexpensecalendar.sms_engine.merchant.detectors
import com.example.smartexpensecalendar.sms_engine.merchant.MerchantPatterns
import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantCandidate
import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantEvidence

object PatternMerchantDetector {

    fun detect(
        text: String
    ): List<MerchantCandidate> {

        val candidates =
            mutableListOf<MerchantCandidate>()

        val upperText =
            text.uppercase()

        MerchantPatterns.TRANSACTION_TRIGGERS.forEach { trigger ->

            val triggerIndex =
                upperText.indexOf(trigger)

            if (triggerIndex == -1)
                return@forEach

            MerchantPatterns.MERCHANT_LOCATORS.forEach { locator ->

                val locatorIndex =
                    upperText.indexOf(
                        " $locator ",
                        startIndex = triggerIndex
                    )

                if (locatorIndex == -1)
                    return@forEach

                val merchant =
                    extractMerchantAfterLocator(
                        text = text,
                        locatorIndex = locatorIndex,
                        locator = locator
                    )

                if (merchant.isNullOrBlank())
                    return@forEach

                candidates.add(
                    MerchantCandidate(
                        merchant = merchant,
                        evidence = mutableListOf(
                            MerchantEvidence(
                                source = "PATTERN",
                                matchedText = "$trigger->$locator",
                                score = 100,
                                explanation =
                                    "Merchant detected using transaction pattern"
                            )
                        )
                    )
                )
            }
        }

        return candidates
    }

    private fun extractMerchantAfterLocator(
        text: String,
        locatorIndex: Int,
        locator: String
    ): String? {

        val start =
            locatorIndex + locator.length + 2

        if (start >= text.length)
            return null

        val remaining =
            text.substring(start)

        return remaining
            .lines()
            .firstOrNull()
            ?.split(
                ".",
                ","
            )
            ?.firstOrNull()
            ?.trim()
            ?.takeIf { it.length >= 3 }
    }
}