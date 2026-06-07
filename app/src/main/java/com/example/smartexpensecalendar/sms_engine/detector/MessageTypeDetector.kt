package com.example.smartexpensecalendar.sms_engine.detector

import com.example.smartexpensecalendar.sms.config.DetectionConstants
import com.example.smartexpensecalendar.sms.config.MessageTypeKeywords
import com.example.smartexpensecalendar.sms.config.MessageTypePhrases
import com.example.smartexpensecalendar.sms.config.MessageTypePhrases.strongInformationPhrases
import com.example.smartexpensecalendar.sms.utils.KeywordMatcher
import com.example.smartexpensecalendar.sms.utils.PhraseMatcher

class MessageTypeDetector {

    private val priorityOrder = listOf(
        MessageType.OBLIGATION,
        MessageType.PROMOTIONAL,
        MessageType.INFORMATION,
        MessageType.TRANSACTION
    )

    private fun scorePhraseMatches(
        text: String,
        phrases: Set<String>
    ): Int {
        return PhraseMatcher.countMatches(
            text,
            phrases
        )
    }

    private val strongObligationKeywords = setOf(
        "DUE",
        "TOTAL DUE",
        "MINIMUM DUE",
        "OVERDUE",
        "PAST DUE",
        "PAYMENT DUE"
    )

    private val strongInformationKeywords = setOf(
        "CURRENT BALANCE",
        "AVAILABLE BALANCE",
        "STATEMENT GENERATED"
    )

    private val strongPromotionalKeywords = setOf(
        "PRE-APPROVED",
        "LOAN OFFER",
        "INSTANT LOAN"
    )

    fun detect(
        sms: String
    ): MessageTypeDetectionResult {

        val text = sms.uppercase()

        val scores = mutableMapOf(
            MessageType.TRANSACTION to 0,
            MessageType.OBLIGATION to 0,
            MessageType.INFORMATION to 0,
            MessageType.PROMOTIONAL to 0
        )

        val matchedKeywords =
            mutableMapOf<MessageType, MutableSet<String>>()

        MessageType.entries.forEach {
            matchedKeywords[it] = mutableSetOf()
        }

        if (isStrongInformationMessage(text)) {
            return MessageTypeDetectionResult(
                messageType = MessageType.INFORMATION,
                confidence = 100,
                scores = mapOf(
                    MessageType.INFORMATION to 100,
                    MessageType.TRANSACTION to 0,
                    MessageType.OBLIGATION to 0,
                    MessageType.PROMOTIONAL to 0
                ),
                matchedKeywords = mapOf(
                    MessageType.INFORMATION to setOf("STRONG_INFO_OVERRIDE")
                )
            )
        }

        scoreTransaction(
            text,
            scores,
            matchedKeywords
        )

        scoreObligation(
            text,
            scores,
            matchedKeywords
        )

        scoreInformation(
            text,
            scores,
            matchedKeywords
        )

        scorePromotional(
            text,
            scores,
            matchedKeywords
        )

        val winner =
            determineWinner(scores)

        val confidence =
            if (winner == MessageType.UNKNOWN)
                0
            else
                scores[winner] ?: 0

        return MessageTypeDetectionResult(
            messageType = winner,
            confidence = confidence,
            scores = scores,
            matchedKeywords = matchedKeywords.mapValues {
                it.value.toSet()
            }
        )
    }

    private fun isStrongInformationMessage(
        text: String
    ): Boolean {

        return strongInformationPhrases.any {
            text.contains(it)
        }
    }

    private fun scoreTransaction(
        text: String,
        scores: MutableMap<MessageType, Int>,
        matchedKeywords: MutableMap<MessageType, MutableSet<String>>
    ) {
        MessageTypeKeywords.transactionKeywords.forEach { keyword ->

            if (
                KeywordMatcher.containsKeyword(
                    text,
                    keyword
                )
            ) {

                scores[MessageType.TRANSACTION] =
                    scores.getValue(
                        MessageType.TRANSACTION
                    ) +
                            DetectionConstants.MESSAGE_TYPE_KEYWORD_SCORE

                matchedKeywords
                    .getValue(
                        MessageType.TRANSACTION
                    )
                    .add(keyword)
            }
        }
        val phraseScore =
            scorePhraseMatches(
                text,
                MessageTypePhrases.transactionPhrases
            )

        if (phraseScore > 0) {

            scores[MessageType.TRANSACTION] =
                scores.getValue(
                    MessageType.TRANSACTION
                ) +
                        (
                                phraseScore *
                                        DetectionConstants.MESSAGE_TYPE_PHRASE_SCORE
                                )

            matchedKeywords
                .getValue(
                    MessageType.TRANSACTION
                )
                .add("PHRASE_MATCH")
        }
    }

    private fun scoreObligation(
        text: String,
        scores: MutableMap<MessageType, Int>,
        matchedKeywords: MutableMap<MessageType, MutableSet<String>>
    ) {

        MessageTypeKeywords.obligationKeywords.forEach { keyword ->

            if (
                KeywordMatcher.containsKeyword(
                    text,
                    keyword
                )
            ) {

                val score =
                    if (
                        keyword in strongObligationKeywords
                    )
                        DetectionConstants.STRONG_OBLIGATION_SCORE
                    else
                        DetectionConstants.MESSAGE_TYPE_KEYWORD_SCORE

                scores[MessageType.OBLIGATION] =
                    scores.getValue(
                        MessageType.OBLIGATION
                    ) + score

                matchedKeywords
                    .getValue(
                        MessageType.OBLIGATION
                    )
                    .add(keyword)
            }
        }
    }

    private fun scoreInformation(
        text: String,
        scores: MutableMap<MessageType, Int>,
        matchedKeywords: MutableMap<MessageType, MutableSet<String>>
    ) {

        MessageTypeKeywords.informationKeywords.forEach { keyword ->

            if (
                KeywordMatcher.containsKeyword(
                    text,
                    keyword
                )
            ) {

                val score =
                    if (
                        keyword in strongInformationKeywords
                    )
                        DetectionConstants.STRONG_INFORMATION_SCORE
                    else
                        DetectionConstants.MESSAGE_TYPE_KEYWORD_SCORE

                scores[MessageType.INFORMATION] =
                    scores.getValue(
                        MessageType.INFORMATION
                    ) + score

                matchedKeywords
                    .getValue(
                        MessageType.INFORMATION
                    )
                    .add(keyword)
            }
        }

        val strongInfoMatches =
            scorePhraseMatches(
                text,
                strongInformationPhrases
            )

        if (strongInfoMatches > 0) {

            scores[MessageType.INFORMATION] =
                scores.getValue(MessageType.INFORMATION) +
                        (strongInfoMatches * 10)

            matchedKeywords
                .getValue(MessageType.INFORMATION)
                .add("STRONG_INFO_PHRASE")
        }

        val infoPhraseMatches =
            scorePhraseMatches(
                text,
                MessageTypePhrases.informationPhrases
            )

        if (infoPhraseMatches > 0) {

            scores[MessageType.INFORMATION] =
                scores.getValue(
                    MessageType.INFORMATION
                ) +
                        (
                                infoPhraseMatches *
                                        DetectionConstants.STRONG_INFORMATION_SCORE
                                )

            matchedKeywords
                .getValue(
                    MessageType.INFORMATION
                )
                .add("PHRASE_MATCH")
        }
    }

    private fun scorePromotional(
        text: String,
        scores: MutableMap<MessageType, Int>,
        matchedKeywords: MutableMap<MessageType, MutableSet<String>>
    ) {

        MessageTypeKeywords.promotionalKeywords.forEach { keyword ->

            if (
                KeywordMatcher.containsKeyword(
                    text,
                    keyword
                )
            ) {

                val score =
                    if (
                        keyword in strongPromotionalKeywords
                    )
                        DetectionConstants.STRONG_PROMOTIONAL_SCORE
                    else
                        DetectionConstants.MESSAGE_TYPE_KEYWORD_SCORE

                scores[MessageType.PROMOTIONAL] =
                    scores.getValue(
                        MessageType.PROMOTIONAL
                    ) + score

                matchedKeywords
                    .getValue(
                        MessageType.PROMOTIONAL
                    )
                    .add(keyword)
            }
        }
    }

    private fun determineWinner(
        scores: Map<MessageType, Int>
    ): MessageType {

        val maxScore =
            scores.values.maxOrNull() ?: 0

        if (
            maxScore <
            DetectionConstants.MESSAGE_TYPE_MIN_THRESHOLD
        ) {
            return MessageType.UNKNOWN
        }

        return priorityOrder.firstOrNull {
            scores[it] == maxScore
        } ?: MessageType.UNKNOWN
    }
}
