package com.example.smartexpensecalendar.sms.config

object SenderRegistry {

    // Mapping of ShortCodes to Friendly Names
    val bankCodeMap = mapOf(
        "HDFCBK" to "HDFC Bank",
        "HDFCBANK" to "HDFC Bank",
        "HDFCBN" to "HDFC Bank",
        "ICICIB" to "ICICI Bank",
        "ICICIA" to "ICICI Bank",
        "ICICIP" to "ICICI Bank",
        "AXISBK" to "Axis Bank",
        "AXISBN" to "Axis Bank",
        "AXISCR" to "Axis Card",
        "SBIINB" to "SBI Bank",
        "SBIPSG" to "SBI Bank",
        "SBICRD" to "SBI Card",
        "SBIOMA" to "SBI Bank",
        "KOTAKB" to "Kotak Bank",
        "KOTAKM" to "Kotak Bank",
        "YESBNK" to "Yes Bank",
        "YESB" to "Yes Bank",
        "IDFCFB" to "IDFC First",
        "IDFCSM" to "IDFC First",
        "RBLBNK" to "RBL Bank",
        "RBLCRD" to "RBL Bank",
        "FEDBNK" to "Federal Bank",
        "DBSBNK" to "DBS Bank",
        "DIGBNK" to "DBS Bank",
        "PNBSMS" to "PNB Bank",
        "CANBNK" to "Canara Bank",
        "BOBMSG" to "BOB Bank",
        "BARODA" to "BOB Bank",
        "UBIINB" to "Union Bank",
        "UNIBNK" to "Union Bank",
        "INDYUS" to "IndusInd",
        "INDSIN" to "IndusInd",
        "CITIBK" to "Citi Bank",
        "CITI" to "Citi Bank",
        "AMEXIN" to "Amex",
        "SCBSMS" to "StanChart",
        "HSBCIN" to "HSBC",
        "BANDHN" to "Bandhan",
        "KVBBK" to "KVB Bank",
        "SOUTHB" to "South Ind Bank",
        "KARBNK" to "Karnataka Bank",
        "AUBANK" to "AU Bank",
        "PAYTM" to "Paytm Bank",
        "AIRTEL" to "Airtel Bank",
        "JIOBNK" to "Jio Bank"
    )

    // Preserve existing sets for backward compatibility with SenderValidationEngine
    val bankSenders = bankCodeMap.filter { !it.key.contains("CRD") && !it.key.contains("CR") }.keys
    
    val cardSenders = bankCodeMap.filter { it.key.contains("CRD") || it.key.contains("CR") }.keys

    val upiSenders = setOf(
        "PAYTM",
        "PHONEPE",
        "GPAY",
        "GOOGLEPAY",
        "BHIM",
        "MOBIKWIK"
    )
}
