package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.rules.TimestampBoundaryRule
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

object SegmentBoundaryDetector {

    private val rules = listOf(
        TimestampBoundaryRule()
    )

    fun shouldTerminate(
        relation: SegmentRelation,
        tokens: List<Token>
    ): Boolean {

        return rules.any {
            it.matches(relation, tokens)
        }
    }
}