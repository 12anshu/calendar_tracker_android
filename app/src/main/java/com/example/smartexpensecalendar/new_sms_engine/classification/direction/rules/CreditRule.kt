package com.example.smartexpensecalendar.new_sms_engine.classification.direction.rules

import com.example.smartexpensecalendar.new_sms_engine.classification.common.ClassificationRule
import com.example.smartexpensecalendar.new_sms_engine.classification.common.ClassificationRuleResult
import com.example.smartexpensecalendar.new_sms_engine.common.enums.TransactionDirection
import com.example.smartexpensecalendar.new_sms_engine.common.evidence.ClassificationEvidence
import com.example.smartexpensecalendar.new_sms_engine.common.knowledge.DirectionKnowledge
import com.example.smartexpensecalendar.new_sms_engine.common.scoring.ClassificationScores
import com.example.smartexpensecalendar.new_sms_engine.common.utils.MatchUtils
import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

class CreditRule : ClassificationRule<TransactionDirection> {

    override fun evaluate(
        context: QualificationContext
    ): ClassificationRuleResult<TransactionDirection> {

        val matches = MatchUtils.findMatches(
            message = context.input.message,
            signals = DirectionKnowledge.CREDIT_SIGNALS
        )

        if (matches.isEmpty()) {
            return ClassificationRuleResult(
                classification = TransactionDirection.UNKNOWN,
                score = ClassificationScores.UNKNOWN
            )
        }

        return ClassificationRuleResult(
            classification = TransactionDirection.CREDIT,
            score = ClassificationScores.STRONG_MATCH,
            evidence = listOf(
                ClassificationEvidence.CREDIT_SIGNAL
            ),
            matches = matches
        )
    }
}