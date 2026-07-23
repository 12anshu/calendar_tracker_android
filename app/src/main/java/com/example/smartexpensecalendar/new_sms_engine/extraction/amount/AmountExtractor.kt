package com.example.smartexpensecalendar.new_sms_engine.extraction.amount

import com.example.smartexpensecalendar.new_sms_engine.common.enums.amount.AmountSignals
import com.example.smartexpensecalendar.new_sms_engine.common.enums.amount.AmountRegex
import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models.AmountCandidate
import com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models.AmountResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models.Currency
import com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models.Money
import java.math.BigDecimal

class AmountExtractor {

    fun extract(context: ExtractionContext): AmountResult {

        val candidates = findCandidates(context.message)

        if (candidates.isEmpty()) {
            return AmountResult(
                money = null,
                confidence = 0f
            )
        }

        return resolveBestCandidate(candidates)
    }

    private fun findCandidates(message: String): List<AmountCandidate> {

        val candidates = mutableListOf<AmountCandidate>()

        collectCandidates(
            message = message,
            regex = AmountRegex.CURRENCY_PREFIX_REGEX,
            candidates = candidates,
            currencyGroup = 1,
            amountGroup = 2
        )

        collectCandidates(
            message = message,
            regex = AmountRegex.CURRENCY_SUFFIX_REGEX,
            candidates = candidates,
            currencyGroup = 2,
            amountGroup = 1
        )

        return candidates
            .distinctBy { it.startIndex to it.endIndex }
    }

    private fun collectCandidates(
        message: String,
        regex: Regex,
        candidates: MutableList<AmountCandidate>,
        currencyGroup: Int?,
        amountGroup: Int
    ) {

        regex.findAll(message).forEach { match ->

            val amount =
                match.groups[amountGroup]?.value ?: return@forEach

            val currency =
                currencyGroup
                    ?.let { match.groups[it]?.value }
                    ?.let(::mapCurrency)
                    ?: Currency.INR.symbol

            candidates.add(
                AmountCandidate(
                    money = Money(
                        amount = normalizeAmount(amount),
                        currency = currency as Currency,
                        rawValue = match.value
                    ),
                    startIndex = match.range.first,
                    endIndex = match.range.last,
                    context = buildContext(
                        message,
                        match.range.first,
                        match.range.last
                    ),
                    rawMatch = match.value
                )
            )
        }
    }

    private fun resolveBestCandidate(
        candidates: List<AmountCandidate>
    ): AmountResult {

        // Highest Priority:
        // Transaction amount without reporting keywords

        candidates.firstOrNull {

            containsAnyKeyword(
                it.context,
                AmountSignals.TRANSACTION_SIGNALS
            ) &&
                    !containsAnyKeyword(
                        it.context,
                        AmountSignals.REPORTING_SIGNALS
                    )

        }?.let {
            return AmountResult(
                money = it.money,
                confidence = 1f
            )
        }

        // Second Priority:
        // Any amount without reporting keywords

        candidates.firstOrNull {

            !containsAnyKeyword(
                it.context,
                AmountSignals.REPORTING_SIGNALS
            )

        }?.let {
            return AmountResult(
                money = it.money,
                confidence = 0.8f
            )
        }

        // Fallback:
        // First extracted amount

        val fallback = candidates.first()
        return AmountResult(
            money = fallback.money,
            confidence = 0.5f
        )
    }

    private fun containsAnyKeyword(
        text: String,
        keywords: Set<String>
    ): Boolean {

        val lower = text.lowercase()

        return keywords.any {
            lower.contains(it)
        }
    }

    private fun normalizeAmount(
        value: String
    ): BigDecimal {

        return value
            .replace(",", "")
            .toBigDecimal()
    }

    private fun buildContext(
        message: String,
        startIndex: Int,
        endIndex: Int,
        windowSize: Int = 25
    ): String {

        val start = (startIndex - windowSize)
            .coerceAtLeast(0)

        val end = (endIndex + windowSize)
            .coerceAtMost(message.length)

        return message.substring(start, end)
    }

    private fun mapCurrency(
        currency: String
    ): Currency {

        return when (currency.lowercase()) {

            "₹", "rs", "rs.", "inr" -> Currency.INR

            "$", "usd" -> Currency.USD

            "€", "eur" -> Currency.EUR

            "£", "gbp" -> Currency.GBP

            "aed" -> Currency.AED

            else -> Currency.UNKNOWN
        }
    }
}