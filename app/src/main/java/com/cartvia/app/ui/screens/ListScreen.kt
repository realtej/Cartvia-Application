package com.cartvia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.components.CartviaItemCard
import com.cartvia.app.ui.components.SwipeDismissWrapper

@Composable
fun ListScreen() {
    var showConstructionDialog by remember { mutableStateOf(true) }

    val itemsList = remember {
        mutableStateListOf<CatalogItem>()
    }

    if (showConstructionDialog) {
        AlertDialog(
            onDismissRequest = { showConstructionDialog = false },
            icon = { Icon(Icons.Default.Construction, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)) },
            title = { Text(text = "Feature Under Development") },
            text = {
                Text(
                    text = "The Cartvia Master Catalog is currently undergoing backend infrastructure optimization and server synchronization. Real-time product inventory tracking will be active shortly.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showConstructionDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Slide right to reveal option to delete [Trash icon | Item]",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (itemsList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text("No items in catalog list", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            items(itemsList, key = { it.id }) { item ->
                SwipeDismissWrapper(
                    onDismiss = {
                        itemsList.remove(item)
                    }
                ) {
                    CartviaItemCard(
                        name = item.name,
                        price = item.price,
                        subtitle = item.subtitle,
                        onAdd = { /* Click Interaction */ }
                    )
                }
            }
        }
    }
}

data class CatalogItem(
    val id: String,
    val name: String,
    val price: String,
    val subtitle: String
)
