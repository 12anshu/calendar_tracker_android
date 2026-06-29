package com.example.smartexpensecalendar.new_sms_engine.qualification.contracts

import com.example.smartexpensecalendar.new_sms_engine.qualification.models.MessageQualificationResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.SenderQualificationResult

/**
 * Produces the final QualificationResult.
 */
fun interface QualificationEngine {

    fun qualify(
        senderResult: SenderQualificationResult,
        messageResult: MessageQualificationResult
    ): QualificationResult
}