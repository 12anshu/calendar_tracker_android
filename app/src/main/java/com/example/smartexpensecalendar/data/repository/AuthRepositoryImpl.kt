package com.example.smartexpensecalendar.data.repository

import com.example.smartexpensecalendar.data.remote.AuthApiService
import com.example.smartexpensecalendar.data.remote.dto.GoogleLoginRequest
import com.example.smartexpensecalendar.data.remote.dto.LoginRequest
import com.example.smartexpensecalendar.data.remote.dto.LoginResponse
import com.example.smartexpensecalendar.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService
) : AuthRepository {
    override suspend fun login(email: String, password: String, tenantSlug: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(tenantSlug, email, password))
            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun googleLogin(id_token: String, tenantSlug: String): Result<LoginResponse> {
        return try {
            val response = api.googleLogin(GoogleLoginRequest(id_token, tenantSlug))
            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
