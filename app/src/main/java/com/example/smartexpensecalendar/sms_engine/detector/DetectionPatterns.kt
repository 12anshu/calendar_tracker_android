package com.example.smartexpensecalendar.sms_engine.detector

import com.example.smartexpensecalendar.sms.config.DetectionConstants

object DetectionPatterns {

    val amountRegex = Regex(
        "${DetectionConstants.CURRENCY_SYMBOLS}\\s*([\\d,]+(?:\\.\\d{1,2})?)",
        RegexOption.IGNORE_CASE
    )

    val upiRegex = listOf(
        Regex(
            "[A-Za-z0-9._-]+@[A-Za-z0-9._-]+",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "UPI\\s*ID\\s*[:\\-]?\\s*[A-Za-z0-9._-]+@[A-Za-z0-9._-]+",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "VPA\\s*[:\\-]?\\s*[A-Za-z0-9._-]+@[A-Za-z0-9._-]+",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "\\b\\d{10}@\\w+",
            RegexOption.IGNORE_CASE
        )
    )
    val accountPatterns = listOf(

        Regex(
            "ACCOUNT\\s*(NO|NUMBER|ENDING)?",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "A/C\\s*(NO|NUMBER|ENDING)?",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "\\*{4,}\\d{3,6}",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "XX\\d{3,6}",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "A/C\\sXX\\d{3,6}",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "\\*{2,}\\d{3,6}",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "A/C\\s*ENDING\\s*\\d+",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "AC\\s*(NO|NUMBER|ENDING)?\\s*\\d+",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "X{2,}\\d{3,6}",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "DEBIT CARD\\s*(ENDING)?\\s*\\d+",
            RegexOption.IGNORE_CASE
        )
    )

    val cardPatterns = listOf(

        Regex(
            "CARD\\s*[Xx*]{0,4}\\d{3,6}",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "CARD ENDING\\s*\\d+",
            RegexOption.IGNORE_CASE
        )
    )

    val balancePatterns = listOf(

        Regex(
            "CURRENT BALANCE",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "AVAILABLE BALANCE",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "\\bBAL\\b",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "AVL BAL",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "TOTAL BAL",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "LOW BALANCE",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "OUTSTANDING",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "OUTSTANDING\\s*BAL(ANCE)?",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "LEDGER\\s*BAL(ANCE)?",
            RegexOption.IGNORE_CASE
        )
    )

    val merchantPatterns = listOf(
        Regex(
            "(?:AT|TO|INFO|FOR)\\s+([A-Z0-9\\s*]+?)(?=\\s+ON|\\s+USING|\\s+LINKED|\\s+REF|\\s+DATE|\\.)",
            RegexOption.IGNORE_CASE
        ),
        Regex(
            "VPA\\s+([A-Za-z0-9._-]+@[A-Za-z0-9._-]+)",
            RegexOption.IGNORE_CASE
        )
    )

    val referenceNumberPatterns = listOf(
        Regex(
            "(?:REF|TXN|ID|UPI)\\s*(?:NO|NUMBER)?[:\\s-]*([A-Z0-9]{8,})",
            RegexOption.IGNORE_CASE
        )
    )

    // --- CONTEXTUAL INTELLIGENCE PATTERNS (V2) ---
    
    val explicitAnchors = listOf(
        Regex("A/C\\s*(NO|NUMBER|ENDING)?", RegexOption.IGNORE_CASE),
        Regex("CARD\\s*(NO|NUMBER|ENDING)?", RegexOption.IGNORE_CASE),
        Regex("DEBIT\\s*CARD|CREDIT\\s*CARD", RegexOption.IGNORE_CASE),
        Regex("ENDING\\s*IN\\s*\\d{4}", RegexOption.IGNORE_CASE)
    )

    val reportingIdentifiers = listOf(
        Regex("UNITS|NAV|FOLIO|PRAN|PORTFOLIO|VALUATION", RegexOption.IGNORE_CASE),
        Regex("CONTRIBUTION|STATEMENT|OUTSTANDING|LOYALTY|POINTS", RegexOption.IGNORE_CASE),
        Regex("LIMIT|AVAILABLE\\s*LIMIT|CREDIT\\s*LIMIT", RegexOption.IGNORE_CASE),
        Regex("PLAN|DATA\\s*BAL|VALIDITY", RegexOption.IGNORE_CASE)
    )

    val failureKillSwitches = listOf(
        Regex("FAILED", RegexOption.IGNORE_CASE),
        Regex("DECLINED", RegexOption.IGNORE_CASE),
        Regex("UNSUCCESSFUL", RegexOption.IGNORE_CASE),
        Regex("REJECTED", RegexOption.IGNORE_CASE),
        Regex("CANCELLED", RegexOption.IGNORE_CASE)
    )
    
    val refundOverrides = listOf(
        Regex("HAS\\s*BEEN\\s*REFUNDED", RegexOption.IGNORE_CASE),
        Regex("AMOUNT\\s*REVERSED", RegexOption.IGNORE_CASE),
        Regex("CREDITED\\s*BACK", RegexOption.IGNORE_CASE),
        Regex("REFUND\\s*SUCCESSFUL", RegexOption.IGNORE_CASE)
    )

    // --- BROAD ANCHORS FOR SAFETY CATCH ---
    val broadAnchors = listOf(
        Regex("A/C|ACCT|BANK|CARD|VPA|UPI\\s*ID|WALLET|ENDING|XX\\d{2,}", RegexOption.IGNORE_CASE),
        Regex("PAYTM|GPAY|PHONEPE|GOOGLE\\s*PAY|AMAZON\\s*PAY|ZETA|SODEXO", RegexOption.IGNORE_CASE),
        Regex("HDFC|ICICI|SBI|AXIS|KOTAK|HSBC|AMEX|SCB|PNB|BOB|IDFC|CITI", RegexOption.IGNORE_CASE)
    )
}
