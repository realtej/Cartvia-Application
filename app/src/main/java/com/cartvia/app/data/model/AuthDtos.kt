package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val password: String,
    val gender: String? = null
)

@Serializable
data class UserIdResponse(
    val userId: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserSummaryDto
)

@Serializable
data class UserSummaryDto(
    val id: String,
    val username: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: Role
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponse(
    val accessToken: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String? = null,
    val phone: String? = null
)

@Serializable
data class ResetTokenSentResponse(
    val resetTokenSent: Boolean
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

@Serializable
data class SuccessResponse(
    val success: Boolean
)

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String
)
