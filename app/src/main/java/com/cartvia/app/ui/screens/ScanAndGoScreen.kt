package com.cartvia.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cartvia.app.ui.components.BarcodeScannerView
import com.cartvia.app.ui.theme.BackgroundGray
import com.cartvia.app.ui.theme.BorderGray
import com.cartvia.app.ui.theme.ErrorRed
import com.cartvia.app.ui.theme.LightRedBg
import com.cartvia.app.ui.theme.NeutralDark
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.util.ApiResult

enum class ScanScreenState {
    SCANNING,
    PAIRING_FAILED,
    CONNECTION_LOST,
    WEIGHT_DISCREPANCY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanAndGoScreen(
    viewModel: JoinCartViewModel,
    onSessionCreated: (String) -> Unit
) {
    var screenState by remember { mutableStateOf(ScanScreenState.SCANNING) }
    val sessionState by viewModel.sessionState.collectAsState()
    var showManualDialog by remember { mutableStateOf(false) }
    var showPaymentFailedDialog by remember { mutableStateOf(false) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var trolleyCodeText by remember { mutableStateOf("TROLLEY-01") }

    LaunchedEffect(sessionState) {
        if (sessionState is ApiResult.Success) {
            onSessionCreated((sessionState as ApiResult.Success).data.sessionId)
        } else if (sessionState is ApiResult.Error) {
            screenState = ScanScreenState.PAIRING_FAILED
        }
    }

    if (showManualDialog) {
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("Enter Code Manually") },
            text = {
                OutlinedTextField(
                    value = trolleyCodeText,
                    onValueChange = { trolleyCodeText = it },
                    label = { Text("Trolley Code / Cart ID") },
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

    // Payment Failed Dialog matching user's exact reference image
    if (showPaymentFailedDialog) {
        Dialog(onDismissRequest = { showPaymentFailedDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFE2E8F0),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Payment didn't go through",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Nothing was charged. Try scanning the QR again.",
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Green Try again button
                    Button(
                        onClick = {
                            showPaymentFailedDialog = false
                            screenState = ScanScreenState.SCANNING
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
                    ) {
                        Text(
                            text = "Try again",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grey Cancel button
                    Button(
                        onClick = { showPaymentFailedDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF94A3B8))
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Live physical camera hardware & flashlight hardware preview
            if (screenState == ScanScreenState.SCANNING) {
                BarcodeScannerView(
                    onBarcodeDetected = { barcode ->
                        viewModel.joinCart(barcode)
                    },
                    isFlashlightOn = isFlashlightOn
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (screenState) {
                    ScanScreenState.SCANNING -> {
                        ScanningView(
                            isFlashlightOn = isFlashlightOn,
                            onFlashlight = { isFlashlightOn = !isFlashlightOn },
                            onEnterManual = { showManualDialog = true },
                            onSimulateScan = { viewModel.joinCart("TROLLEY-01") }
                        )
                    }
                    ScanScreenState.PAIRING_FAILED -> {
                        PairingFailedView(
                            onRetryScan = { screenState = ScanScreenState.SCANNING },
                            onEnterManual = { showManualDialog = true }
                        )
                    }
                    ScanScreenState.CONNECTION_LOST -> {
                        ConnectionLostView(
                            onReconnect = { screenState = ScanScreenState.SCANNING }
                        )
                    }
                    ScanScreenState.WEIGHT_DISCREPANCY -> {
                        WeightDiscrepancyView(
                            onRescan = { screenState = ScanScreenState.SCANNING }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScanningView(
    isFlashlightOn: Boolean,
    onFlashlight: () -> Unit,
    onEnterManual: () -> Unit,
    onSimulateScan: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Scanner Frame Box with Blue Corner Guides
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clickable(onClick = onSimulateScan),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 3.dp.toPx()
                    val cornerLength = 32.dp.toPx()
                    val color = PrimaryBlue

                    // Top-Left corner
                    drawLine(color, Offset(0f, 0f), Offset(cornerLength, 0f), strokeWidth)
                    drawLine(color, Offset(0f, 0f), Offset(0f, cornerLength), strokeWidth)

                    // Top-Right corner
                    drawLine(color, Offset(size.width, 0f), Offset(size.width - cornerLength, 0f), strokeWidth)
                    drawLine(color, Offset(size.width, 0f), Offset(size.width, cornerLength), strokeWidth)

                    // Bottom-Left corner
                    drawLine(color, Offset(0f, size.height), Offset(cornerLength, size.height), strokeWidth)
                    drawLine(color, Offset(0f, size.height), Offset(0f, size.height - cornerLength), strokeWidth)

                    // Bottom-Right corner
                    drawLine(color, Offset(size.width, size.height), Offset(size.width - cornerLength, size.height), strokeWidth)
                    drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - cornerLength), strokeWidth)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Flashlight toggle button with text label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onFlashlight)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isFlashlightOn) PrimaryBlue else Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = "Flashlight",
                        tint = if (isFlashlightOn) Color.White else PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Flashlight", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onEnterManual,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Enter Code Manually", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun PairingFailedView(
    onRetryScan: () -> Unit,
    onEnterManual: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(ErrorRed.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Pairing Failed",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ErrorRed
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Smart-Cart pairing unsuccessful. Please review the steps below.",
            fontSize = 14.sp,
            color = NeutralGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("How to resolve:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeutralDark)

                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    Text("1. ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text("Ensure you are scanning the QR code located directly on the cart handle.", fontSize = 14.sp, color = NeutralGray)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    Text("2. ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text("Check that your device’s Bluetooth and Location services are enabled.", fontSize = 14.sp, color = NeutralGray)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    Text("3. ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text("Clean your phone's camera lens or enter the manual code instead.", fontSize = 14.sp, color = NeutralGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetryScan,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Scan QR Code Again", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onEnterManual,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
        ) {
            Text("Enter Code Manually", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ConnectionLostView(
    onReconnect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(ErrorRed.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Connection Lost",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NeutralDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Unable to connect to Smart-Cart #5351. Please verify that you are near the cart or try again.",
            fontSize = 14.sp,
            color = NeutralGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ErrorCode", fontSize = 14.sp, color = NeutralGray)
                    Text("ERR_BLE_TIMEOUT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Cart ID", fontSize = 14.sp, color = NeutralGray)
                    Text("Cart#5351", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onReconnect,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Tap to Reconnect", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun WeightDiscrepancyView(
    onRescan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Red Warning Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = LightRedBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Weight Discrepancy Detected", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "The scale on Cart #1248 measured a different weight than expected for the items in your basket.",
                        fontSize = 13.sp,
                        color = ErrorRed.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Summary Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Scale Comparison Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeutralDark)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Digital Cart Expected Weight", fontSize = 14.sp, color = NeutralGray)
                    Text("1.55 kg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Physical Basket Measured Weight", fontSize = 14.sp, color = NeutralGray)
                    Text("2.10 kg (+0.55 kg)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = BorderGray)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Please make sure you have scanned all items currently inside the physical basket before continuing.",
                    fontSize = 13.sp,
                    color = NeutralGray,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRescan,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scan Missing Item (Rescan)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
        ) {
            Text("Review Scanned Items", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
        ) {
            Text("Request On-Floor Staff Help", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
