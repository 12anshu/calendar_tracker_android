package com.example.smartexpensecalendar.sms.detection

object DetectionPatterns {

    val amountRegex = Regex(
        "(₹|RS\\.?|INR)\\s*[\\d,]+(\\.\\d{1,2})?",
        RegexOption.IGNORE_CASE
    )

    val upiRegex = Regex(
        "[A-Za-z0-9._-]+@[A-Za-z0-9._-]+"
    )

    val accountPatterns = listOf(

        Regex(
            "A/C",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "ACCOUNT",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "\\*{2,}\\d{3,6}"
        ),

        Regex(
            "XX\\d{3,6}"
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
        )
    )
}