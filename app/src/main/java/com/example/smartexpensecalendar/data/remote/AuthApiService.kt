package com.example.smartexpensecalendar.data.remote

import com.example.smartexpensecalendar.data.remote.dto.GoogleLoginRequest
import com.example.smartexpensecalendar.data.remote.dto.LoginRequest
import com.example.smartexpensecalendar.data.remote.dto.LoginResponse
import com.example.smartexpensecalendar.data.remote.dto.RegisterResponse
import com.example.smartexpensecalendar.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/v1/auth/google/login")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): LoginResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}
