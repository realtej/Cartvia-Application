package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.components.CartviaAppTopBar
import com.cartvia.app.ui.components.CartviaButton
import com.cartvia.app.ui.theme.PrimaryBlue

import com.cartvia.app.util.ApiResult

@Composable
fun ScanAndGoScreen(
    viewModel: JoinCartViewModel,
    onSessionCreated: (String) -> Unit
) {
    val sessionState by viewModel.sessionState.collectAsState()
    var showManualDialog by remember { mutableStateOf(false) }
    var trolleyCodeText by remember { mutableStateOf("TROLLEY-01") }

    LaunchedEffect(sessionState) {
        if (sessionState is ApiResult.Success) {
            onSessionCreated((sessionState as ApiResult.Success).data.sessionId)
        }
    }

    if (showManualDialog) {
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("Connect to Trolley") },
            text = {
                OutlinedTextField(
                    value = trolleyCodeText,
                    onValueChange = { trolleyCodeText = it },
                    label = { Text("Trolley Code") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.joinCart(trolleyCodeText)
                        showManualDialog = false
                    }
                ) {
                    Text("Join")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Scan QR code to Start Session",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Hold scanner over any authorized merchant code",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Mock Scanner Frame
        Box(
            modifier = Modifier
                .size(280.dp)
                .border(2.dp, PrimaryBlue, RoundedCornerShape(24.dp))
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = PrimaryBlue.copy(alpha = 0.1f)
            )
            // Corner guides could be added here
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.FlashlightOn, contentDescription = "Flashlight", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Flashlight", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { viewModel.joinCart("TROLLEY-01") },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = "My QR", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Simulate Scan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        CartviaButton(
            text = "Enter Code Manually",
            onClick = { showManualDialog = true },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = PrimaryBlue
        )
    }
}
