package com.example.smartexpensecalendar.new_sms_engine.qualification.contracts

import com.example.smartexpensecalendar.new_sms_engine.qualification.models.MessageQualificationResult

/**
 * Qualifies an SMS message.
 */
fun interface MessageQualifier {

    fun qualify(message: String): MessageQualificationResult
}