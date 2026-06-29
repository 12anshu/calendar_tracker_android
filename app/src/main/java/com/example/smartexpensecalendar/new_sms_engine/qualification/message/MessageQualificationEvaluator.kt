package com.example.smartexpensecalendar.new_sms_engine.qualification.message

import android.util.Log
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationEvaluationResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput
import com.example.smartexpensecalendar.new_sms_engine.qualification.rules.QualificationRule

class MessageQualificationEvaluator(
    private val rules: List<QualificationRule>
) {

    fun evaluate(
        input: QualificationInput
    ): QualificationEvaluationResult {

        var totalScore = 0
        val evidence = mutableListOf<String>()
        val executedRules = mutableListOf<String>()

        rules.forEach { rule ->
            try {
                val result = rule.evaluate(input)
                totalScore += result.score
                if (result.evidences.isNotEmpty()) {
                    evidence.addAll(result.evidences)
                    executedRules.add("${rule.javaClass.simpleName} (+${result.score})")
                } else {
                    executedRules.add("${rule.javaClass.simpleName} (0)")
                }
            } catch (e: Exception) {
                Log.e("QualificationEvaluator", "Error executing rule: ${rule.javaClass.simpleName}", e)
                executedRules.add("${rule.javaClass.simpleName} (FAILED)")
            }
        }

        return QualificationEvaluationResult(
            score = totalScore.coerceIn(0, 100),
            evidence = evidence.distinct(),
            executedRules = executedRules
        )
    }
}
