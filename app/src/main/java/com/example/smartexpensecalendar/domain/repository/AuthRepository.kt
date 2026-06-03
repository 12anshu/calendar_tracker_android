package com.example.smartexpensecalendar.domain.repository

import com.example.smartexpensecalendar.data.remote.dto.LoginResponse

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
        tenantSlug: String
    ): Result<LoginResponse>

    suspend fun googleLogin(
        id_token: String,
        tenantSlug: String
    ): Result<LoginResponse>
}