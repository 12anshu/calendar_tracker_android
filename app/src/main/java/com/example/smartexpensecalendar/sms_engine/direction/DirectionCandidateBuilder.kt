package com.example.smartexpensecalendar.sms_engine.direction

import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms.config.DetectionConstants
import com.example.smartexpensecalendar.sms_engine.model.Candidate
import com.example.smartexpensecalendar.sms_engine.model.ExtractionEvidence

object DirectionCandidateBuilder {

    private const val SCORE_OVERRIDE = 150
    private const val SCORE_PHRASE = 100
    private const val SCORE_VERB = 60

    fun build(
        smsText: String
    ): List<Candidate<TransactionDirection>> {

        val text = smsText.uppercase()

        val debitCandidate =
            Candidate(TransactionDirection.DEBIT)

        val creditCandidate =
            Candidate(TransactionDirection.CREDIT)

        // =========================================================
        // OVERRIDE SIGNALS (Highest Priority)
        // =========================================================

        DirectionPatterns.DEBIT_OVERRIDE_SIGNALS
            .filter { text.contains(it, ignoreCase = true) }
            .forEach { signal ->

                debitCandidate.evidence.add(
                    ExtractionEvidence(
                        source = "OVERRIDE",
                        score = SCORE_OVERRIDE,
                        matchedText = signal,
                        explanation = "Strong debit override signal"
                    )
                )
            }

        DirectionPatterns.CREDIT_OVERRIDE_SIGNALS
            .filter { text.contains(it, ignoreCase = true) }
            .forEach { signal ->

                creditCandidate.evidence.add(
                    ExtractionEvidence(
                        source = "OVERRIDE",
                        score = SCORE_OVERRIDE,
                        matchedText = signal,
                        explanation = "Strong credit override signal"
                    )
                )
            }

        // =========================================================
        // PHRASES
        // =========================================================

        DirectionPatterns.PHRASES_DEBIT
            .filter { smartMatch(text, it) }
            .forEach { phrase ->

                debitCandidate.evidence.add(
                    ExtractionEvidence(
                        source = "PHRASE",
                        score = SCORE_PHRASE,
                        matchedText = phrase,
                        explanation = "Matched debit phrase"
                    )
                )
            }

        DirectionPatterns.PHRASES_CREDIT
            .filter { smartMatch(text, it) }
            .forEach { phrase ->

                creditCandidate.evidence.add(
                    ExtractionEvidence(
                        source = "PHRASE",
                        score = SCORE_PHRASE,
                        matchedText = phrase,
                        explanation = "Matched credit phrase"
                    )
                )
            }

        // =========================================================
        // VERBS
        // =========================================================

        DirectionPatterns.VERBS_DEBIT
            .filter {
                text.contains(
                    "\\b${Regex.escape(it)}\\b".toRegex()
                )
            }
            .forEach { verb ->

                debitCandidate.evidence.add(
                    ExtractionEvidence(
                        source = "VERB",
                        score = SCORE_VERB,
                        matchedText = verb,
                        explanation = "Matched debit verb"
                    )
                )
            }

        DirectionPatterns.VERBS_CREDIT
            .filter {
                text.contains(
                    "\\b${Regex.escape(it)}\\b".toRegex()
                )
            }
            .forEach { verb ->

                creditCandidate.evidence.add(
                    ExtractionEvidence(
                        source = "VERB",
                        score = SCORE_VERB,
                        matchedText = verb,
                        explanation = "Matched credit verb"
                    )
                )
            }

        return listOf(
            debitCandidate,
            creditCandidate
        )
    }

    private fun smartMatch(
        text: String,
        phrase: String
    ): Boolean {

        // 1. Handle the {CUR} placeholder by replacing it with a currency regex.
        // 2. Escape other regex special characters to prevent PatternSyntaxException.
        
        val currencyPattern = DetectionConstants.CURRENCY_SYMBOLS
        val marker = "___CUR_PLACEHOLDER___"
        
        val templated = phrase.replace("{CUR}", marker)
        val escaped = Regex.escape(templated)
        val finalRegex = escaped.replace(marker, "\\E$currencyPattern\\Q")

        return Regex(
            finalRegex,
            RegexOption.IGNORE_CASE
        ).containsMatchIn(text)
    }

}
