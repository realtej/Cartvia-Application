package com.cartvia.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentInitiateRequest(
    val orderId: String
)

@Serializable
data class PaymentInitiateResponse(
    val paymentId: String,
    val orderId: String,
    val amount: String,
    val status: PaymentStatus,
    val upiDeepLink: String? = null,
    val qrPayload: String? = null
)

@Serializable
data class PaymentStatusResponse(
    val orderId: String,
    val paymentId: String,
    val status: PaymentStatus,
    val amount: String
)

@Serializable
data class OrderResponse(
    val orderId: String,
    val orderCode: String,
    val status: OrderStatus,
    val subtotal: String,
    val discountTotal: String,
    val taxTotal: String,
    val grandTotal: String,
    val items: List<OrderItemDto>,
    val createdAt: String
)

@Serializable
data class OrderItemDto(
    val productId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: String,
    val lineTotal: String
)

@Serializable
data class ReceiptResponse(
    val orderId: String,
    val orderCode: String,
    val storeName: String,
    val paymentStatus: OrderStatus,
    val items: List<ReceiptItemDto>,
    val subtotal: String,
    val discountTotal: String,
    val taxTotal: String,
    val grandTotal: String,
    val issuedAt: String
)

@Serializable
data class ReceiptItemDto(
    val productId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: String,
    val lineTotal: String
)
