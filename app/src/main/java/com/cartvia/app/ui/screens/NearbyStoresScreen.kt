package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyStoresScreen(
    viewModel: NearbyStoresViewModel,
    onStoreClick: (String) -> Unit
) {
    var selectedMapMode by remember { mutableIntStateOf(0) } // 0: Map, 1: Satellite

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Map",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    // Segmented toggle [ Map | Satellite ]
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(
                                    if (selectedMapMode == 0) MaterialTheme.colorScheme.surface else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedMapMode = 0 }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Map", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(
                                    if (selectedMapMode == 1) MaterialTheme.colorScheme.surface else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedMapMode = 1 }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Satellite", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val defaultLocation = LatLng(40.7128, -74.0060) // New York
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(defaultLocation, 13f)
            }

            val mapProperties = MapProperties(
                mapType = if (selectedMapMode == 1) MapType.SATELLITE else MapType.NORMAL
            )
            val mapUiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )

            val markerState1 = rememberMarkerState(position = defaultLocation)
            val markerState2 = rememberMarkerState(position = LatLng(40.7589, -73.9851))

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                Marker(
                    state = markerState1,
                    title = "SuperMart Downtown HQ",
                    snippet = "91 Orchard St, New York"
                )
                Marker(
                    state = markerState2,
                    title = "SuperMart Queens Branch",
                    snippet = "142 Broadway, Queens"
                )
            }
        }
    }
}
