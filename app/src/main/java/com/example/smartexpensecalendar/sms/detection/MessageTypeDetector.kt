package com.example.smartexpensecalendar.sms.detection

import com.example.smartexpensecalendar.sms.config.SMSKeywordRegistry

class MessageTypeDetector {

    fun detect(message: String): MessageTypeResult {

        val sms = message.uppercase()

        var transactionScore = 0
        var obligationScore = 0
        var informationScore = 0
        var promotionalScore = 0

        val matchedKeywords = mutableSetOf<String>()

        SMSKeywordRegistry.transactionKeywords.forEach { keyword ->
            if (sms.contains(keyword)) {
                transactionScore += 20
                matchedKeywords.add(keyword)
            }
        }

        SMSKeywordRegistry.creditKeywords.forEach { keyword ->
            if (sms.contains(keyword)) {
                transactionScore += 20
                matchedKeywords.add(keyword)
            }
        }

        SMSKeywordRegistry.obligationKeywords.forEach { keyword ->
            if (sms.contains(keyword)) {
                obligationScore += 20
                matchedKeywords.add(keyword)
            }
        }

        SMSKeywordRegistry.informationKeywords.forEach { keyword ->
            if (sms.contains(keyword)) {
                informationScore += 20
                matchedKeywords.add(keyword)
            }
        }

        SMSKeywordRegistry.promotionalKeywords.forEach { keyword ->
            if (sms.contains(keyword)) {
                promotionalScore += 20
                matchedKeywords.add(keyword)
            }
        }

        return buildResult(
            transactionScore,
            obligationScore,
            informationScore,
            promotionalScore,
            matchedKeywords
        )
    }

    private fun buildResult(
        transactionScore: Int,
        obligationScore: Int,
        informationScore: Int,
        promotionalScore: Int,
        matchedKeywords: Set<String>
    ): MessageTypeResult {
        val maxScore = maxOf(
            transactionScore,
            obligationScore,
            informationScore,
            promotionalScore
        )

        if (
            transactionScore > 0 &&
            obligationScore == 0 &&
            informationScore == 0 &&
            promotionalScore == 0
        ) {
            return MessageTypeResult(
                messageType = MessageType.TRANSACTION,
                confidence = transactionScore,
                score = transactionScore,
                matchedKeywords = matchedKeywords
            )
        }

        val type = when {
            transactionScore == maxScore && maxScore > 0 ->
                MessageType.TRANSACTION

            obligationScore == maxScore && maxScore > 0 ->
                MessageType.OBLIGATION

            informationScore == maxScore && maxScore > 0 ->
                MessageType.INFORMATION

            promotionalScore == maxScore && maxScore > 0 ->
                MessageType.PROMOTIONAL

            else -> MessageType.UNKNOWN
        }

        return MessageTypeResult(
            messageType = type,
            confidence = maxScore,
            score = maxScore,
            matchedKeywords = matchedKeywords
        )
    }
}