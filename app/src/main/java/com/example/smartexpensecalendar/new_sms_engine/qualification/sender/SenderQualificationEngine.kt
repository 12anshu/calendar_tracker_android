package com.example.smartexpensecalendar.new_sms_engine.qualification.sender

import com.example.smartexpensecalendar.new_sms_engine.common.regex.SenderRegex
import com.example.smartexpensecalendar.new_sms_engine.qualification.contracts.SenderQualifier
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.SenderQualificationResult

class SenderQualificationEngine(
    private val confidenceCalculator: SenderConfidenceCalculator
) : SenderQualifier {

    override fun qualify(sender: String): SenderQualificationResult {

        val normalizedSender = sender.trim().uppercase()

        val qualified =
            SenderRegex.STANDARD_SENDER_REGEX.matches(normalizedSender)

        val evidence = mutableListOf<String>()
        val executedRules = mutableListOf<String>()

        if (qualified) {
            evidence.add("VALID_SENDER_FORMAT")
            executedRules.add("SenderFormatRule (+40)")
        } else {
            executedRules.add("SenderFormatRule (0)")
        }

        val confidence = confidenceCalculator.calculate(evidence)

        return SenderQualificationResult(
            qualified = qualified,
            confidence = confidence,
            score = confidence,
            sender = normalizedSender,
            evidence = evidence,
            executedRules = executedRules
        )
    }
}
