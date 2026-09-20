package com.cartvia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.components.CartviaItemCard
import com.cartvia.app.ui.theme.NeutralGray

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.cartvia.app.data.model.StoreOccupancyDto
import com.cartvia.app.util.ApiResult

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val offers by viewModel.offers.collectAsState()
    val storeRanking by viewModel.storeRanking.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
            item {
                Text(
                    text = "Nearby Store Density",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                when (val ranking = storeRanking) {
                    is ApiResult.Success -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(ranking.data) { store ->
                                StoreDensitySmallCard(store)
                            }
                        }
                    }
                    is ApiResult.Loading -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(2.dp))
                    }
                    else -> {
                        Text("Unable to load store density.", style = MaterialTheme.typography.bodySmall, color = NeutralGray)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Updates & Offers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Explore the best deals today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray
                )
            }
            items(offers) { offer ->
                CartviaItemCard(
                    name = offer.title,
                    price = "Special Offer",
                    subtitle = offer.description,
                    onAdd = { viewModel.addOfferToList(offer) }
                )
            }
        }
}

@Composable
fun StoreDensitySmallCard(store: StoreOccupancyDto) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.width(160.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = store.storeName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val color = when {
                    store.activeCarts < 5 -> Color(0xFF4CAF50)
                    store.activeCarts < 15 -> Color(0xFFFBC02D)
                    else -> Color(0xFFF44336)
                }
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "${store.activeCarts} active carts", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
