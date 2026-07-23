package com.example.smartexpensecalendar.new_sms_engine.developer.presentation

import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageType
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

sealed class DashboardFilter {

    object QUALIFIED : DashboardFilter()

    object NOT_QUALIFIED : DashboardFilter()

    // Message Type
    data class MESSAGE_TYPE(val type: MessageType) : DashboardFilter()

    // Evidence
    data class EVIDENCE_TYPE(val type: EvidenceType) : DashboardFilter()

    // Pattern
    data class PATTERN(val name: String) : DashboardFilter()

    // Token Category
    data class TOKEN_CATEGORY(val category: TokenCategory) : DashboardFilter()

    // Extraction
    object AMOUNT_FOUND : DashboardFilter()
    object AMOUNT_MISSING : DashboardFilter()
    object MERCHANT_FOUND : DashboardFilter()
    object MERCHANT_MISSING : DashboardFilter()
    object DIRECTION_FOUND : DashboardFilter()
    object DIRECTION_MISSING : DashboardFilter()
    object MODE_FOUND : DashboardFilter()
    object MODE_MISSING : DashboardFilter()
    object ACCOUNT_FOUND : DashboardFilter()
    object ACCOUNT_MISSING : DashboardFilter()
    object REFERENCE_FOUND : DashboardFilter()
    object REFERENCE_MISSING : DashboardFilter()

    // Legacy/Combined
    object DEBIT : DashboardFilter()
    object CREDIT : DashboardFilter()
    object UNKNOWN_DIRECTION : DashboardFilter()

    data class PAYMENT_MODE(val mode: PaymentMode) : DashboardFilter()
}
