package com.example.smartexpensecalendar.sms_engine.merchant

import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantCandidate
import com.example.smartexpensecalendar.sms_engine.merchant.model.MerchantEvidence

object MerchantCandidateBuilder {

    fun build(
        smsText: String
    ): List<MerchantCandidate> {

        val candidates =
            mutableListOf<MerchantCandidate>()

        val upperText =
            smsText.uppercase()

        // AFTER TO
        extractAfterAnchor(
            text = smsText,
            anchor = " TO ",
            source = "AFTER_TO",
            score = 100,
            explanation = "Merchant found after TO anchor"
        )?.let {
            candidates.add(it)
        }

        // AFTER AT
        extractAfterAnchor(
            text = smsText,
            anchor = " AT ",
            source = "AFTER_AT",
            score = 100,
            explanation = "Merchant found after AT anchor"
        )?.let {
            candidates.add(it)
        }

        // AFTER BY
        extractAfterAnchor(
            text = smsText,
            anchor = " BY ",
            source = "AFTER_BY",
            score = 90,
            explanation = "Merchant found after BY anchor"
        )?.let {
            candidates.add(it)
        }

        // AFTER FROM
        extractAfterAnchor(
            text = smsText,
            anchor = " FROM ",
            source = "AFTER_FROM",
            score = 90,
            explanation = "Merchant found after FROM anchor"
        )?.let {
            candidates.add(it)
        }

        candidates.addAll(extractUpiHandleCandidates(smsText))

        return candidates
    }

    private fun extractAfterAnchor(
        text: String,
        anchor: String,
        source: String,
        score: Int,
        explanation: String
    ): MerchantCandidate? {

        val upper =
            text.uppercase()

        val start =
            upper.indexOf(anchor)

        if (start == -1)
            return null

        val value =
            text.substring(
                start + anchor.length
            )

        val merchant =
            value
                .split(
                    ".",
                    ",",
                    "\n",
                    " VIA ",
                    " USING ",
                    " ON ",
                    " REF ",
                    " UTR "
                )
                .firstOrNull()
                ?.trim()
                ?: return null

        if (merchant.length < 3)
            return null

        return MerchantCandidate(
            merchant = merchant,
            evidence = mutableListOf(
                MerchantEvidence(
                    source = source,
                    matchedText = merchant,
                    score = score,
                    explanation = explanation
                )
            )
        )
    }

    private fun extractUpiHandleCandidates(
        text: String
    ): List<MerchantCandidate> {
        val candidates = mutableListOf<MerchantCandidate>()
        val upperText = text.uppercase()

        MerchantPatterns.UPI_HANDLE_HINTS.forEach { hint ->
            var startIndex = 0
            while (true) {
                val index = upperText.indexOf(hint, startIndex)
                if (index == -1) break

                // Extract the word containing the hint (the UPI ID)
                val beforeHint = text.substring(0, index).substringAfterLast(" ", "")
                val upiId = beforeHint + text.substring(index, index + hint.length)

                if (upiId.length > hint.length) {
                    // The merchant name is often the part before the '@'
                    val merchantName = upiId.substringBefore("@").trim()

                    if (merchantName.length >= 3) {
                        candidates.add(
                            MerchantCandidate(
                                merchant = merchantName,
                                evidence = mutableListOf(
                                    MerchantEvidence(
                                        source = "UPI_HANDLE",
                                        matchedText = upiId,
                                        score = 180,
                                        explanation = "Merchant extracted from UPI ID: $upiId"
                                    )
                                )
                            )
                        )
                    }
                }
                startIndex = index + hint.length
            }
        }
        return candidates
    }
}