package com.example.smartexpensecalendar.new_sms_engine.entity.framework.pipeline

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityAssessor
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityDiscovery
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityNormalizer
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract.EntityResolver
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityResult

/**
 * Generic processing pipeline for all Entity Intelligence implementations.
 */
abstract class EntityProcessingPipeline(

    private val discovery: EntityDiscovery,
    private val assessor: EntityAssessor,
    private val resolver: EntityResolver,
    private val normalizer: EntityNormalizer

) {

    /**
     * Executes the complete entity processing pipeline.
     */
    fun process(
        context: ExtractionContext
    ): EntityResult {

        val windows = discovery.discover(context)

        val assessments = assessor.assess(context, windows)

        val resolvedEntity = resolver.resolve(context, assessments)

        val normalizedEntity = normalizer.normalize(context, resolvedEntity)

        return EntityResult(
            normalizedEntity = normalizedEntity,
            confidence = normalizedEntity.resolvedEntity.assessment.confidence,
            score = normalizedEntity.resolvedEntity.assessment.score,
            discoveryMethod = normalizedEntity.resolvedEntity.assessment.window.discoveryMethod.name,
            assessmentSummary = normalizedEntity.resolvedEntity.assessment.evidence.joinToString(", "),
            resolutionSummary = normalizedEntity.resolvedEntity.resolutionReason,
            normalizationSummary = if (normalizedEntity.normalized) {
                "${normalizedEntity.originalName} → ${normalizedEntity.canonicalName}"
            } else {
                "No normalization applied"
            }
        )
    }
}