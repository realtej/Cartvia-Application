package com.cartvia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.components.CartviaItemCard
import com.cartvia.app.ui.components.CartviaStickyBottom

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun ShoppingBagScreen(
    sessionManager: com.cartvia.app.util.SessionManager,
    onCheckoutOrderId: (String) -> Unit,
    onCheckout: () -> Unit
) {
    val activeSessionId by sessionManager.activeSessionId.collectAsState()

    if (activeSessionId != null) {
        ShopScreen(
            sessionId = activeSessionId!!,
            viewModel = androidx.hilt.navigation.compose.hiltViewModel(),
            onCheckout = onCheckoutOrderId
        )
    } else {
        val items = remember {
            mutableStateListOf<BagItem>()
        }
        
        var budgetLimit by remember { mutableStateOf<Double?>(null) }
        val totalAmount = 0.0 // Hardcoded for this mock screen
        val budgetProgress = if (budgetLimit != null && budgetLimit!! > 0) (totalAmount / budgetLimit!!).toFloat() else 0f
        
        var showBudgetDialog by remember { mutableStateOf(false) }

        if (showBudgetDialog) {
            var tempBudgetText by remember { mutableStateOf(budgetLimit?.toString() ?: "") }
            AlertDialog(
                onDismissRequest = { showBudgetDialog = false },
                title = { Text("Set Shopping Budget") },
                text = {
                    OutlinedTextField(
                        value = tempBudgetText,
                        onValueChange = { tempBudgetText = it },
                        label = { Text("Budget Limit (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = { 
                        budgetLimit = tempBudgetText.toDoubleOrNull()
                        showBudgetDialog = false
                    }) { Text("Set") }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        budgetLimit = null
                        showBudgetDialog = false
                    }) { Text("Clear") }
                }
            )
        }

        Scaffold(
            bottomBar = {
                CartviaStickyBottom(
                    total = "₹0.00",
                    buttonText = "CHECK OUT",
                    onClick = onCheckout
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No active shopping session. Scan a trolley to start!", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

data class BagItem(
    val id: String,
    val name: String,
    val price: String,
    val subtitle: String,
    val quantity: Int
)
