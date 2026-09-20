package com.cartvia.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cartvia.app.data.model.ReceiptResponse
import com.cartvia.app.data.repository.PaymentRepository
import com.cartvia.app.util.ApiResult
import com.cartvia.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _receiptState = MutableStateFlow<ApiResult<ReceiptResponse>?>(null)
    val receiptState = _receiptState.asStateFlow()

    fun loadReceipt(orderId: String) {
        viewModelScope.launch {
            _receiptState.value = ApiResult.Loading
            _receiptState.value = paymentRepository.getReceipt(orderId)
        }
    }

    fun clearSession() {
        sessionManager.endSession()
    }
}
