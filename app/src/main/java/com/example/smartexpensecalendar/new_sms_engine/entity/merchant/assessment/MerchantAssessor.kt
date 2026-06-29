package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.assessment

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityAssessor
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityAssessment
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow

/**
 * Assesses merchant windows using registered assessment rules.
 */
class MerchantAssessor(

    private val rules: List<MerchantAssessmentRule>

) : EntityAssessor {

    override fun assess(
        context: ExtractionContext,
        windows: List<EntityWindow>
    ): List<EntityAssessment> {

        return windows.map { window ->

            val assessments = rules.map { rule ->
                rule.assess(context, window)
            }

            EntityAssessment(
                window = window,
                confidence = assessments.sumOf { it.confidence }.coerceAtMost(100),
                score = assessments.sumOf { it.score },
                evidence = assessments.flatMap { it.evidence }.distinct(),
                metadata = assessments
                    .flatMap { it.metadata.entries }
                    .associate { it.toPair() }
            )
        }
    }
}