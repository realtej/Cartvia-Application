package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.components.CartviaButton
import com.cartvia.app.ui.theme.ErrorRed
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SuccessGreen

import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.*

import com.cartvia.app.util.ApiResult

@Composable
fun PaymentSuccessScreen(
    orderId: String,
    viewModel: ReceiptViewModel,
    onContinue: () -> Unit,
    onViewReceipt: () -> Unit
) {
    var tripRating by remember { mutableIntStateOf(0) }
    val receiptState by viewModel.receiptState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadReceipt(orderId)
        viewModel.clearSession()
    }

    Scaffold(
        topBar = {
            CartviaAppTopBar(
                title = "Success",
                onBack = onContinue
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(SuccessGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Payment Successful!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Thank you! Your transaction has been completed successfully.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            when (val state = receiptState) {
                is ApiResult.Success -> {
                    val data = state.data
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ResultRow("Cart Session", data.orderId)
                            ResultRow("Invoice Number", data.orderCode)
                            ResultRow("Amount Paid", "₹${data.grandTotal}", valueColor = PrimaryBlue)
                        }
                    }
                }
                is ApiResult.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ResultRow("Cart Session", orderId)
                            ResultRow("Invoice Number", "INV-$orderId")
                            ResultRow("Amount Paid", "₹0.00", valueColor = PrimaryBlue)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Rate Your Trip Component
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "How was your shopping experience?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    (1..5).forEach { index ->
                        IconButton(onClick = { tripRating = index }) {
                            Icon(
                                imageVector = if (index <= tripRating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$index Stars",
                                tint = if (index <= tripRating) Color(0xFFFBC02D) else NeutralGray
                            )
                        }
                    }
                }
                if (tripRating > 0) {
                    Text(
                        text = "Thank you for your feedback!",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(onClick = onViewReceipt) {
                Text(
                    text = "View Detailed Digital Receipt",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            CartviaButton(
                text = "Continue Shopping",
                onClick = onContinue
            )
        }
    }
}

@Composable
fun PaymentFailedScreen(
    orderId: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            CartviaAppTopBar(
                title = "Payment Failed",
                onBack = onCancel
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(ErrorRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer(rotationZ = 45f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Transaction Declined",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "The transaction could not be processed. Please try again or use a different payment method.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    ResultRow("Pending Order", orderId)
                    ResultRow("Total Amount", "₹ --")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "* Please contact support if the issue persists.",
                        color = ErrorRed,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            CartviaButton(
                text = "Retry Payment",
                onClick = onRetry
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onCancel) {
                Text(text = "Cancel & Return to Cart", color = ErrorRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = NeutralGray)
        Text(text = value, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
