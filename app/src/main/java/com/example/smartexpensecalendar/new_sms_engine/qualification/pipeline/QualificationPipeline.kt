package com.example.smartexpensecalendar.new_sms_engine.qualification.pipeline

import android.util.Log
import com.example.smartexpensecalendar.new_sms_engine.qualification.contracts.MessageQualifier
import com.example.smartexpensecalendar.new_sms_engine.qualification.contracts.SenderQualifier
import com.example.smartexpensecalendar.new_sms_engine.qualification.engine.QualificationEngine
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

/**
 * Executes the complete Qualification phase.
 */
class QualificationPipeline(
    private val senderQualifier: SenderQualifier,
    private val messageQualifier: MessageQualifier,
    private val engine: QualificationEngine
) {

    fun execute(
        input: QualificationInput
    ): QualificationContext {

        val senderResult = senderQualifier.qualify(input.sender)
        val messageResult = messageQualifier.qualify(input.message)

        val qualification = engine.qualify(
            senderResult,
            messageResult
        )

        logQualification(input, qualification)

        return QualificationContext(
            input = input,
            qualification = qualification
        )
    }

    private fun logQualification(input: QualificationInput, result: com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationResult) {
        val log = buildString {
            appendLine("Qualification Started")
            appendLine("Sender: ${input.sender}")
            appendLine("Message: ${input.message}")
            appendLine("Executed Rules")
            
            result.sender.executedRules.forEach { appendLine("✔ $it") }
            result.message.executedRules.forEach { appendLine("✔ $it") }
            
            appendLine("Final Score: ${result.score}")
            appendLine("Confidence: ${result.confidence}")
            appendLine("Qualified: ${if (result.qualified) "YES" else "NO"}")
        }
        Log.d("QualificationPipeline", log)
    }
}
