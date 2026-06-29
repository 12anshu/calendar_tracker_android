package com.example.smartexpensecalendar.new_sms_engine.classification.direction

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

        return DirectionResult(
            direction = best.classification,
            confidence = best.score,
            score = best.score,
            evidence = collectEvidence(results),
            matches = collectMatches(results)
        )
    }
}