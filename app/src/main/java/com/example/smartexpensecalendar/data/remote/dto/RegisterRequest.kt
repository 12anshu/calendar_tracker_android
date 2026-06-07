package com.example.smartexpensecalendar.data.remote.dto

data class RegisterRequest(
    val tenant_slug: String,
    val email: String,
    val password: String,
    val first_name: String?,
    val last_name: String?
)
