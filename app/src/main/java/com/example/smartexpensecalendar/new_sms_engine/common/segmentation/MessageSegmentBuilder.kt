package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

import com.example.smartexpensecalendar.new_sms_engine.common.signals.RelationshipSignals
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.SegmentBoundaryDetector

object MessageSegmentBuilder {

    fun build(
        tokens: List<Token>
    ): List<MessageSegment> {

        if (tokens.isEmpty()) {
            return emptyList()
        }

        val segments = mutableListOf<MessageSegment>()

        var currentRelation = SegmentRelation.ROOT
        var currentTokens = mutableListOf<Token>()

        fun flush() {

            if (currentTokens.isEmpty()) {
                return
            }

            segments += MessageSegment(
                relation = currentRelation,
                tokens = segmentTokens(
                    currentRelation,
                    currentTokens.toList()
                )
            )

            currentTokens.clear()
        }

        var previousLine = tokens.first().lineIndex

        tokens.forEach { token ->

            // New line starts a new segment
            if (token.lineIndex != previousLine) {

                flush()

                currentRelation = SegmentRelation.ROOT

                previousLine = token.lineIndex
            }

            val relation = relationOf(token)

            if (relation != null) {

                flush()

                currentRelation = relation

                currentTokens += token

                return@forEach
            }

            currentTokens += token

            if (
                SegmentBoundaryDetector.shouldTerminate(
                    currentRelation,
                    currentTokens
                )
            ) {

                flush()

                currentRelation = SegmentRelation.ROOT
            }
        }

        flush()

        return segments
    }

    private fun relationOf(
        token: Token
    ): SegmentRelation? {
        return RelationshipSignals.relationOf(
            token.text
        )
    }

    private fun segmentTokens(
        relation: SegmentRelation,
        tokens: List<Token>
    ): List<Token> {

        return when (relation) {

            SegmentRelation.ROOT,
            SegmentRelation.UPI ->
                tokens

            else ->
                tokens.drop(1)
        }
    }
}