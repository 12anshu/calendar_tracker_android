package com.example.smartexpensecalendar.new_sms_engine.common.segmentation.rules

import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.BoundaryContext
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.SegmentRelation
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

interface SegmentBoundaryRule {

    fun matches(
        context: BoundaryContext
    ): Boolean
}