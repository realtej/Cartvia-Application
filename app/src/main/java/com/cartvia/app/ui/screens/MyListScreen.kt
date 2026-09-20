package com.cartvia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.components.CartviaItemCard

@Composable
fun MyListScreen() {
    val items = listOf(
        ListItem("1", "Nomad Chronograph", "₹165.00", "In Stock"),
        ListItem("2", "Tech Utility Pack", "₹85.00", "Only 3 Left")
    )

    Scaffold(
        topBar = {
            CartviaAppTopBar(
                title = "My List",
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
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
            items(items) { item ->
                CartviaItemCard(
                    name = item.name,
                    price = item.price,
                    subtitle = item.status,
                    onAdd = { /* TODO */ }
                )
            }
        }
    }
}

data class ListItem(
    val id: String,
    val name: String,
    val price: String,
    val status: String
)
