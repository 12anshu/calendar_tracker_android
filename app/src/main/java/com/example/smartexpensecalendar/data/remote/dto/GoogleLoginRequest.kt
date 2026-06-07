package com.example.smartexpensecalendar.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleLoginRequest(
    val id_token: String,
    val tenant_slug: String
)
