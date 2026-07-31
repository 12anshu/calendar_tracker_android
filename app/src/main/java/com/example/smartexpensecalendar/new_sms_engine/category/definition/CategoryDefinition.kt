package com.example.smartexpensecalendar.new_sms_engine.category.definition

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryGroup
import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId

data class CategoryDefinition(

    val categoryId: CategoryId,

    val group: CategoryGroup,

    val keywords: Set<String>
) {
    fun matches(message: String): Boolean {
        return keywords.any { keyword ->
            message.contains(
                keyword,
                ignoreCase = true
            )
        }
    }
}
