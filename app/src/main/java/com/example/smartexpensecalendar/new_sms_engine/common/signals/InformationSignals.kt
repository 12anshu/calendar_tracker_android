package com.example.smartexpensecalendar.new_sms_engine.common.signals

import kotlin.collections.flatten

object InformationSignals {

    /**
     * Statements / documents.
     */
    val STATEMENT_SIGNALS = setOf(

        "STATEMENT",
        "ESTATEMENT",
        "E-STATEMENT",
        "SUMMARY"
    )

    /**
     * Account lifecycle / maintenance.
     */
    val ACCOUNT_UPDATE_SIGNALS = setOf(

        "KYC",
        "PROFILE",
        "NOMINEE",
        "EMAIL",
        "MOBILE",
        "ADDRESS",
        "CKYC",
        "AADHAAR",
        "PAN",
        "PASSWORD"
    )

    /**
     * Security notifications.
     */
    val SECURITY_SIGNALS = setOf(

        "SECURITY",
        "FRAUD",
        "SAFE",
        "PROTECT"
    )

    /**
     * Rewards / loyalty.
     */
    val REWARD_SIGNALS = setOf(

        "REWARD",
        "REWARDS",
        "POINTS",
        "MILES",
        "NEUCOINS",
        "NEUCOIN",
        "COINS",
        "COIN"
    )

    /**
     * Government / retirement information.
     */
    val RETIREMENT_SIGNALS = setOf(

        "PF",
        "EPF",
        "EPFO",
        "UAN",
        "PENSION",
        "NPS",
        "EPS"
    )

    /**
     * Investment / portfolio updates.
     */
    val INVESTMENT_SIGNALS = setOf(

        "MUTUAL",
        "FUND",
        "NAV",
        "DIVIDEND",
        "PORTFOLIO",
        "SIP",
        "STP",
        "SWP",
        "IPO",
        "FOLIO"
    )

    /**
     * Tax related updates.
     */
    val TAX_SIGNALS = setOf(

        "TDS",
        "GST",
        "PAN",
        "FORM16",
        "FORM26AS",
        "AIS",
    )

    /**
     * Compliance / documentation.
     */
    val DOCUMENT_SIGNALS = setOf(

        "CERTIFICATE",
        "DOCUMENT",
        "RECEIPT",
        "ACKNOWLEDGEMENT",
        "REFERENCE",
        "REFERENCEID"
    )

    /**
     * Loan application lifecycle.
     */
    val LOAN_LIFECYCLE_SIGNALS = setOf(

        "APPLICATION",
        "LOAN"
    )

    /**
     * Request / application processing state.
     */
    val PROCESS_STATE_SIGNALS = setOf(

        "RECEIVED",

        "SUBMITTED",

        "PROCESSING",
        "PROCESSED",

        "UNDERPROCESS",
        "UNDER_PROCESS",

        "UNDER REVIEW",
        "UNDER_REVIEW",

        "REVIEW",

        "APPROVED",

        "DECLINED",

        "REJECTED",

        "VERIFIED",

        "PENDING",

        "GENERATED",

        "CREATED",

        "REGISTERED",

        "DISPATCHED",

        "DELIVERED",

        "READY"
    )

    val ALL = setOf(
        STATEMENT_SIGNALS,
        ACCOUNT_UPDATE_SIGNALS,
        SECURITY_SIGNALS,
        REWARD_SIGNALS,
        RETIREMENT_SIGNALS,
        INVESTMENT_SIGNALS,
        TAX_SIGNALS,
        DOCUMENT_SIGNALS,
        LOAN_LIFECYCLE_SIGNALS,
        PROCESS_STATE_SIGNALS
    ).flatten().toSet()
}