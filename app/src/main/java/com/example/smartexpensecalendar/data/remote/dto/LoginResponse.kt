package com.example.smartexpensecalendar.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val data: LoginData?,
    val error: String?,
    val timestamp: String
)

data class LoginData(
    val user: UserDto,
    val tokens: TokenDto
)

data class UserDto(
    val id: String,
    val email: String?,
    @SerializedName("auth_type") val authType: String,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("picture_url") val pictureUrl: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("last_login") val lastLogin: String?,
    @SerializedName("created_at") val createdAt: String
)

data class TokenDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)
