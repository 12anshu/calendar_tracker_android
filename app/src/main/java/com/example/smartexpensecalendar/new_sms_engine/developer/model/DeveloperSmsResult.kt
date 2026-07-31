package com.example.smartexpensecalendar.new_sms_engine.developer.model

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models.Money
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.PatternMatch
import com.example.smartexpensecalendar.new_sms_engine.classification.model.Evidence
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageType
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.MessageSegment
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventType
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventEvidence

data class DeveloperSmsResult(

    val id: Long,

    val sender: String,

    val message: String,

    val timestamp: Long,

    // Qualification
    val qualified: Boolean,
    val qualificationReason: String? = null,

    // Tokenization
    val tokens: List<Token> = emptyList(),

    // Pattern Matching
    val matchedPatterns: List<PatternMatch> = emptyList(),

    // Evidence
    val evidence: List<Evidence> = emptyList(),

    // Classification
    val messageType: MessageType = MessageType.UNKNOWN,

    // Extraction

    val amount: Money? = null,
    val amountConfidence: Float = 0f,

    val direction: Direction = Direction.UNKNOWN,
    val directionConfidence: Float = 0f,

    val paymentMode: PaymentMode = PaymentMode.UNKNOWN,
    val paymentModeConfidence: Float = 0f,

    val category: CategoryId = CategoryId.UNKNOWN,
    val categoryConfidence: Float = 0f,
    val categoryEvidence: String,

    val account: String? = null,
    val merchant: String? = null,
    val merchantSourceSegment: String?,
    val merchantConfidence: Float = 0f,
    val merchantAnchorUsed: String? = null,
    val reference: String? = null,
    val messageSegments: List<MessageSegment> = emptyList()
)