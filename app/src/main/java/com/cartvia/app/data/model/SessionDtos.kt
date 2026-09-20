package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(
    val trolleyCode: String,
    val userId: String? = null
)

@Serializable
data class SessionResponse(
    val sessionId: String,
    val sessionCode: String,
    val userId: String,
    val trolleyCode: String,
    val storeId: String,
    val status: SessionStatus,
    val cartId: String,
    val cartTotal: String,
    val startedAt: String,
    val endedAt: String? = null
)

@Serializable
data class SessionEndResponse(
    val session: SessionResponse,
    val orderId: String,
    val orderCode: String,
    val grandTotal: String
)
