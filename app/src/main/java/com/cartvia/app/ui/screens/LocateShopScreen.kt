package com.cartvia.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SuccessGreen

@Composable
fun LocateShopScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Render Rich High-Fidelity Interactive Styled Vector Navigation Map
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8ECEF))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Drawing background urban park grids
                drawRect(color = Color(0xFFD4E6D5), topLeft = Offset(20.dp.toPx(), 40.dp.toPx()), size = Size(w * 0.3f, h * 0.15f))
                drawRect(color = Color(0xFFD4E6D5), topLeft = Offset(w * 0.6f, h * 0.4f), size = Size(w * 0.35f, h * 0.2f))

                // Drawing Avenue Street Networks
                val streetColor = Color.White
                drawRect(color = streetColor, topLeft = Offset(0f, h * 0.25f), size = Size(w, 36.dp.toPx()))
                drawRect(color = streetColor, topLeft = Offset(0f, h * 0.6f), size = Size(w, 44.dp.toPx()))
                drawRect(color = streetColor, topLeft = Offset(w * 0.45f, 0f), size = Size(40.dp.toPx(), h))
                drawRect(color = streetColor, topLeft = Offset(w * 0.15f, 0f), size = Size(28.dp.toPx(), h))

                // Drawing Blue Dynamic Route Tracking Path Polyline
                drawLine(
                    color = Color(0xFF2563EB),
                    start = Offset(w * 0.2f, h * 0.63f),
                    end = Offset(w * 0.48f, h * 0.63f),
                    strokeWidth = 6.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF2563EB),
                    start = Offset(w * 0.48f, h * 0.63f),
                    end = Offset(w * 0.48f, h * 0.28f),
                    strokeWidth = 6.dp.toPx()
                )
            }

            // Map Markers - Store Location Hub
            Card(
                modifier = Modifier.offset(x = 130.dp, y = 140.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cartvia Mega Hub", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Map Markers - Delivery Partner Live Pin
            Box(
                modifier = Modifier
                    .offset(x = 180.dp, y = 430.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }

            // Map Markers - Current User Location Pin Target
            Box(
                modifier = Modifier
                    .offset(x = 50.dp, y = 430.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
        
        // Bottom Info Card
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Awaiting Assignment", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Delivery Partner", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Calculating...",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                    // Progress bar mock
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0f)
                                .fillMaxHeight()
                                .background(SuccessGreen)
                        )
                    }
                }
            }
        }
    }
}
