package com.example.smartexpensecalendar.new_sms_engine.extraction

import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageType
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.MessageSegment
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

/**
 * Shared context passed to every extractor.
 *
 * This object is immutable and reused across the
 * complete extraction pipeline.
 */
data class ExtractionContext(

    /**
     * Original SMS body.
     */
    val message: String,

    /**
     * SMS sender.
     */
    val sender: String,

    /**
     * Semantic tokens generated during classification.
     */
    val tokens: List<Token>,

    /**
     * Message classification result.
     */
    val messageType: MessageType,

    val segments: List<MessageSegment> = emptyList()
)