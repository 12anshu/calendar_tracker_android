package com.example.smartexpensecalendar.new_sms_engine.common.regex

object CorporateSuffixRegex {

    val CORPORATE_SUFFIX_REGEX =
        Regex(
            """\b(PVT|PRIVATE|LTD|LIMITED|LLP|LLC|INC|CORP|CORPORATION|COMPANY|CO)\b\.?""",
            RegexOption.IGNORE_CASE
        )
}