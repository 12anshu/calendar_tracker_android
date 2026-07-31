package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

import com.example.smartexpensecalendar.new_sms_engine.common.signals.RelationshipSignals
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

object MessageSegmentBuilder {

    fun build(
        tokens: List<Token>
    ): List<MessageSegment> {

        if (tokens.isEmpty()) {
            return emptyList()
        }

        val accumulator = SegmentAccumulator()

        var previousLine = tokens.first().lineIndex

        tokens.forEachIndexed { index, token ->

            val previous =
                tokens.getOrNull(index - 1)

            val next =
                tokens.getOrNull(index + 1)

            if (token.lineIndex != previousLine) {

                accumulator.flush()
                accumulator.resetRelation()

                previousLine = token.lineIndex
            }

            relationOf(token)?.let { relation ->

                accumulator.startRelation(relation)

                accumulator.append(token)

                return@forEachIndexed
            }

            accumulator.append(token)

            if (
                SegmentBoundaryDetector.shouldTerminate(
                    boundaryContext(
                        accumulator,
                        previous,
                        token,
                        next
                    )
                )
            ) {
                accumulator.flush()
                accumulator.resetRelation()
            }
        }

        accumulator.flush()

        return accumulator.segments()
    }

    private fun relationOf(
        token: Token
    ): SegmentRelation? {

        return RelationshipSignals.relationOf(
            token.text
        )
    }

    private fun boundaryContext(
        accumulator: SegmentAccumulator,
        previous: Token?,
        current: Token,
        next: Token?
    ): BoundaryContext {

        return BoundaryContext(
            relation = accumulator.currentRelation,
            segment = accumulator.currentTokens(),
            previous = previous,
            current = current,
            next = next
        )
    }
}