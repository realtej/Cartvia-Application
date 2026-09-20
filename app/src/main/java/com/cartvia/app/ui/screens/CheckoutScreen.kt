package com.cartvia.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cartvia.app.data.model.PaymentStatus
import com.cartvia.app.util.ApiResult
import com.cartvia.app.util.QrCodeUtils
import com.cartvia.app.util.SecureScreen

@Composable
fun CheckoutScreen(
    orderId: String,
    viewModel: CheckoutViewModel,
    onPaymentSuccess: (String) -> Unit
) {
    SecureScreen()
    val context = LocalContext.current
    val paymentState by viewModel.paymentState.collectAsState()
    val paymentStatus by viewModel.paymentStatus.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.initiatePayment(orderId)
    }

    LaunchedEffect(paymentStatus) {
        if (paymentStatus == PaymentStatus.SUCCESS) {
            onPaymentSuccess(orderId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Checkout", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        when (val state = paymentState) {
            is ApiResult.Success -> {
                val data = state.data
                Text("Amount: ${data.amount}", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                data.qrPayload?.let { payload ->
                    QrCodeUtils.generateQrCode(payload)?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Payment QR",
                            modifier = Modifier.size(256.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))

                data.upiDeepLink?.let { link ->
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        context.startActivity(intent)
                    }) {
                        Text("Pay with UPI App")
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                Text("Waiting for payment confirmation...", style = MaterialTheme.typography.bodyMedium)
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is ApiResult.Loading -> CircularProgressIndicator()
            is ApiResult.Error -> Text("Error: ${state.message}")
            else -> {}
        }
        
        if (paymentStatus == PaymentStatus.FAILED) {
            Text("Payment Failed. Please try again.", color = MaterialTheme.colorScheme.error)
            Button(onClick = { viewModel.initiatePayment(orderId) }) {
                Text("Retry")
            }
        }
    }
}
