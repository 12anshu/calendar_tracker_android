package com.example.smartexpensecalendar.new_sms_engine.qualification.contracts

import com.example.smartexpensecalendar.new_sms_engine.qualification.models.SenderQualificationResult

/**
 * Qualifies an SMS sender.
 */
fun interface SenderQualifier {

    fun qualify(sender: String): SenderQualificationResult
}