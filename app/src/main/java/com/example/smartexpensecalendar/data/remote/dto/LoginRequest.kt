package com.example.smartexpensecalendar.data.remote.dto

data class LoginRequest(
    val tenant_slug: String,
    val email: String,
    val password: String
)