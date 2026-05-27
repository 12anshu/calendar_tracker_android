package com.example.smartexpensecalendar.domain.model

data class Category(
    val name: String,
    val iconName: String? = null // For UI
)

object DefaultCategories {
    val list = listOf(
        "Food",
        "Travel",
        "Grocery",
        "Fuel",
        "Shopping",
        "Entertainment",
        "Medical",
        "Utilities",
        "Rent",
        "Subscription",
        "Investment",
        "Miscellaneous"
    )
}
