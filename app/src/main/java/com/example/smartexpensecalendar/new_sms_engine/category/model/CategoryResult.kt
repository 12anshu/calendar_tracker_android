package com.example.smartexpensecalendar.new_sms_engine.category.model

data class CategoryResult(

    val categoryId: CategoryId,

    val confidence: Float,

    val evidence: List<String>
)