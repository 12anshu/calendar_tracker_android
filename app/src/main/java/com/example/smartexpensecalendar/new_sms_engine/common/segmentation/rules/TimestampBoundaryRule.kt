package com.example.smartexpensecalendar.new_sms_engine.common.segmentation.rules

import com.example.smartexpensecalendar.new_sms_engine.common.regex.DateRegex
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.SegmentRelation
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

class TimestampBoundaryRule : SegmentBoundaryRule {

    private val SUPPORTED_RELATIONS = setOf(
        SegmentRelation.ON
    )

    override fun matches(
        relation: SegmentRelation,
        tokens: List<Token>
    ): Boolean {

        if (relation !in SUPPORTED_RELATIONS) {
            return false
        }

        val normalized = tokens
            .map { normalize(it.text) }

        return when {

            normalized.size == 2 &&
                    DateRegex.isDate(normalized.last()) ->
                true

            normalized.size == 3 &&
                    DateRegex.isDate(normalized[1]) &&
                    DateRegex.isTime(normalized[2]) ->
                true

            normalized.size == 4 &&
                    DateRegex.isDate(normalized[1]) &&
                    DateRegex.isTime(normalized[2]) &&
                    DateRegex.isAmPm(normalized[3]) ->
                true

            else ->
                false
        }
    }

    private fun normalize(
        text: String
    ): String {

        return text
            .trim()
            .trimEnd('.', ',', ';', ':')
    }
}