package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

object SegmentSelector {

    fun merchantCandidates(
        segments: List<MessageSegment>
    ): List<MessageSegment> {

        return segments.filter {

            it.relation == SegmentRelation.AT ||
                    it.relation == SegmentRelation.TO || it.relation == SegmentRelation.UPI
        }
    }
}