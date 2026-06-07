package com.example.smartexpensecalendar.data.remote.dto

data class RegisterResponse(
    val success: Boolean,
    val data: LoginData?,
    val error: String?,
    val timestamp: String
)
