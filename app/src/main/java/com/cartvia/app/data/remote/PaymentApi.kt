package com.cartvia.app.data.remote

import com.cartvia.app.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {
    @POST("/api/payments/initiate")
    suspend fun initiatePayment(@Body request: PaymentInitiateRequest): ApiEnvelope<PaymentInitiateResponse>

    @GET("/api/payments/{orderId}/status")
    suspend fun getPaymentStatus(@Path("orderId") orderId: String): ApiEnvelope<PaymentStatusResponse>

    @GET("/api/orders/{id}")
    suspend fun getOrder(@Path("id") id: String): ApiEnvelope<OrderResponse>

    @GET("/api/receipts/{orderId}")
    suspend fun getReceipt(@Path("orderId") orderId: String): ApiEnvelope<ReceiptResponse>
}
