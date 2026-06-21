package com.example.smartexpensecalendar.sms_engine.detector

object DetectionPatterns {

    // --- ATOMIC: BANK ENTITIES ---
    val BANKS = listOf(
        "HDFC", "ICICI", "AXIS", "SBI", "KOTAK", "YES BANK", "IDFC", "RBL", "FEDERAL",
        "CITI", "HSBC", "SCB", "AMEX", "DBS", "DIGIBANK", "STANDARD CHARTERED",
        "BARODA", "PNB", "CANARA", "UNION BANK", "INDIAN BANK", "BOI", "UCO", "BOM",
        "IDBI", "IOB", "PSB", "AU BANK", "EQUITAS", "UJJIVAN", "FINO", "NSDL", "IPPB",
        "PAYTM", "AIRTEL", "JIO", "SARASWAT", "COSMOS", "TJSB", "SVC", "NKGSB"
    )

    // --- ATOMIC: INSTRUMENTS ---
    val INSTRUMENT_BANK = listOf("BANK", "BK", "BNK", "BRANCH", "BR.")
    val INSTRUMENT_CARD = listOf("CARD", "CRD", "CR", "CD", "DEBIT", "CREDIT", "DC", "CC", "VISA", "MASTERCARD", "RUPAY")
    val INSTRUMENT_ACCOUNT = listOf("A/C", "ACC", "ACCT", "ACCOUNT", "ACCNT", "SAVINGS", "CURRENT", "LOAN", "OVERDRAFT", "OD", "A/C NO", "ACCOUNT NO")
    val INSTRUMENT_MEAL = listOf("MEAL", "FOOD", "BENEFIT", "VOUCHER", "SODEXO", "PLUXEE", "ZETA", "EDENRED", "SWILE")
    val INSTRUMENT_WALLET = listOf("WALLET", "WLT", "CASH", "PREPAID", "AMAZON PAY", "PAYTM", "PHONEPE", "MOBIKWIK")
    val INSTRUMENT_POINTS = listOf("POINTS", "REWARDS", "RP", "COINS", "MILES", "CASHBACK")

    // --- ATOMIC: STRUCTURAL CONNECTORS ---
    val SUFFIX_MARKERS = listOf("ENDING", "ENDING IN", "ENDING WITH", "XX", "X", "NO.", "NO", "#")
    val PREPOSITIONS = listOf("AT", "ON", "TO", "VIA", "FOR", "USING", "THROUGH", "TOWARDS", "BY", "FROM")

    // --- ATOMIC: STATISTICAL HINTS ---
    val DEBIT_HINTS = listOf("TXN", "RS.", "INR")

    // --- ATOMIC: MODE INDICATORS ---
    val MODE_UPI = listOf("UPI", "VPA", "GPAY", "GOOGLE PAY", "PHONEPE", "PAYTM", "BHIM", "MOBIKWIK", "@YBL", "@OKSBI", "@OKHDFCBANK", "@PAYTM", "@APL")
    val MODE_BANK_TRANSFER = listOf("IMPS", "NEFT", "RTGS", "FUND TRANSFER", "MONEY TRANSFER", "BANK TRANSFER", "BENEFICIARY")
    val MODE_AUTO_DEBIT = listOf("AUTOPAY", "AUTO DEBIT", "E-MANDATE", "STANDING INSTRUCTION", "ECS", "SI DEBIT")
    val MODE_CASH = listOf("CASH", "ATM", "WITHDRAWAL", "DEPOSIT")

    // --- ATOMIC: VERBS ---
    val VERBS_STATUS = listOf("SUCCESSFUL", "COMPLETED", "PROCESSED", "SUBMITTED", "PENDING", "FAILED", "DECLINED", "CANCELLED")

    // --- ATOMIC: TIME & AUXILIARY ---
    val AUX_PRESENT = listOf("IS", "HAS", "DONE", "NOW")
    val AUX_PAST = listOf("WAS", "HAD")
    val AUX_FUTURE = listOf("WILL", "DUE", "UPCOMING", "REMINDER")


    // --- COMPOSITE: PHRASES (Event Types) ---
    val PHRASES_REFUND = listOf("REFUND INITIATED", "REFUND PROCESSED", "REFUND COMPLETED", "REFUND SUCCESSFUL", "REFUND CREDITED", "AMOUNT REVERSED", "REVERSAL PROCESSED", "CHARGE REVERSAL")
    val PHRASES_CC_PAYMENT = listOf("CREDIT CARD PAYMENT", "CARD BILL PAYMENT", "PAID TOWARDS YOUR CREDIT CARD", "PAYMENT RECEIVED FOR YOUR CARD", "CREDIT CARD DUES PAID", "PAYMENT RECEIVED ON", "RECEIVED TOWARDS YOUR CREDIT CARD", "PAYMENT OF {CUR}", "PAYMENT HAS BEEN RECEIVED", "RECEIVED ON CREDIT CARD")
    val PHRASES_TRANSFER = listOf("FUND TRANSFER", "MONEY TRANSFER", "ACCOUNT TO ACCOUNT", "BENEFICIARY TRANSFER", "TRANSFERRED TO", "TRANSFERRED FROM", "IMPS TRANSFER", "NEFT TRANSFER", "RTGS TRANSFER", "UPI TRANSFER")
    val PHRASES_EMI = listOf("EMI PAYMENT", "EMI DEBITED", "LOAN INSTALLMENT", "INSTALLMENT DEDUCTED", "EMI DEDUCTED", "TOWARDS LOAN", "LOAN REPAYMENT", "EMI PROCESSED")
    val PHRASES_INVESTMENT = listOf("MUTUAL FUND PURCHASE", "SIP INSTALLMENT", "SIP PAYMENT", "FD BOOKED", "RD INSTALLMENT", "NPS CONTRIBUTION", "DEMAT PURCHASE", "STOCK PURCHASE")

    // --- COMPOSITE: REGEX GENERATORS ---

    /**
     * Matches a bank structural mention like "HDFC Bank A/c XX1234" or "AXIS CARD 4567"
     */
    val bankStructureRegex: Regex by lazy {
        val banks = BANKS.joinToString("|")
        val instruments = (INSTRUMENT_BANK + INSTRUMENT_CARD + INSTRUMENT_ACCOUNT + INSTRUMENT_MEAL).distinct().joinToString("|")
        val suffixes = SUFFIX_MARKERS.joinToString("|")
        // Matches: BankName [Instrument] [Suffix] [Numbers]
        Regex("(?i)\\b($banks)\\b(?:\\s+($instruments))?(?:\\s+($suffixes))?\\s*[\\(\\[:\\-#\\s]*\\s*[*xX]*\\s*(\\d{2,4})\\b")
    }

    /**
     * Standard Account Suffix Extractor
     */
    val accountSuffixRegex = Regex("(?i)(?:Card|Account|A/c|Acct|Acc|XX|X|No|ending|ending\\sin)\\s*[\\(\\[:\\-#\\s]*\\s*[*xX]*\\s*(\\d{2,4})\\b")

    /**
     * Matches standalone bank entities like "DBS BANK" or "ICICI BK"
     */
    val bankEntityRegex: Regex by lazy {
        val banks = BANKS.joinToString("|")
        val types = INSTRUMENT_BANK.joinToString("|")
        Regex("(?i)\\b($banks)(?:\\s+($types))?\\b")
    }

    val amountRegex = Regex("(?i)(?:RS\\.?|INR|₹|RE\\.?|AMT|AMOUNT)\\s*([0-9,]+(?:\\.\\d{2})?)")
    
    val upiRegex = listOf(
        Regex("(?i)BY\\s+UPI"),
        Regex("(?i)VIA\\s+UPI"),
        Regex("(?i)[a-zA-Z0-9._-]+@[a-zA-Z]{3,}") // VPA Pattern
    )

    // Patterns for filtering out Outward Transfer Confirmations
    val outwardConfirmationRegex = listOf(
        Regex("TO\\s+[A-Za-z0-9 .]+\\s+IS\\s+SUCCESSFULLY\\s+CREDITED"),
        Regex("TRANSFER\\s+OF\\s+INR\\s+[0-9.]+\\s+TO\\s+[A-Za-z0-9 .]+\\s+IS\\s+SUCCESSFUL")
    )

    // Quality Scoring Signals
    val qualitySignalsTier2 = Regex("UPI\\s*REF|VPA|PAID\\s*VIA|SUCCESS|TXN\\s*ID|REF#")
    val qualitySignalsTier3 = Regex("VALUE\\s*DATE|AVL\\s*BAL|AVAILABLE\\s*BALANCE|A/C\\s*ENDING|CREDITED\\s*TO\\s*YOUR\\s*CARD")

    // Amount Scoring signals
    val amountScoringVerbs = Regex("SPENT|PAID|DEBITED|PURCHASE|CREDITED|RECEIVED|TXN|TRANSFER")
    val amountReportingSignals = Regex("LIMIT|BALANCE|AVL|BAL|OUTSTANDING|TOTAL")

    val explicitAnchors = listOf(
        Regex("(?i)BANK"), Regex("(?i)A/C"), Regex("(?i)ACCOUNT"), 
        Regex("(?i)CARD"), Regex("(?i)ENDING"), Regex("(?i)XX\\d{2,4}")
    )

    val reportingIdentifiers = listOf(
        Regex("(?i)BAL(?:ANCE)?\\s+IS"),
        Regex("(?i)AVL\\s+BAL"),
        Regex("(?i)LIMIT\\s+IS"),
        Regex("(?i)STMT\\s+GEN")
    )

    // Keywords that indicate a message is NOT a transaction (Kill switches)
    val failureKillSwitches = listOf(
        Regex("(?i)FAILED"), Regex("(?i)DECLINED"), Regex("(?i)CANCELLED"), 
        Regex("(?i)REJECTED"), Regex("(?i)INSUFFICIENT"), Regex("(?i)REVERSED")
    )
    
    val refundOverrides = listOf(
        Regex("(?i)REFUND"), Regex("(?i)REVERSAL")
    )

    // Broad anchors for identifying the start of merchant strings
    val broadAnchors = (PREPOSITIONS + listOf("SPENT", "PURCHASED", "PAID")).distinct()

    // --- CATEGORY KEYWORDS (Fallback Logic) ---
    val CAT_FUEL = setOf("FUEL", "PETROL", "SHELL", "HPCL", "IOCL", "INDIANOIL", "BHARAT PETROLEUM", "BPCL")
    val CAT_RENT = setOf("RENT", "FLAT", "MAINTENANCE", "HOUSING", "MYGATE", "NOBROKER")
    val CAT_MEDICAL = setOf("HOSPITAL", "PHARMACY", "CLINIC", "DOCTOR", "HEALTHCARE", "DIAGNOSTIC", "MEDPLUS", "APOLLO")
    val CAT_ENTERTAINMENT = setOf("NETFLIX", "SPOTIFY", "DISNEY", "HOTSTAR", "YOUTUBE", "PRIME", "TICKET", "PVR", "INOX")
    val CAT_BILL_PAYMENT = setOf("AIRTEL", "JIO", "VODAFONE", "IDEA", "ELECTRICITY", "WATER", "GAS", "BESCOM", "RECHARGE")
    val CAT_SHOPPING = setOf("AMAZON", "FLIPKART", "MYNTRA", "AJIO", "RETAIL", "FASHION", "ZIVAME", "NYKAA", "LIFESTYLE", "PANTALOONS", "MAX")
    val CAT_TRAVEL = setOf("UBER", "OLA", "RAPIDO", "METRO", "RAIL", "IRCTC", "FLIGHT", "MAKEMYTRIP", "INDIGO", "AIR INDIA")
    val CAT_FOOD = setOf("SWIGGY", "ZOMATO", "FOOD", "RESTAURANT", "CAFE", "BAKERY", "EATCLUB", "DOMINOS", "PIZZA", "BURGER", "KFC", "MCDONALD")
    val CAT_GROCERIES = setOf("BIGBASKET", "BLINKIT", "ZEPTO", "DMART", "GROCERY", "SUPERMARKET", "RELIANCE FRESH", "JIOMART", "BBDAILY")
    val CAT_INVESTMENT = setOf("INVEST", "MUTUAL", "SIP", "STOCK", "GROWW", "ZERODHA", "TRADING", "COIN", "SMALLCASE", "INDMONEY")
}
