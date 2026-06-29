package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.discovery

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.common.enums.DiscoveryMethod
import com.example.smartexpensecalendar.new_sms_engine.common.utils.MatchUtils
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow
import com.example.smartexpensecalendar.new_sms_engine.entity.merchant.patterns.MerchantVocabulary

/**
 * Discovers merchant candidates appearing on standalone transaction lines.
 */
class LinePatternDiscoveryStrategy : MerchantDiscoveryStrategy {

    override fun discover(
        context: ExtractionContext
    ): List<EntityWindow> {

        val windows = mutableListOf<EntityWindow>()
        val message = MatchUtils.normalize(
            context.qualificationContext.input.message
        )

        var searchIndex = 0

        message.lineSequence().forEach { line ->

            val candidate = line.trim()

            if (candidate.isBlank()) {
                searchIndex += line.length + 1
                return@forEach
            }

            if (!isMerchantCandidate(candidate)) {
                searchIndex += line.length + 1
                return@forEach
            }

            val start = message.indexOf(candidate, searchIndex)

            if (start >= 0) {

                val end = start + candidate.length

                windows.add(
                    EntityWindow(
                        text = candidate,
                        startIndex = start,
                        endIndex = end,
                        discoveryMethod = DiscoveryMethod.LINE_PATTERN
                    )
                )

                searchIndex = end
            }
        }

        return windows
    }

    private fun isMerchantCandidate(
        text: String
    ): Boolean {

        if (text.length < 3) return false

        if (text.any(Char::isDigit) && text.count(Char::isDigit) > text.length / 2) {
            return false
        }

        val lower = text.lowercase()

        return !MerchantVocabulary.IGNORE_LINE_PREFIXES.any(lower::startsWith)
    }
}