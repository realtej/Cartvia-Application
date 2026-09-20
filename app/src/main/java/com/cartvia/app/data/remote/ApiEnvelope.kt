package com.cartvia.app.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiEnvelope<T>(
    val success: Boolean,
    val data: T? = null,
    val errorCode: String? = null,
    val message: String? = null,
    val fieldErrors: List<FieldError>? = null
)

@Serializable
data class FieldError(
    val field: String,
    val constraint: String,
    val rejectedValue: String? = null,
    val message: String
)

enum class ApiErrorCode {
    VALIDATION_ERROR, USERNAME_TAKEN, EMAIL_TAKEN, PHONE_TAKEN, 
    INVALID_CREDENTIALS, INVALID_TOKEN, TOKEN_EXPIRED, 
    PRODUCT_NOT_FOUND, USER_NOT_FOUND, TROLLEY_NOT_FOUND, 
    SESSION_NOT_FOUND, NOT_FOUND, FORBIDDEN, INTERNAL_ERROR,
    UNKNOWN
}
