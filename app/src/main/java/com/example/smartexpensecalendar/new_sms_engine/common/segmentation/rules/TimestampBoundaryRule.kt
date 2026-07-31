package com.example.smartexpensecalendar.new_sms_engine.common.segmentation.rules

import com.example.smartexpensecalendar.new_sms_engine.common.regex.DateRegex
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.BoundaryContext
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.SegmentRelation
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

class TimestampBoundaryRule : SegmentBoundaryRule {

    private val SUPPORTED_RELATIONS = setOf(
        SegmentRelation.ON
    )

    override fun matches(
        context: BoundaryContext
    ): Boolean {

        if (context.relation !in SUPPORTED_RELATIONS) {
            return false
        }

        val normalized = context.segment
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