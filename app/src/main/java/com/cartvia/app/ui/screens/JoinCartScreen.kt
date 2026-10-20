package com.cartvia.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.cartvia.app.data.remote.ApiErrorCode
import com.cartvia.app.ui.components.BarcodeScannerView
import com.cartvia.app.ui.components.CartConnectionPermissionDialog
import com.cartvia.app.ui.components.StoreTrafficBadge
import com.cartvia.app.util.ApiResult
import kotlinx.coroutines.delay

@Composable
fun JoinCartScreen(
    viewModel: JoinCartViewModel,
    onSessionCreated: (String) -> Unit
) {
    val context = LocalContext.current
    val sessionState by viewModel.sessionState.collectAsState()
    val storeTraffic by viewModel.storeTraffic.collectAsState()
    
    var isManualMode by remember { mutableStateOf(false) }
    var trolleyCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var connectedTrolley by remember { mutableStateOf<String?>(null) }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var showPermissionRationale by remember { mutableStateOf(!hasCameraPermission) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (!granted) {
                isManualMode = true
                errorMessage = "Camera permission denied — enter the code printed on your cart"
            }
        }
    )

    if (showPermissionRationale) {
        CartConnectionPermissionDialog(
            onAllow = {
                showPermissionRationale = false
                launcher.launch(Manifest.permission.CAMERA)
            },
            onDismiss = {
                showPermissionRationale = false
                isManualMode = true
                errorMessage = "Scan mode unavailable without camera access"
            }
        )
    }

    LaunchedEffect(sessionState) {
        val state = sessionState
        if (state is ApiResult.Success) {
            errorMessage = null
            connectedTrolley = state.data.trolleyCode
            delay(1500) // Brief delay to show connection status
            onSessionCreated(state.data.sessionId)
        } else if (state is ApiResult.Error) {
            errorMessage = if (state.code == ApiErrorCode.TROLLEY_NOT_FOUND) {
                "Trolley not found. Please check the code and try again."
            } else {
                state.message ?: "An error occurred. Please try again."
            }
        }
    }

    // Auto-switch to manual mode if no scan for 10 seconds
    LaunchedEffect(isManualMode) {
        if (!isManualMode && hasCameraPermission) {
            delay(10000)
            if (!isManualMode) {
                isManualMode = true
                errorMessage = "Camera timed out — enter the code printed on your cart"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isManualMode && hasCameraPermission) {
            BarcodeScannerView(
                onBarcodeDetected = { code ->
                    if (code.isNotBlank()) {
                        viewModel.joinCart(code)
                    }
                },
                onError = {
                    isManualMode = true
                    errorMessage = "Camera unavailable — enter the code printed on your cart"
                }
            )
            
            // Overlays for Scan Mode
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Scan Trolley QR Code",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { isManualMode = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("Enter code manually")
                }
            }
        } else {
            // Manual Mode UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                storeTraffic?.let { traffic ->
                    StoreTrafficBadge(traffic)
                    Spacer(Modifier.height(16.dp))
                }

                Text("Connect to Trolley", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = trolleyCode,
                    onValueChange = { 
                        trolleyCode = it
                        errorMessage = null 
                    },
                    label = { Text("Trolley Code") },
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.joinCart(trolleyCode) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sessionState !is ApiResult.Loading
                ) {
                    if (sessionState is ApiResult.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Join Session")
                    }
                }
                
                if (hasCameraPermission) {
                    TextButton(onClick = { 
                        isManualMode = false
                        errorMessage = null
                    }) {
                        Text("Back to QR Scanner")
                    }
                }
            }
        }
        
        // Error Snackbar if TROLLEY_NOT_FOUND happens during scan
        if (!isManualMode && errorMessage != null) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                action = {
                    TextButton(onClick = { errorMessage = null }) { Text("OK") }
                }
            ) {
                Text(errorMessage!!)
            }
        }
    }
}
