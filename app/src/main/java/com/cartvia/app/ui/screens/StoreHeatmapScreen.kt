package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cartvia.app.data.model.HeatmapLevel
import com.cartvia.app.data.model.ZoneHeatmapDto
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.util.ApiResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreHeatmapScreen(
    storeId: String,
    viewModel: StoreHeatmapViewModel,
    onBack: () -> Unit
) {
    val heatmapState by viewModel.heatmapState.collectAsState()

    LaunchedEffect(storeId) {
        viewModel.startPollingHeatmap(storeId)
    }

    DisposableEffect(storeId) {
        onDispose {
            viewModel.stopPollingHeatmap()
        }
    }

    Scaffold(
        topBar = {
            CartviaAppTopBar(
                title = "Store Live Heatmap",
                onBack = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = heatmapState) {
                is ApiResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ApiResult.Success -> {
                    val zones = state.data
                    if (zones.isEmpty()) {
                        Text(
                            text = "No live heatmap zones tracking data available currently.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Center).padding(24.dp)
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text(
                                text = "Live Zone Crowd Density Indicators",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(zones) { zone ->
                                    ZoneHeatmapCard(zone = zone)
                                }
                            }
                        }
                    }
                }
                is ApiResult.Error -> {
                    Text(
                        text = state.message ?: "Failed to retrieve live store density map updates.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ZoneHeatmapCard(zone: ZoneHeatmapDto) {
    val indicatorColor = when (zone.level) {
        HeatmapLevel.LOW -> Color(0xFF4CAF50)    // Green
        HeatmapLevel.MEDIUM -> Color(0xFFFFEB3B) // Yellow
        HeatmapLevel.HIGH -> Color(0xFFF44336)   // Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
                Text(
                    text = zone.level.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = zone.zoneName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Active Carts: ${zone.activeCarts} / ${zone.capacityThreshold}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
