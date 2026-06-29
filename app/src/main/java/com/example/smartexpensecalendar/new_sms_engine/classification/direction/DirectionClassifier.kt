package com.example.smartexpensecalendar.new_sms_engine.classification.direction

import android.util.Log
import com.example.smartexpensecalendar.new_sms_engine.classification.common.ClassificationRule
import com.example.smartexpensecalendar.new_sms_engine.classification.common.RuleBasedClassifier
import com.example.smartexpensecalendar.new_sms_engine.classification.models.DirectionResult
import com.example.smartexpensecalendar.new_sms_engine.common.enums.TransactionDirection
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

class DirectionClassifier(

    override val rules: List<ClassificationRule<TransactionDirection>>

) : RuleBasedClassifier<TransactionDirection>() {

    fun classify(
        context: QualificationContext
    ): DirectionResult {

        val results = executeRules(context)

        val best = bestResult(results)

        val result = DirectionResult(
            direction = best.classification,
            confidence = best.score,
            score = best.score,
            evidence = collectEvidence(results),
            matches = collectMatches(results)
        )

        logResult(result)

        return result
    }

    private fun logResult(result: DirectionResult) {
        val log = buildString {
            appendLine("Direction Classification")
            appendLine("Winning Direction : ${result.direction}")
            appendLine("Score : ${result.score}")
            appendLine("Evidence :")
            appendLine(result.evidence.joinToString("\n") { "• $it" })
            appendLine("Matches :")
            appendLine(result.matches.joinToString("\n") { "• $it" })
        }
        Log.d("DirectionClassifier", log)
    }
}
