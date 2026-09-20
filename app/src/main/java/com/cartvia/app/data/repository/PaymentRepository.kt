package com.cartvia.app.data.repository

import com.cartvia.app.data.model.*
import com.cartvia.app.data.remote.PaymentApi
import com.cartvia.app.util.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentApi: PaymentApi
) : BaseRepository() {

    suspend fun initiatePayment(orderId: String): ApiResult<PaymentInitiateResponse> = safeApiCall {
        paymentApi.initiatePayment(PaymentInitiateRequest(orderId))
    }

    suspend fun getPaymentStatus(orderId: String): ApiResult<PaymentStatusResponse> = safeApiCall {
        paymentApi.getPaymentStatus(orderId)
    }

    suspend fun getOrder(id: String): ApiResult<OrderResponse> = safeApiCall {
        paymentApi.getOrder(id)
    }

    suspend fun getReceipt(orderId: String): ApiResult<ReceiptResponse> = safeApiCall {
        paymentApi.getReceipt(orderId)
    }
}
