package com.cartvia.app.data.remote

import com.cartvia.app.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiEnvelope<UserIdResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiEnvelope<LoginResponse>

    @POST("/api/auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): Response<ApiEnvelope<RefreshTokenResponse>>

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiEnvelope<ResetTokenSentResponse>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ApiEnvelope<SuccessResponse>
}
