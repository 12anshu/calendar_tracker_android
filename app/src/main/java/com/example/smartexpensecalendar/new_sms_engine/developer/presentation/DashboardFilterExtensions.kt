package com.example.smartexpensecalendar.new_sms_engine.developer.presentation

fun DashboardFilter.displayName(): String =
    when (this) {

        DashboardFilter.QUALIFIED -> "Qualified"
        DashboardFilter.NOT_QUALIFIED -> "Not Qualified"
        
        is DashboardFilter.MESSAGE_TYPE -> "Type: ${this.type.name}"
        is DashboardFilter.EVIDENCE_TYPE -> "Evidence: ${this.type.name}"
        is DashboardFilter.PATTERN -> "Pattern: ${this.name}"
        is DashboardFilter.TOKEN_CATEGORY -> "Category: ${this.category.name}"

        DashboardFilter.AMOUNT_FOUND -> "Amount Found"
        DashboardFilter.AMOUNT_MISSING -> "Amount Missing"
        DashboardFilter.MERCHANT_FOUND -> "Merchant Found"
        DashboardFilter.MERCHANT_MISSING -> "Merchant Missing"
        DashboardFilter.DIRECTION_FOUND -> "Direction Found"
        DashboardFilter.DIRECTION_MISSING -> "Direction Missing"
        DashboardFilter.MODE_FOUND -> "Mode Found"
        DashboardFilter.MODE_MISSING -> "Mode Missing"
        DashboardFilter.ACCOUNT_FOUND -> "Account Found"
        DashboardFilter.ACCOUNT_MISSING -> "Account Missing"
        DashboardFilter.REFERENCE_FOUND -> "Reference Found"
        DashboardFilter.REFERENCE_MISSING -> "Reference Missing"

        DashboardFilter.DEBIT -> "Debit"
        DashboardFilter.CREDIT -> "Credit"
        DashboardFilter.UNKNOWN_DIRECTION -> "Direction Unknown"

        is DashboardFilter.PAYMENT_MODE ->
            this.mode.name.replace("_", " ").lowercase().capitalize()
    }

private fun String.capitalize(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
