package com.example.smartexpensecalendar.new_sms_engine.extraction

import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageType
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.model.MerchantDefinition
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.MessageSegment
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode

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

    val direction: Direction? = null,

    /**
     * Semantic tokens generated during classification.
     */
    val tokens: List<Token>,

    val paymentMode: PaymentMode = PaymentMode.UNKNOWN,

    /**
     * Message classification result.
     */
    val messageType: MessageType,

    val merchant: String? = null,

    val merchantDefinition: MerchantDefinition? = null,

    val segments: List<MessageSegment> = emptyList()
)