package com.example.smartexpensecalendar.new_sms_engine.classification

import com.example.smartexpensecalendar.new_sms_engine.classification.builders.PatternEvidenceBuilder
import com.example.smartexpensecalendar.new_sms_engine.classification.builders.SemanticEvidenceBuilder
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageTypeResult
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

object ClassificationEngine {

    fun classify(
        tokens: List<Token>
    ): MessageTypeResult {

        val patternEvidence = PatternEvidenceBuilder.build(tokens)

        val evidence = buildList {
            addAll(patternEvidence)
            addAll(SemanticEvidenceBuilder.build(tokens, patternEvidence))
        }

        return MessageTypeClassifier.classify(evidence)
    }
}