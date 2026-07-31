package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.rules.SentenceBoundaryRule
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.rules.TimestampBoundaryRule
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

object SegmentBoundaryDetector {

    private val rules = listOf(
        TimestampBoundaryRule(),
        SentenceBoundaryRule()
    )

    fun shouldTerminate(
        context: BoundaryContext
    ): Boolean {

        return rules.any {
            it.matches(context)
        }
    }
}