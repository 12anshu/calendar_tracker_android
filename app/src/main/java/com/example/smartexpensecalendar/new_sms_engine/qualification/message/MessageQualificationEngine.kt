package com.example.smartexpensecalendar.new_sms_engine.qualification.message

import com.example.smartexpensecalendar.new_sms_engine.qualification.contracts.MessageQualifier
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.MessageQualificationResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput

/**
 * Default implementation of Message Qualification.
 */
class MessageQualificationEngine(
    private val evaluator: MessageQualificationEvaluator,
    private val confidenceCalculator: MessageConfidenceCalculator
) : MessageQualifier {

    override fun qualify(message: String): MessageQualificationResult {

        // Note: The sender is handled separately by SenderQualificationEngine,
        // but the evaluator takes QualificationInput which includes it.
        val input = QualificationInput(
            sender = "",
            message = message
        )

        val evaluation = evaluator.evaluate(input)

        val confidence = confidenceCalculator.calculate(
            evaluation.score
        )

        return MessageQualificationResult(
            qualified = confidence >= 50, // Standard Threshold
            confidence = confidence,
            score = evaluation.score,
            evidence = evaluation.evidence,
            executedRules = evaluation.executedRules
        )
    }
}
