package com.cartvia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cartvia.app.data.model.ReceiptItemDto
import com.cartvia.app.util.ApiResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    orderId: String,
    viewModel: ReceiptViewModel,
    onDone: () -> Unit
) {
    val receiptState by viewModel.receiptState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadReceipt(orderId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Receipt") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = receiptState) {
                is ApiResult.Success -> {
                    val receipt = state.data
                    Text("Store: ${receipt.storeName}", modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 4.dp), style = MaterialTheme.typography.titleMedium)
                    Text("Order ID: ${receipt.orderCode}", modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp), style = MaterialTheme.typography.bodyMedium)
                    Text("Issued At: ${receipt.issuedAt.replace("T", " ")}", modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(receipt.items) { item ->
                            ReceiptItemRow(item)
                        }
                    }
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Subtotal: ${receipt.subtotal}")
                        Text("Tax: ${receipt.taxTotal}")
                        Text("Grand Total: ${receipt.grandTotal}", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.clearSession()
                                onDone()
                            }, 
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Done")
                        }
                    }
                }
                is ApiResult.Loading -> CircularProgressIndicator()
                else -> {}
            }
        }
    }
}

@Composable
fun ReceiptItemRow(item: ReceiptItemDto) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)) {
        Text(item.name, modifier = Modifier.weight(1f))
        Text("${item.quantity} x ${item.unitPrice} = ${item.lineTotal}")
    }
}
