package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.PaymentInitiateResponse
import com.cartvia.app.data.model.PaymentStatus
import com.cartvia.app.data.repository.PaymentRepository
import com.cartvia.app.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _paymentState = MutableStateFlow<ApiResult<PaymentInitiateResponse>?>(null)
    val paymentState = _paymentState.asStateFlow()

    private val _paymentStatus = MutableStateFlow<PaymentStatus?>(null)
    val paymentStatus = _paymentStatus.asStateFlow()

    fun initiatePayment(orderId: String) {
        viewModelScope.launch {
            _paymentState.value = ApiResult.Loading
            val result = paymentRepository.initiatePayment(orderId)
            _paymentState.value = result
            
            if (result is ApiResult.Success) {
                pollPaymentStatus(orderId)
            }
        }
    }

    private fun pollPaymentStatus(orderId: String) {
        viewModelScope.launch {
            var retryCount = 0
            while (retryCount < 30) { // ~60s
                val result = paymentRepository.getPaymentStatus(orderId)
                if (result is ApiResult.Success) {
                    _paymentStatus.value = result.data.status
                    if (result.data.status == PaymentStatus.SUCCESS || result.data.status == PaymentStatus.FAILED) {
                        break
                    }
                }
                delay(2000)
                retryCount++
            }
        }
    }
}
