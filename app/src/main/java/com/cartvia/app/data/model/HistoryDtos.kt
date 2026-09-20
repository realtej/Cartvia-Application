package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionHistoryDto(
    val sessionId: String,
    val sessionCode: String,
    val storeId: String,
    val storeName: String,
    val status: SessionStatus,
    val orderId: String? = null,
    val orderCode: String? = null,
    val grandTotal: String? = null,
    val startedAt: String,
    val endedAt: String? = null
)
