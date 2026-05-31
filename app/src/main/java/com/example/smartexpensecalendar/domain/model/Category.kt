package com.example.smartexpensecalendar.domain.model

data class Category(
    val name: String,
    val iconName: String? = null // For UI
)

object DefaultCategories {
    val list = listOf(
        "Food",
        "Groceries",
        "UPI / Digital",
        "Online Shopping",
        "Bill Payment",
        "Travel",
        "Fuel",
        "Entertainment",
        "Medical",
        "Utilities",
        "Rent",
        "Investments",
        "Insurance",
        "EMI",
        "Loan",
        "Subscription",
        "Miscellaneous"
    )
}
