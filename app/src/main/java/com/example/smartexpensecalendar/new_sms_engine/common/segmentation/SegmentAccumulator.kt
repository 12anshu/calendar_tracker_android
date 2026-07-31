package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

class SegmentAccumulator {

    private val segments = mutableListOf<MessageSegment>()

    private var relation = SegmentRelation.ROOT

    private val tokens = mutableListOf<Token>()

    val currentRelation: SegmentRelation
        get() = relation

    fun currentTokens(): List<Token> =
        tokens

    fun append(
        token: Token
    ) {
        tokens += token
    }

    fun startRelation(
        relation: SegmentRelation
    ) {
        flush()

        this.relation = relation
    }

    fun resetRelation() {
        relation = SegmentRelation.ROOT
    }

    fun flush() {

        if (tokens.isEmpty()) {
            return
        }

        val snapshot = tokens.toList()

        segments += MessageSegment(
            relation = relation,
            tokens = segmentTokens(
                relation,
                snapshot
            )
        )

        tokens.clear()
    }

    fun segments(): List<MessageSegment> =
        segments

    private fun segmentTokens(
        relation: SegmentRelation,
        tokens: List<Token>
    ): List<Token> {

        return when (relation) {

            SegmentRelation.ROOT,
            SegmentRelation.UPI ->
                tokens.toList()

            else ->
                tokens.drop(1).toList()
        }
    }
}