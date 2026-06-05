package com.example.smartexpensecalendar.sms.detection

import com.example.smartexpensecalendar.sms.config.DetectionConstants
import com.example.smartexpensecalendar.sms.config.SMSKeywordRegistry


object FinancialDetector {

    fun detect(
        sms: String
    ): FinancialDetectionResult {

        val text = sms.uppercase()

        var score = 0

        val matchedSignals =
            mutableSetOf<String>()

        score += scoreStrongSignals(
            text,
            matchedSignals
        )

        score += scoreMediumSignals(
            text,
            matchedSignals
        )

        score += scorePatternSignals(
            text,
            matchedSignals
        )

        score += scoreNegativeSignals(
            text,
            matchedSignals
        )

        if (matchedSignals.size >= 3) {

            score +=
                DetectionConstants.MULTIPLE_SIGNAL_BONUS

            matchedSignals.add(
                "MULTIPLE_SIGNAL_BONUS"
            )
        }

        val confidence =
            score.coerceIn(0, 100)

        return FinancialDetectionResult(

            isFinancial =
                score >=
                        DetectionConstants.FINANCIAL_THRESHOLD,

            confidence =
                confidence,

            score =
                score,

            matchedSignals =
                matchedSignals
        )
    }

    private fun scoreStrongSignals(
        text: String,
        matchedSignals: MutableSet<String>
    ): Int {

        var score = 0

        val strongKeywordGroups = listOf(

            SMSKeywordRegistry.expenseKeywords,

            SMSKeywordRegistry.incomeKeywords,

            SMSKeywordRegistry.transferKeywords,

            SMSKeywordRegistry.refundKeywords,

            SMSKeywordRegistry.salaryKeywords,

            SMSKeywordRegistry.investmentKeywords,

            SMSKeywordRegistry.interestKeywords,

            SMSKeywordRegistry.cashbackKeywords,

            SMSKeywordRegistry.feeKeywords,

            SMSKeywordRegistry.emiKeywords,

            SMSKeywordRegistry.cardPaymentKeywords
        )

        strongKeywordGroups.forEach { keywords ->

            keywords.forEach { keyword ->

                if (containsKeyword(
                        text,
                        keyword
                    )
                ) {

                    score +=
                        DetectionConstants.STRONG_SIGNAL_SCORE

                    matchedSignals.add(
                        keyword
                    )
                }
            }
        }

        return score
    }

    private fun scoreMediumSignals(
        text: String,
        matchedSignals: MutableSet<String>
    ): Int {

        var score = 0

        SMSKeywordRegistry.financialSignals
            .forEach { keyword ->

                if (
                    containsKeyword(
                        text,
                        keyword
                    )
                ) {

                    score +=
                        DetectionConstants.MEDIUM_SIGNAL_SCORE

                    matchedSignals.add(
                        keyword
                    )
                }
            }

        return score
    }

    private fun scorePatternSignals(
        text: String,
        matchedSignals: MutableSet<String>
    ): Int {

        var score = 0
        if (
            DetectionPatterns
                .amountRegex
                .containsMatchIn(text)
        ) {

            score +=
                DetectionConstants.AMOUNT_PATTERN_SCORE

            matchedSignals.add(
                "AMOUNT_PATTERN"
            )
        }
        if (
            DetectionPatterns
                .upiRegex
                .containsMatchIn(text)
        ) {

            score +=
                DetectionConstants.UPI_PATTERN_SCORE

            matchedSignals.add(
                "UPI_PATTERN"
            )
        }
        if (
            DetectionPatterns
                .accountPatterns
                .any {
                    it.containsMatchIn(text)
                }
        ) {

            score +=
                DetectionConstants.ACCOUNT_PATTERN_SCORE

            matchedSignals.add(
                "ACCOUNT_PATTERN"
            )
        }
        if (
            DetectionPatterns
                .cardPatterns
                .any {
                    it.containsMatchIn(text)
                }
        ) {

            score +=
                DetectionConstants.CARD_PATTERN_SCORE

            matchedSignals.add(
                "CARD_PATTERN"
            )
        }
        if (
            DetectionPatterns
                .balancePatterns
                .any {
                    it.containsMatchIn(text)
                }
        ) {

            score +=
                DetectionConstants.BALANCE_PATTERN_SCORE

            matchedSignals.add(
                "BALANCE_PATTERN"
            )
        }

        return score
    }

    private fun scoreNegativeSignals(
        text: String,
        matchedSignals: MutableSet<String>
    ): Int {

        var score = 0

        SMSKeywordRegistry
            .negativeFinancialKeywords
            .forEach { keyword ->

                if (
                    containsKeyword(
                        text,
                        keyword
                    )
                ) {

                    score +=
                        DetectionConstants
                            .NEGATIVE_SIGNAL_SCORE

                    matchedSignals.add(
                        "NEGATIVE:$keyword"
                    )
                }
            }

        return score
    }

    private fun containsKeyword(
        text: String,
        keyword: String
    ): Boolean {

        return Regex(
            "\\b${Regex.escape(keyword)}\\b"
        ).containsMatchIn(text)
    }
}
