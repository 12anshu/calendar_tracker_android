package com.example.smartexpensecalendar.sms.config

data class MerchantDefinition(
    val canonicalName: String,
    val category: String,
    val aliases: Set<String>
)

object MerchantRegistry {

    val merchants = listOf(
        // --- FOOD & DINING ---
        MerchantDefinition("Swiggy", "Food", setOf("SWIGGY", "BUNDL", "INSTAMART", "SWIGGY FOOD")),
        MerchantDefinition("Zomato", "Food", setOf("ZOMATO", "PYU ZOMATO", "ETERNALLIM")),
        MerchantDefinition("Dominos", "Food", setOf("DOMINOS", "DOMINO")),
        MerchantDefinition("KFC", "Food", setOf("KFC")),
        MerchantDefinition("McDonald's", "Food", setOf("MCDONALD")),
        MerchantDefinition("Starbucks", "Food", setOf("STARBUCKS", "TATA STARBUCKS")),
        MerchantDefinition("EatClub", "Food", setOf("EATCLUB", "FAASOS", "BEHROUZ")),
        
        // --- GROCERIES ---
        MerchantDefinition("Zepto", "Groceries", setOf("ZEPTO", "ZEPTONOW")),
        MerchantDefinition("Blinkit", "Groceries", setOf("BLINKIT", "GROFERS")),
        MerchantDefinition("BigBasket", "Groceries", setOf("BIGBASKET", "BBDAILY")),
        MerchantDefinition("DMart", "Groceries", setOf("DMART", "AVENUE SUPERMARTS")),
        MerchantDefinition("Reliance Fresh", "Groceries", setOf("RELIANCE FRESH", "JIOMART")),

        // --- TRAVEL & TRANSPORT ---
        MerchantDefinition("Uber", "Travel", setOf("UBER", "UBER INDIA")),
        MerchantDefinition("Ola", "Travel", setOf("OLA", "ANI TECHNOLOGIES")),
        MerchantDefinition("Rapido", "Travel", setOf("RAPIDO", "ROPPEN")),
        MerchantDefinition("IRCTC", "Travel", setOf("IRCTC")),
        MerchantDefinition("MakeMyTrip", "Travel", setOf("MAKEMYTRIP", "MMT")),
        MerchantDefinition("IndiGo", "Travel", setOf("INDIGO", "INTERGLOBE")),

        // --- ONLINE SHOPPING ---
        MerchantDefinition("Amazon", "Online Shopping", setOf("AMAZON", "AMZN", "AMAZON PAY")),
        MerchantDefinition("Flipkart", "Online Shopping", setOf("FLIPKART")),
        MerchantDefinition("Myntra", "Online Shopping", setOf("MYNTRA")),
        MerchantDefinition("Ajio", "Online Shopping", setOf("AJIO")),
        MerchantDefinition("Nykaa", "Online Shopping", setOf("NYKAA")),

        // --- UTILITIES & BILLS ---
        MerchantDefinition("Airtel", "Bill Payment", setOf("AIRTEL", "BHARTI AIRTEL")),
        MerchantDefinition("Jio", "Bill Payment", setOf("JIO", "RELIANCE JIO")),
        MerchantDefinition("VI", "Bill Payment", setOf("VODAFONE", "IDEA", "VIL")),
        MerchantDefinition("BESCOM", "Utilities", setOf("BESCOM")),
        MerchantDefinition("Indane", "Utilities", setOf("INDANE", "LPG")),

        // --- ENTERTAINMENT ---
        MerchantDefinition("Netflix", "Entertainment", setOf("NETFLIX")),
        MerchantDefinition("Spotify", "Entertainment", setOf("SPOTIFY")),
        MerchantDefinition("Hotstar", "Entertainment", setOf("HOTSTAR", "DISNEY")),
        MerchantDefinition("BookMyShow", "Entertainment", setOf("BOOKMYSHOW", "BMS")),
        MerchantDefinition("Google Play", "Entertainment", setOf("GOOGLE PLAY", "GPLAY")),

        // --- HEALTH ---
        MerchantDefinition("1mg", "Medical", setOf("1MG", "TATA 1MG")),
        MerchantDefinition("Apollo", "Medical", setOf("APOLLO")),
        MerchantDefinition("Netmeds", "Medical", setOf("NETMEDS")),
        MerchantDefinition("Pharmeasy", "Medical", setOf("PHARMEASY")),
        MerchantDefinition("Medplus", "Medical", setOf("MEDPLUS")),
        
        // --- FUEL ---
        MerchantDefinition("Shell", "Fuel", setOf("SHELL", "SHELL INDIA")),
        MerchantDefinition("HPCL", "Fuel", setOf("HPCL", "HINDUSTAN PETROLEUM")),
        MerchantDefinition("BPCL", "Fuel", setOf("BPCL", "BHARAT PETROLEUM")),
        MerchantDefinition("IOCL", "Fuel", setOf("IOCL", "INDIAN OIL")),

        // --- SERVICES ---
        MerchantDefinition("Urban Company", "Services", setOf("URBANCLAP", "URBAN COMPANY", "UC")),
        MerchantDefinition("MyGate", "Rent & Maintenance", setOf("MYGATE", "VIVIO")),
        MerchantDefinition("NoBroker", "Rent & Maintenance", setOf("NOBROKER")),

        // --- SHOPPING & LIFESTYLE ---
        MerchantDefinition("Decathlon", "Shopping", setOf("DECATHLON")),
        MerchantDefinition("Shoppers Stop", "Shopping", setOf("SHOPPERS STOP")),
        MerchantDefinition("Westside", "Shopping", setOf("WESTSIDE", "TRENT")),
        MerchantDefinition("LIFESTYLE", "Shopping", setOf("LIFESTYLE")),
        MerchantDefinition("Uniqlo", "Shopping", setOf("UNIQLO")),

        // --- INVESTMENTS & FINTECH ---
        MerchantDefinition("Groww", "Investment", setOf("GROWW", "NEXTBILLION")),
        MerchantDefinition("Zerodha", "Investment", setOf("ZERODHA")),
        MerchantDefinition("Upstox", "Investment", setOf("UPSTOX", "RKSV")),
        MerchantDefinition("INDmoney", "Investment", setOf("INDMONEY", "FINLIBERTY")),
        MerchantDefinition("Smallcase", "Investment", setOf("SMALLCASE")),

        // --- OTHERS ---
        MerchantDefinition("Cred", "Bill Payment", setOf("CRED", "CREDCLUB")),
        MerchantDefinition("Paytm", "Miscellaneous", setOf("PAYTM")),
        MerchantDefinition("PhonePe", "Miscellaneous", setOf("PHONEPE")),
        MerchantDefinition("Google Pay", "Miscellaneous", setOf("GOOGLE PAY", "GPAY")),
        MerchantDefinition("Tata Sky", "Utilities", setOf("TATA SKY", "TATA PLAY")),
        MerchantDefinition("Airtel DTH", "Utilities", setOf("AIRTEL DTH", "AIRTEL Digital TV"))
    )
}
