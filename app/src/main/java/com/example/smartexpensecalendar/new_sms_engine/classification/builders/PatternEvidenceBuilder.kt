package com.example.smartexpensecalendar.new_sms_engine.classification.builders

import com.example.smartexpensecalendar.new_sms_engine.classification.model.Evidence
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.ActionPatterns
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.ContextPatterns
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.MandatePatterns
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.ObligationPatterns
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.StructuralPatterns
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.TensePatterns
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.PatternMatcher
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

object PatternEvidenceBuilder {

    private val ALL_PATTERNS = buildList {

        addAll(ActionPatterns.ALL)

        addAll(TensePatterns.ALL)

        addAll(ContextPatterns.ALL)

        addAll(StructuralPatterns.ALL)

        addAll(ObligationPatterns.ALL)

        addAll(MandatePatterns.ALL)
    }

    fun build(
        tokens: List<Token>
    ): List<Evidence> {

        val evidence = mutableListOf<Evidence>()

        for (pattern in ALL_PATTERNS) {

            val match = PatternMatcher.find(tokens, pattern)
                ?: continue

            evidence += Evidence(
                type = pattern.evidenceType,
                strength = pattern.strength,
                source = pattern.name,
                matchedText = match.matchedIndices
                    .joinToString(" ") { index -> tokens[index].text },
                startIndex = match.startIndex,
                endIndex = match.endIndex)
        }

        return evidence
    }
}