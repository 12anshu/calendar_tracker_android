package com.example.smartexpensecalendar.domain.model

data class Category(
    val name: String,
    val iconName: String? = null // For UI
)

object DefaultCategories {
    val list = listOf(
        "Food",
        "Groceries",
        "Online Shopping",
        "Bill Payment",
        "Travel",
        "Fuel",
        "Entertainment",
        "Medical",
        "Utilities",
        "Rent",
        "Miscellaneous"
    )
}
