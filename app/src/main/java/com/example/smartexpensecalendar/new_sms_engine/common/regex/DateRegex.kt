package com.example.smartexpensecalendar.new_sms_engine.common.regex

object DateRegex {

    private const val CONNECTOR = """[.\-/_\s]"""

    private const val MONTH =
        """(?:\d{1,2}|[A-Za-z]{3,9})"""

    //Add regex to support 2026-07-08:10:47:45
    private val DATE_REGEX = Regex(
        """
        \b
        (
            \d{1,2}$CONNECTOR$MONTH$CONNECTOR\d{2,4}
            |
            \d{2,4}$CONNECTOR$MONTH$CONNECTOR\d{1,2}
            |
            \d{1,2}$CONNECTOR\d{1,2}$CONNECTOR\d{2,4}
        )
        \b
        """.trimIndent()
            .replace("\n", "")
            .replace(" ", ""),
        RegexOption.IGNORE_CASE
    )

    private val TIME_REGEX = Regex(
        """\b\d{1,2}:\d{2}(?::\d{2})?\b"""
    )

    private val AM_PM_REGEX = Regex(
        """^(AM|PM)$""",
        RegexOption.IGNORE_CASE
    )

    val DATE_PATTERNS = listOf(
        DATE_REGEX,
        TIME_REGEX
    )

    fun isDate(
        text: String
    ): Boolean {

        return DATE_REGEX.matches(
            normalize(text)
        )
    }

    fun isTime(
        text: String
    ): Boolean {

        return TIME_REGEX.matches(
            normalize(text)
        )
    }

    fun isAmPm(
        text: String
    ): Boolean {

        return AM_PM_REGEX.matches(
            normalize(text)
        )
    }

    private fun normalize(
        text: String
    ): String {

        return text
            .trim()
            .trimEnd('.', ',', ';', ':')
    }
}