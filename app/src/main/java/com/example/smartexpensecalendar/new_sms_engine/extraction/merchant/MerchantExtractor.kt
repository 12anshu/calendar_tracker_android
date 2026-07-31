package com.example.smartexpensecalendar.new_sms_engine.extraction.merchant

import android.util.Log
import com.example.smartexpensecalendar.new_sms_engine.common.confidence.ConfidenceCalculator
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAccount
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAction
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAmount
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsBalance
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsCurrency
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsDate
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsFailure
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.MessageSegment
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.SegmentEvaluator
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.SegmentRelation
import com.example.smartexpensecalendar.new_sms_engine.common.signals.CounterpartySignals
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token
import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.MerchantConstants.MAX_CANDIDATE_TOKENS
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.models.MerchantCandidate
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.models.MerchantResult

class MerchantExtractor {

    fun extract(
        context: ExtractionContext
    ): MerchantResult {

        val segmentCandidates =
            extractSegmentCandidates(context)

        val candidates =
            when {
                segmentCandidates.isNotEmpty() ->
                    segmentCandidates
                else ->
                    extractLineCandidates(context)
            }

        val winner = pickWinner(candidates)

        return MerchantResult(
            merchant = winner?.merchant,
            anchor = winner?.anchor,
            sourceSegment = winner?.sourceSegment,
            confidence = ConfidenceCalculator.fromScores(
                winner = winner?.score ?: 0,
                competitor = candidates
                    .filterNot { it == winner }
                    .maxOfOrNull { it.score } ?: 0
            )
        )
    }

    private fun extractSegmentCandidates(
        context: ExtractionContext
    ): List<MerchantCandidate> {

        return context.segments
            .mapNotNull(SegmentEvaluator::evaluateSegment)
    }

    private fun extractLineCandidates(
        context: ExtractionContext
    ): List<MerchantCandidate> {

        val candidates = mutableListOf<MerchantCandidate>()
        val lines = context.tokens
            .groupBy { it.lineIndex }
            .values

        lines.forEach { lineTokens ->

            val line = lineTokens.joinToString(" ") { it.text }

            val score = scoreLine(line)

            if (score > 0) {

                candidates += MerchantCandidate(
                    merchant = line,
                    score = score,
                    anchor = "LINE"
                )
            }
        }
        return candidates
    }

    private fun scoreLine(
        line: String
    ): Int {

        var score = MerchantScores.LINE

        val text = line.trim()

        if (text.length < 3) {
            return 0
        }

        score += scoreLength(text)

        score += scoreCharacterComposition(text)

        score += scoreContent(text)

        return score.coerceAtLeast(0)
    }

    private fun scoreLength(
        text: String
    ): Int {

        return when {

            text.length < 3 ->
                MerchantScores.TOO_SHORT

            text.length in 3..40 ->
                10

            else ->
                -10
        }
    }

    private fun scoreCharacterComposition(
        text: String
    ): Int {

        var score = 0

        if (text == text.uppercase()) {
            score += MerchantScores.UPPERCASE_LINE
        }

        if (text.firstOrNull()?.isUpperCase() == true) {
            score += MerchantScores.TITLE_CASE_LINE
        }

        if (text.all { it.isDigit() }) {
            score += MerchantScores.ONLY_NUMBERS
        }

        return score
    }

    private fun scoreContent(
        text: String
    ): Int {

        var score = 0

        if (BankingEntityMatcher.containsAmount(text))
            score += MerchantScores.HAS_AMOUNT

        if (BankingEntityMatcher.containsDate(text))
            score += MerchantScores.HAS_DATE

        if (BankingEntityMatcher.containsAccount(text))
            score += MerchantScores.HAS_ACCOUNT

        if (BankingEntityMatcher.containsBalance(text))
            score += MerchantScores.HAS_BALANCE

        if (BankingEntityMatcher.containsBank(text) && !text.contains("@"))
            score += MerchantScores.HAS_BANK

        if (BankingEntityMatcher.containsFailure(text))
            score -= 50

        return score
    }

    private fun pickWinner(
        candidates: List<MerchantCandidate>
    ): MerchantCandidate? {

        return candidates
            .filter { it.score > 0 }
            .maxByOrNull { it.score }
    }
}