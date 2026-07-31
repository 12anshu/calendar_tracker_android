package com.example.smartexpensecalendar.new_sms_engine.category.definition

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryGroup
import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId
import com.example.smartexpensecalendar.new_sms_engine.common.enums.category.CategoryKeywords

object DefaultCategories {

    val ALL = listOf(

        // Expense

        CategoryDefinition(
            CategoryId.FOOD,
            CategoryGroup.EXPENSE,
            CategoryKeywords.FOOD
        ),

        CategoryDefinition(
            CategoryId.GROCERIES,
            CategoryGroup.EXPENSE,
            CategoryKeywords.GROCERIES
        ),

        CategoryDefinition(
            CategoryId.SHOPPING,
            CategoryGroup.EXPENSE,
            CategoryKeywords.SHOPPING
        ),

        CategoryDefinition(
            CategoryId.FUEL,
            CategoryGroup.EXPENSE,
            CategoryKeywords.FUEL
        ),

        CategoryDefinition(
            CategoryId.TRAVEL,
            CategoryGroup.EXPENSE,
            CategoryKeywords.TRAVEL
        ),

        CategoryDefinition(
            CategoryId.UTILITIES,
            CategoryGroup.EXPENSE,
            CategoryKeywords.UTILITIES
        ),

        CategoryDefinition(
            CategoryId.HEALTHCARE,
            CategoryGroup.EXPENSE,
            CategoryKeywords.HEALTHCARE
        ),

        CategoryDefinition(
            CategoryId.ENTERTAINMENT,
            CategoryGroup.EXPENSE,
            CategoryKeywords.ENTERTAINMENT
        ),

        CategoryDefinition(
            CategoryId.EDUCATION,
            CategoryGroup.EXPENSE,
            CategoryKeywords.EDUCATION
        ),

        CategoryDefinition(
            CategoryId.RENT,
            CategoryGroup.EXPENSE,
            CategoryKeywords.RENT
        ),

        CategoryDefinition(
            CategoryId.INSURANCE,
            CategoryGroup.EXPENSE,
            CategoryKeywords.INSURANCE
        ),

        CategoryDefinition(
            CategoryId.INVESTMENT,
            CategoryGroup.EXPENSE,
            CategoryKeywords.INVESTMENT
        ),

        CategoryDefinition(
            CategoryId.BANK_CHARGES,
            CategoryGroup.EXPENSE,
            CategoryKeywords.BANK_CHARGES
        ),

        // Income

        CategoryDefinition(
            CategoryId.SALARY,
            CategoryGroup.INCOME,
            CategoryKeywords.SALARY
        ),

        CategoryDefinition(
            CategoryId.REFUND,
            CategoryGroup.INCOME,
            CategoryKeywords.REFUND
        ),

        CategoryDefinition(
            CategoryId.CASHBACK,
            CategoryGroup.INCOME,
            CategoryKeywords.CASHBACK
        ),

        CategoryDefinition(
            CategoryId.INTEREST,
            CategoryGroup.INCOME,
            CategoryKeywords.INTEREST
        ),

        // Transfer

        CategoryDefinition(
            CategoryId.CREDIT_CARD_BILL,
            CategoryGroup.TRANSFER,
            CategoryKeywords.CREDIT_CARD_BILL
        ),

        CategoryDefinition(
            CategoryId.LOAN_EMI,
            CategoryGroup.TRANSFER,
            CategoryKeywords.LOAN_EMI
        ),

        CategoryDefinition(
            CategoryId.WALLET_LOAD,
            CategoryGroup.TRANSFER,
            CategoryKeywords.WALLET_LOAD
        )
    )
}