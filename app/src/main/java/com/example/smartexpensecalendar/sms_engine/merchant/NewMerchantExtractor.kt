package com.example.smartexpensecalendar.sms_engine.merchant
import com.example.smartexpensecalendar.sms_engine.model.ExtractionEvidence
import com.example.smartexpensecalendar.sms_engine.model.ExtractionResult

object NewMerchantExtractor {

    fun extract(
        smsText: String
    ): ExtractionResult<String> {

        val candidates =
            MerchantCandidateBuilder.build(
                smsText
            )

        val winner =
            MerchantResolver.resolve(
                candidates
            )

        return ExtractionResult(
            value = winner?.merchant,
            confidence = winner?.confidence ?: 0,
            score = winner?.totalScore ?: 0,
            evidence =
                winner?.evidence?.map {
                    ExtractionEvidence(
                        source = it.source,
                        score = it.score,
                        matchedText = it.matchedText,
                        explanation = it.explanation
                    )
                } ?: emptyList()
        )
    }
}
