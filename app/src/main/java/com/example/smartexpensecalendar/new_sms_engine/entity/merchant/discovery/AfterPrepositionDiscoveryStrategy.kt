package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.discovery

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.common.enums.DiscoveryMethod
import com.example.smartexpensecalendar.new_sms_engine.common.utils.MatchUtils
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow
import com.example.smartexpensecalendar.new_sms_engine.entity.merchant.patterns.MerchantDiscoveryPatterns

/**
 * Discovers merchant candidates appearing after transaction prepositions.
 */
class AfterPrepositionDiscoveryStrategy : MerchantDiscoveryStrategy {

    override fun discover(
        context: ExtractionContext
    ): List<EntityWindow> {

        val message = MatchUtils.normalize(
            context.qualificationContext.input.message
        )
        val windows = mutableListOf<EntityWindow>()

        MerchantDiscoveryPatterns.PREPOSITIONS.forEach { preposition ->

            var searchIndex = 0

            while (true) {

                val prepositionIndex = message.indexOf(
                    preposition,
                    searchIndex,
                    ignoreCase = true
                )

                if (prepositionIndex == -1) break

                val start = prepositionIndex + preposition.length

                val end = findWindowEnd(message, start)

                if (end > start) {

                    windows.add(
                        EntityWindow(
                            text = message.substring(start, end).trim(),
                            startIndex = start,
                            endIndex = end,
                            discoveryMethod = DiscoveryMethod.AFTER_PREPOSITION,
                            metadata = mapOf(
                                "preposition" to preposition.trim()
                            )
                        )
                    )
                }

                searchIndex = start
            }
        }

        return windows
    }

    private fun findWindowEnd(
        message: String,
        start: Int
    ): Int {

        var index = start

        while (index < message.length) {

            if (message[index] in MerchantDiscoveryPatterns.WINDOW_TERMINATORS) {
                break
            }

            index++
        }

        return index
    }
}