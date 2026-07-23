package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAccount
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAction
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsAmount
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsBalance
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsBank
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsCurrency
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsDate
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher.containsFailure
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.MerchantConstants.MAX_CANDIDATE_TOKENS
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.MerchantScores
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.MerchantTextCleaner
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.models.MerchantCandidate

object SegmentEvaluator {


    fun evaluateSegment(
        segment: MessageSegment
    ): MerchantCandidate? {

        return when (segment.relation) {

            SegmentRelation.AT ->
                evaluateAt(segment)

            SegmentRelation.UPI ->
                evaluateUpi(segment)

            SegmentRelation.TO ->
                evaluateTo(segment)

            SegmentRelation.ON ->
                evaluateOn(segment)

            SegmentRelation.ROOT ->
                evaluateRoot(segment)

            else ->
                null
        }
    }

    private fun evaluateOn(
        segment: MessageSegment
    ) : MerchantCandidate? {

        return buildCandidate(
            segment,
            MerchantScores.AFTER_ANCHOR
        )
    }

    private fun evaluateAt(
        segment: MessageSegment
    ): MerchantCandidate? {

        return buildCandidate(
            segment,
            MerchantScores.AFTER_ANCHOR
        )
    }

    private fun evaluateTo(
        segment: MessageSegment
    ): MerchantCandidate? {

        if (!isValidToSegment(segment)) {
            return null
        }

        return buildCandidate(
            segment,
            MerchantScores.TO_ANCHOR
        )
    }

    private fun evaluateUpi(
        segment: MessageSegment
    ): MerchantCandidate? {

        return buildCandidate(
            segment,
            MerchantScores.AFTER_ANCHOR
        )
    }

    private fun evaluateRoot(
        segment: MessageSegment
    ): MerchantCandidate? {

        if (isMerchantRoot(segment) ||
            isStandaloneMerchantRoot(segment)
        ) {
            return buildCandidate(
                segment,
                MerchantScores.AFTER_ANCHOR + 10
            )
        }
        return null
    }

    private fun buildCandidate(
        segment: MessageSegment,
        score: Int
    ): MerchantCandidate? {

        val merchant = MerchantTextCleaner.clean(

            segment.tokens
                .take(MAX_CANDIDATE_TOKENS)
                .joinToString(" ") { it.text }

        )

        if (merchant.isBlank()) {
            return null
        }

        val finalScore = adjustCandidateScore(
            merchant = merchant,
            score = score
        )

        return MerchantCandidate(
            merchant = merchant,
            score = finalScore,
            anchor = segment.relation.name,
            sourceSegment = segment.text
        )
    }

    private fun adjustCandidateScore(
        merchant: String,
        score: Int
    ): Int {

        var adjustedScore = score

        if (containsAmount(merchant))
            adjustedScore += MerchantScores.HAS_AMOUNT

        if (containsDate(merchant))
            adjustedScore += MerchantScores.HAS_DATE

        if (containsAccount(merchant))
            adjustedScore += MerchantScores.HAS_ACCOUNT

        if (containsBalance(merchant))
            adjustedScore += MerchantScores.HAS_BALANCE

        if (containsBank(merchant) && !merchant.contains("@"))
            adjustedScore += MerchantScores.HAS_BANK

        if (containsFailure(merchant))
            adjustedScore -= 50

        return adjustedScore
    }

    private fun isMerchantRoot(
        segment: MessageSegment
    ): Boolean {

        val text = segment.text.uppercase()

        return text.startsWith("MERCHANT:")
    }

    private fun isStandaloneMerchantRoot(
        segment: MessageSegment
    ): Boolean {

        val tokens = segment.tokens

        val candidate = tokens.joinToString(" ") { it.text }

        if (tokens.isEmpty()) {
            return false
        }

        if (tokens.size > MAX_CANDIDATE_TOKENS) {
            return false
        }

        if (containsAmount(candidate)) {
            return false
        }

        if (containsDate(candidate)) {
            return false
        }

        if (containsCurrency(candidate)) {
            return false
        }

        if (containsAccount(candidate)) {
            return false
        }

        if (containsBalance(candidate)) {
            return false
        }

        if (containsFailure(candidate)) {
            return false
        }

        if (containsAction(candidate)) {
            return false
        }

        return true
    }

    private fun isValidToSegment(
        segment: MessageSegment
    ): Boolean {

        val firstToken = segment.tokens.firstOrNull()?.text
            ?: return false

        if (firstToken.startsWith("http", ignoreCase = true) ||
            firstToken.startsWith("www.", ignoreCase = true)
        ) {
            return false
        }

        if (containsAmount(firstToken)) {
            return false
        }

        if (containsDate(firstToken)) {
            return false
        }

        if (containsCurrency(firstToken)) {
            return false
        }

        if (containsAccount(firstToken)) {
            return false
        }

        if (containsBalance(firstToken)) {
            return false
        }

        if (containsFailure(firstToken)) {
            return false
        }

        if (containsAction(firstToken)) {
            return false
        }

        return true
    }

}