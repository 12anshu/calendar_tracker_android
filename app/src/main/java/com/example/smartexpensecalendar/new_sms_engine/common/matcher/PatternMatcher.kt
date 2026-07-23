package com.example.smartexpensecalendar.new_sms_engine.common.matcher

import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.AnyWordToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.CategoryToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.OptionalToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Pattern
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.PatternToken
import com.example.smartexpensecalendar.new_sms_engine.common.patterns.model.LiteralToken
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

/**
 * Sequential semantic pattern matcher.
 *
 * Pattern tokens must appear in order but are not required
 * to be adjacent.
 */
object PatternMatcher {

    fun find(
        tokens: List<Token>,
        pattern: Pattern,
        maxGap: Int = pattern.maxGap
    ): PatternMatch? {

        if (tokens.isEmpty()) return null

        for (startIndex in tokens.indices) {

            val matchedIndices = mutableListOf<Int>()

            var currentIndex = startIndex

            var success = true

            for (patternToken in pattern.tokens) {

                val nextIndex = findNextMatch(
                    tokens = tokens,
                    startIndex = currentIndex,
                    patternToken = patternToken,
                    maxGap = maxGap
                )

                if (nextIndex == null) {
                    success = false
                    break
                }

                matchedIndices += nextIndex

                currentIndex = nextIndex + 1
            }

            if (success) {

                return PatternMatch(
                    patternName = pattern.name,
                    matchedIndices = matchedIndices
                )
            }
        }

        return null
    }

    /**
     * Finds the next matching token.
     */
    private fun findNextMatch(
        tokens: List<Token>,
        startIndex: Int,
        patternToken: PatternToken,
        maxGap: Int
    ): Int? {

        val endIndex = minOf(
            tokens.lastIndex,
            startIndex + maxGap
        )

        for (index in startIndex..endIndex) {

            if (matches(tokens[index], patternToken)) {
                return index
            }
        }

        return null
    }

    private fun normalize(value: String): String =
        value
            .uppercase()
            .replace(Regex("[^A-Z0-9]"), "")

    /**
     * Matches a single token.
     */
    fun matches(
        token: Token,
        patternToken: PatternToken
    ): Boolean {

        return when (patternToken) {

            is CategoryToken ->

                patternToken.categories.any(token::has)

            is AnyWordToken ->

                true

            is OptionalToken ->

                matches(token, patternToken.token)

            is LiteralToken ->

                patternToken.values.any { normalize(it) == normalize(token.text) }

            else -> false
        }
    }
}