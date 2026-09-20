package com.cartvia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cartvia.app.data.model.CartItemDto
import com.cartvia.app.data.model.VerificationStatus
import androidx.compose.material.icons.filled.Person
import com.cartvia.app.util.ApiResult
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    sessionId: String,
    viewModel: ShopViewModel,
    onCheckout: (String) -> Unit
) {
    val cartState by viewModel.cartState.collectAsState()
    val shoppingStatus by viewModel.shoppingStatus.collectAsState()
    val orderId by viewModel.orderId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showBudgetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        viewModel.initSession(sessionId)
    }

    LaunchedEffect(sessionId) {
        viewModel.uiEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(orderId) {
        orderId?.let { onCheckout(it) }
    }

    if (showBudgetDialog) {
        BudgetDialog(
            currentLimit = shoppingStatus.budgetLimit,
            onDismiss = { showBudgetDialog = false },
            onConfirm = { 
                viewModel.setBudgetLimit(it)
                showBudgetDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { 
            TopAppBar(
                title = { Text("Shopping Session") }
            ) 
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ShoppingStatusCard(
                status = shoppingStatus,
                onSetBudget = { showBudgetDialog = true }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (val state = cartState) {
                    is ApiResult.Success -> {
                        CartList(
                            items = state.data.items,
                            total = state.data.totalAmount,
                            onRemoveItem = { viewModel.removeCartItemLocal(it) },
                            onQtyChange = { id, delta -> viewModel.updateCartItemQuantityLocal(id, delta) }
                        )
                    }
                    is ApiResult.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is ApiResult.Error -> {
                        Card(
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(36.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Connection Lost",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Attempting to re-establish synchronization automatically...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }

            Button(
                onClick = { viewModel.endSession(sessionId) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = shoppingStatus.canCheckout
            ) {
                Text("Proceed to Checkout (₹${shoppingStatus.totalAmount})")
            }
        }
    }
}

@Composable
fun ShoppingStatusCard(
    status: ShoppingStatus,
    onSetBudget: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatusRow(text = "${status.productCount} products scanned", isSuccess = status.productCount > 0)
            StatusRow(text = "Cart synchronized", isSuccess = status.isSynchronized)
            
            val verificationText = if (status.unverifiedCount > 0) {
                "Verification needed on ${status.unverifiedCount} item(s)"
            } else {
                "Items verified"
            }
            StatusRow(text = verificationText, isSuccess = status.unverifiedCount == 0 && status.productCount > 0)
            
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Current total", style = MaterialTheme.typography.titleMedium)
                Text("₹${status.totalAmount}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            // Budget Tracker Section
            Spacer(Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (status.budgetLimit != null) "Budget: ₹${"%.2f".format(status.budgetLimit)}" else "No Budget Set",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (status.budgetLimit != null) {
                        val remaining = status.budgetLimit - (status.totalAmount.toDoubleOrNull() ?: 0.0)
                        Text(
                            text = if (remaining >= 0) "₹${"%.2f".format(remaining)} remaining" else "Over budget by ₹${"%.2f".format(-remaining)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (remaining >= 0) MaterialTheme.colorScheme.onSurfaceVariant else Color.Red
                        )
                    }
                }
                IconButton(onClick = onSetBudget, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Set Budget", modifier = Modifier.size(16.dp))
                }
            }

            if (status.budgetLimit != null) {
                Spacer(Modifier.height(8.dp))
                val progressColor = when {
                    status.budgetProgress > 1.0f -> Color.Red
                    status.budgetProgress > 0.8f -> Color(0xFFFBC02D) // Amber
                    else -> MaterialTheme.colorScheme.primary
                }
                LinearProgressIndicator(
                    progress = { status.budgetProgress.coerceAtMost(1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = progressColor,
                    trackColor = progressColor.copy(alpha = 0.2f)
                )
            }
            
            if (status.canCheckout) {
                Text(
                    "● Cart is ready for checkout", 
                    color = Color(0xFF4CAF50), 
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BudgetDialog(
    currentLimit: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double?) -> Unit
) {
    var budgetText by remember { mutableStateOf(currentLimit?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Shopping Budget") },
        text = {
            Column {
                Text("Keep track of your spending as you scan items.", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    label = { Text("Budget Limit (₹)") },
                    placeholder = { Text("e.g. 2000") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val limit = budgetText.toDoubleOrNull()
                    onConfirm(limit)
                }
            ) {
                Text("Set Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = { onConfirm(null) }) {
                Text("Clear")
            }
        }
    )
}

@Composable
fun StatusRow(text: String, isSuccess: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            if (isSuccess) "✓ " else "● ", 
            color = if (isSuccess) Color(0xFF4CAF50) else Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun CartList(
    items: List<CartItemDto>,
    total: String,
    onRemoveItem: (String) -> Unit,
    onQtyChange: (String, Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp, 8.dp)
    ) {
        item {
            Text("Swipe right to remove items from session cart [Trash icon | Item]", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
        items(items, key = { it.productId }) { item ->
            com.cartvia.app.ui.components.SwipeDismissWrapper(
                onDismiss = { onRemoveItem(item.productId) }
            ) {
                CartItemRow(
                    item = item,
                    onIncrement = { onQtyChange(item.productId, 1) },
                    onDecrement = { onQtyChange(item.productId, -1) }
                )
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Total: ₹$total", 
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemDto,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyLarge)
            if (item.aisleNumber != null) {
                Text(
                    text = "Aisle ${item.aisleNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text("₹${item.unitPrice} x ${item.quantity}", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onDecrement, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text("-", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                Text(text = "${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                TextButton(onClick = onIncrement, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text("+", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        val lineTotal = (item.unitPrice.toDoubleOrNull() ?: 0.0) * item.quantity
        Text("₹%.2f".format(lineTotal), fontWeight = FontWeight.SemiBold)
    }
}
