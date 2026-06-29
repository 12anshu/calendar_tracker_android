package com.example.smartexpensecalendar.new_sms_engine.qualification.engine

import com.example.smartexpensecalendar.new_sms_engine.qualification.models.MessageQualificationResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.SenderQualificationResult

/**
 * Combines Sender and Message qualification into a final decision.
 */
class QualificationEngine {

    fun qualify(
        senderResult: SenderQualificationResult,
        messageResult: MessageQualificationResult
    ): QualificationResult {

        val qualified = senderResult.qualified || messageResult.qualified

        val score = senderResult.score + messageResult.score

        val confidence = maxOf(
            senderResult.confidence,
            messageResult.confidence
        )

        return QualificationResult(
            qualified = qualified,
            confidence = confidence,
            score = score,
            sender = senderResult,
            message = messageResult
        )
    }
}