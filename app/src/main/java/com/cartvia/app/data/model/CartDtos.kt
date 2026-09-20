package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddItemRequest(
    val barcode: String,
    val source: CartSource? = null
)

@Serializable
data class AddItemResponse(
    val cartId: String,
    val item: CartItemDto,
    val cartTotal: String,
    val recommendations: List<RecommendationDto> = emptyList()
)

@Serializable
data class CartItemDto(
    val productId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: String,
    val aisleNumber: String? = null,
    val sectionName: String? = null,
    val verificationStatus: VerificationStatus? = null
)

@Serializable
data class RecommendationDto(
    val productId: String,
    val name: String,
    val reason: String
)

@Serializable
data class BatchAddItemRequest(
    val items: List<BatchItem>
)

@Serializable
data class BatchItem(
    val barcode: String,
    val source: CartSource? = null,
    val clientTs: String
)

@Serializable
data class CartResponse(
    val cartId: String,
    val sessionId: String,
    val totalAmount: String,
    val items: List<CartItemDto>
)

@Serializable
data class UpdateQuantityRequest(
    val quantity: Int
)

@Serializable
data class VerifyWeightRequest(
    val measuredWeightG: String
)

@Serializable
data class VerifyWeightResponse(
    val productId: String,
    val verificationStatus: VerificationStatus,
    val measuredWeightG: String,
    val expectedWeightG: String? = null
)
