package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SecondaryBlue
import com.cartvia.app.ui.theme.SuccessGreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*

@Composable
fun OrderHistoryScreen(
    onNavigateToReceipt: (String) -> Unit,
    onBack: () -> Unit
) {
    val orders = emptyList<Order>()

    Scaffold(
        topBar = {
            CartviaAppTopBar(
                title = "Order History",
                onBack = onBack
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
            items(orders) { order ->
                OrderCard(order, onNavigateToReceipt)
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onNavigateToReceipt: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (order.isCurrent) Modifier.border(1.dp, PrimaryBlue, RoundedCornerShape(16.dp)) else Modifier)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Cart #${order.id} ${if (order.isCurrent) "(Current)" else ""}", fontWeight = FontWeight.Bold)
                    Text(text = order.date, style = MaterialTheme.typography.bodySmall, color = NeutralGray)
                }
                Badge(
                    containerColor = if (order.status == "SUCCESS") SuccessGreen.copy(alpha = 0.1f) else SecondaryBlue,
                    contentColor = if (order.status == "SUCCESS") SuccessGreen else PrimaryBlue
                ) {
                    Text(text = order.status, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    order.itemizedList.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${item.quantity}x ${item.name}", style = MaterialTheme.typography.bodySmall)
                            Text(text = item.price, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (!expanded) {
                        Text(text = order.itemsSummary, style = MaterialTheme.typography.bodySmall, color = NeutralGray)
                    }
                    Text(text = order.total, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onNavigateToReceipt(order.id) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F4F6))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "View Receipt")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = NeutralGray
                    )
                }
            }
        }
    }
}

data class Order(
    val id: String,
    val date: String,
    val total: String,
    val itemsSummary: String,
    val status: String,
    val isCurrent: Boolean,
    val itemizedList: List<OrderItem> = emptyList()
)

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: String
)
