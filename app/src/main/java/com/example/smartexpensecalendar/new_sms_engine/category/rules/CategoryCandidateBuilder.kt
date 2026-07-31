package com.example.smartexpensecalendar.new_sms_engine.category.rules

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId

class CategoryCandidateBuilder(
    private val categoryId: CategoryId
) {

    private val evidence = mutableListOf<String>()

    private var confidence = 0f

    private var hasPrimarySignal = false

    fun addPrimary(
        condition: Boolean,
        score: Float,
        evidence: String
    ) = apply {

        if (!condition) return@apply

        hasPrimarySignal = true
        confidence += score
        this.evidence += evidence
    }

    fun addSupporting(
        condition: Boolean,
        score: Float,
        evidence: String
    ) = apply {

        if (!condition) return@apply

        confidence += score
        this.evidence += evidence
    }

    fun build(): CategoryCandidate? {

        if (!hasPrimarySignal) {
            return null
        }

        return CategoryCandidate(
            categoryId = categoryId,
            confidence = confidence.coerceAtMost(1f),
            evidence = evidence
        )
    }
}