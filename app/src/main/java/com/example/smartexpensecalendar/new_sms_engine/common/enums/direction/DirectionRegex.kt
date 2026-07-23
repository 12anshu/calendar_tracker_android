package com.example.smartexpensecalendar.new_sms_engine.common.enums.direction

object DirectionRegex {

    val DEBIT_ACTION_REGEX =
        Regex("""(?i)\b(${DirectionPatterns.DEBIT_ACTION_PATTERN})\b""")

    val TRANSACTION_ACTION_REGEX =
        Regex("""(?i)\b(${DirectionPatterns.TRANSACTION_ACTION_PATTERN})\b""")

    val AUTO_DEBIT_REGEX =
        Regex("""(?i)\b(${DirectionPatterns.AUTO_DEBIT_PATTERN})\b""")

    val CREDIT_ACTION_REGEX =
        Regex("""(?i)\b(${DirectionPatterns.CREDIT_ACTION_PATTERN})\b""")

    val REFUND_ACTION_REGEX =
        Regex("""(?i)\b(${DirectionPatterns.REFUND_ACTION_PATTERN})\b""")

    val NEGATIVE_REGEX =
        Regex("""(?i)\b(${DirectionPatterns.NEGATIVE_PATTERN})\b""")
}