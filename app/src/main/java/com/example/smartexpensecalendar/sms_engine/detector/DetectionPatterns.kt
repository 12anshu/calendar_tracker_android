package com.example.smartexpensecalendar.sms_engine.detector

object DetectionPatterns {

    val amountRegex = Regex(
        "(?:₹|RS\\.?|INR|RS|AMT|AMOUNT)\\s*([\\d,]+(?:\\.\\d{1,2})?)",
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
}
