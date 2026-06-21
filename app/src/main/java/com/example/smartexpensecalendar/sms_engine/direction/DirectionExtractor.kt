package com.example.smartexpensecalendar.sms_engine.direction

import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms_engine.model.ExtractionResult
import com.example.smartexpensecalendar.sms_engine.model.Extractor
import com.example.smartexpensecalendar.sms_engine.resolver.EvidenceResolver

object DirectionExtractor : Extractor<TransactionDirection> {

    override fun extract(
        smsText: String
    ): ExtractionResult<TransactionDirection> {

        val candidates =
            DirectionCandidateBuilder.build(
                smsText
            )

        return EvidenceResolver.resolve(
            candidates
        )
    }

    fun extractDirectionOnly(
        smsText: String
    ): TransactionDirection {

        return extract(
            smsText
        ).value ?: TransactionDirection.UNKNOWN
    }
}