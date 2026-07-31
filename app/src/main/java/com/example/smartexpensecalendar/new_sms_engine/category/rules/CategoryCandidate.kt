package com.example.smartexpensecalendar.new_sms_engine.category.rules

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId

data class CategoryCandidate(

    val categoryId: CategoryId,

    val confidence: Float,

    val evidence: List<String>
)