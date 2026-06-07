package com.example.smartexpensecalendar.features.developer_tools.engine

import java.util.regex.Pattern

object SMSPatternGroupingEngine {

    private val amountPattern = Pattern.compile("\\d+(?:\\.\\d{1,2})?")
    private val accountPattern = Pattern.compile("(?i)(?:A/c|Acct|Card|XX|X|No|no\\.?)\\s*\\*?([0-9]{4,})")
    private val datePattern = Pattern.compile("\\d{1,2}[-/]\\d{1,2}[-/]\\d{2,4}")
    private val timePattern = Pattern.compile("\\d{1,2}:\\d{2}(?::\\d{2})?")
    private val upiPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z]+")
    private val refPattern = Pattern.compile("(?i)(?:Ref|ID|No)[: ]+([a-zA-Z0-9]{8,})")

    fun generateTemplate(message: String): String {
        var template = message
        
        // Order matters to avoid overlapping replacements
        
        // 1. UPI
        template = upiPattern.matcher(template).replaceAll("{UPI}")
        
        // 2. Dates
        template = datePattern.matcher(template).replaceAll("{DATE}")
        
        // 3. Times
        template = timePattern.matcher(template).replaceAll("{TIME}")
        
        // 4. Accounts
        val accMatcher = accountPattern.matcher(template)
        while (accMatcher.find()) {
            val fullMatch = accMatcher.group()
            val suffix = accMatcher.group(1)
            if (suffix != null) {
               template = template.replace(fullMatch, fullMatch.replace(suffix, "{ACCOUNT}"))
            }
        }

        // 5. Reference Numbers
        template = refPattern.matcher(template).replaceAll("{REF}")

        // 6. Amounts (Look for currency symbols first to be more accurate)
        val amountRegex = "(?i)(?:RS\\.?|INR|₹|Rs\\.?|USD|GBP|EUR)\\s*([\\d,]+(?:\\.\\d{1,2})?)"
        template = template.replace(amountRegex.toRegex(), "{CURRENCY} {AMOUNT}")
        
        // Generic numbers remaining that look like amounts
        template = template.replace("\\b\\d+\\.\\d{2}\\b".toRegex(), "{AMOUNT}")
        
        // Normalize whitespace
        template = template.replace("\\s+".toRegex(), " ").trim()

        return template
    }
}
