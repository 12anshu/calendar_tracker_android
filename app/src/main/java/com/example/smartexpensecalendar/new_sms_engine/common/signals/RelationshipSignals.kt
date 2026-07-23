package com.example.smartexpensecalendar.new_sms_engine.common.signals

import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.SegmentRelation

object RelationshipSignals {

    val RELATIONSHIP_MAP = mapOf(
        "AT" to SegmentRelation.AT,
        "TO" to SegmentRelation.TO,
        "ON" to SegmentRelation.ON,
        "FROM" to SegmentRelation.FROM,
        "FOR" to SegmentRelation.FOR,
        "VIA" to SegmentRelation.VIA,
        "USING" to SegmentRelation.USING,
        "WITH" to SegmentRelation.WITH,
    )

    fun relationOf(
        token: String
    ): SegmentRelation? {

        val text = token.uppercase()

        RELATIONSHIP_MAP[text]?.let {
            return it
        }

        return when {

            text.startsWith("@UPI_") ->
                SegmentRelation.UPI

            else ->
                null
        }
    }
}