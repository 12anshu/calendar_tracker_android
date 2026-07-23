package com.example.smartexpensecalendar.new_sms_engine.qualification

import com.example.smartexpensecalendar.new_sms_engine.common.regex.SenderRegex
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationResult

class QualificationEngine {

    fun qualify(input: QualificationInput): QualificationResult {

        val sender = input.sender.trim().uppercase()

        val isSenderQualified = SenderRegex.STANDARD_SENDER_REGEX.matches(sender)

        return QualificationResult(
            qualified = isSenderQualified,
            sender = sender
        )
    }
}