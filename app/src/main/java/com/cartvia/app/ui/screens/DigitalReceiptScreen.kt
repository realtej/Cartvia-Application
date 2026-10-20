package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.cartvia.app.ui.theme.BackgroundGray
import com.cartvia.app.ui.theme.BorderGray
import com.cartvia.app.ui.theme.NeutralDark
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalReceiptScreen(
    orderId: String,
    viewModel: ReceiptViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(orderId) {
        viewModel.loadReceipt(orderId)
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Digital Receipt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeutralDark, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundGray)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Invoice #INV-2026-004",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Date: Oct 24, 2026 • 11:32 AM",
                        fontSize = 13.sp,
                        color = NeutralGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = BorderGray)

                    Spacer(modifier = Modifier.height(16.dp))

                    ReceiptItemRow("AeroSound Max", "Qty: 1 • Space Grey", "₹18,499.00")

                    Spacer(modifier = Modifier.height(14.dp))

                    ReceiptItemRow("Hydro Smart Flask", "Qty: 2 • Matte Blue", "₹5,998.00")

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = BorderGray)

                    Spacer(modifier = Modifier.height(16.dp))

                    ReceiptBreakdownRow("Subtotal", "₹24,497.00")

                    Spacer(modifier = Modifier.height(10.dp))

                    ReceiptBreakdownRow("GST (18%)", "₹4,409.46")

                    Spacer(modifier = Modifier.height(10.dp))

                    ReceiptBreakdownRow("Promo (FIRSTPURCHASE500)", "-₹500.00", valueColor = SuccessGreen)

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = BorderGray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grand Total",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralDark
                        )
                        Text(
                            text = "₹28,406.46",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Download PDF */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Download PDF Receipt",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ReceiptItemRow(name: String, details: String, price: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = details, fontSize = 13.sp, color = NeutralGray)
        }
        Text(text = price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
    }
}

@Composable
private fun ReceiptBreakdownRow(label: String, value: String, valueColor: Color = NeutralDark) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = NeutralGray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
