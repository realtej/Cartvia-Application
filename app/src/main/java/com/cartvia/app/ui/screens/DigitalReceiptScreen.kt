package com.cartvia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.components.CartviaButton
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SuccessGreen

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.cartvia.app.util.ApiResult

@Composable
fun DigitalReceiptScreen(
    orderId: String,
    viewModel: ReceiptViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(orderId) {
        viewModel.loadReceipt(orderId)
    }

    val receiptState by viewModel.receiptState.collectAsState()

    Scaffold(
        topBar = {
            CartviaAppTopBar(
                title = "Digital Receipt",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (val state = receiptState) {
                is ApiResult.Success -> {
                    val data = state.data
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(text = "Invoice #${data.orderCode}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Date: ${data.issuedAt} • Store: ${data.storeName}", style = MaterialTheme.typography.bodySmall, color = NeutralGray)
                            Text(text = "Status: ${data.paymentStatus}", style = MaterialTheme.typography.bodySmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            data.items.forEach { item ->
                                ReceiptItem(item.name, "Qty: ${item.quantity} • Price: ₹${item.unitPrice}", "₹${item.lineTotal}")
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = Color(0xFFF3F4F6))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            ReceiptRow("Subtotal", "₹${data.subtotal}")
                            ReceiptRow("GST / Tax", "₹${data.taxTotal}")
                            ReceiptRow("Discount", "-₹${data.discountTotal}", valueColor = SuccessGreen)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Grand Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(text = "₹${data.grandTotal}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                        }
                    }
                }
                is ApiResult.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Failed to load receipt details.", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CartviaButton(
                text = "Download PDF Receipt",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun ReceiptItem(name: String, qty: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = name, fontWeight = FontWeight.Bold)
            Text(text = qty, style = MaterialTheme.typography.bodySmall, color = NeutralGray)
        }
        Text(text = price, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = NeutralGray, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}
