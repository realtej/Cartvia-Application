package com.cartvia.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cartvia.app.ui.theme.BackgroundGray
import com.cartvia.app.ui.theme.BorderGray
import com.cartvia.app.ui.theme.ErrorRed
import com.cartvia.app.ui.theme.NeutralDark
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SuccessGreen
import com.cartvia.app.util.ApiResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSuccessScreen(
    orderId: String,
    viewModel: ReceiptViewModel,
    onContinue: () -> Unit,
    onViewReceipt: () -> Unit
) {
    LaunchedEffect(orderId) {
        viewModel.loadReceipt(orderId)
        viewModel.clearSession()
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Success",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onContinue) {
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Payment successful",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Amount paid",
                fontSize = 14.sp,
                color = NeutralGray
            )

            Text(
                text = "₹1,250.00",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            TransactionDetailsCard(
                cartId = "Cart#5351",
                invoiceNumber = "Inv - 2026-19131",
                txnId = "PF8421906",
                from = "HDFC ••6421"
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "View Detailed Digital Receipt",
                fontSize = 15.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onViewReceipt)
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Continue Shopping",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFailedScreen(
    orderId: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Payment Failed",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
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
                .padding(horizontal = 24.dp),
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
                text = "Payment Failed",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Unable to complete payment",
                fontSize = 14.sp,
                color = NeutralGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Amount paid",
                fontSize = 14.sp,
                color = NeutralGray
            )

            Text(
                text = "₹1,250.00",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            TransactionDetailsCard(
                cartId = "Cart#5351",
                invoiceNumber = "Inv - 2026-19131",
                txnId = "PF8421906",
                from = "HDFC ••6421"
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Cancel and Return to Cart",
                fontSize = 15.sp,
                color = ErrorRed,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onCancel)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingPaymentScreen(
    orderId: String,
    onCancel: () -> Unit,
    onCheckAgain: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Processing Payment",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralDark
                        )
                        Text(
                            text = "Cart#5351 Checkout",
                            fontSize = 13.sp,
                            color = NeutralGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Large Circular Ring Spinner
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(SuccessGreen, PrimaryBlue, SuccessGreen)
                        ),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "₹1,250.00",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Checking Payment Status...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Checking with payment gateway... Please wait",
                fontSize = 14.sp,
                color = NeutralGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            TransactionDetailsCard(
                cartId = "Cart#5351",
                invoiceNumber = "Inv - 2026-19131",
                txnId = "PF8421906",
                from = "HDFC ••6421"
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = onCheckAgain,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Check Again", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TransactionDetailsCard(
    cartId: String,
    invoiceNumber: String,
    txnId: String,
    from: String
) {
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
                Text("Cart Id", fontSize = 14.sp, color = NeutralGray)
                Text(cartId, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Invoice Number", fontSize = 14.sp, color = NeutralGray)
                Text(invoiceNumber, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Txn ID", fontSize = 14.sp, color = NeutralGray)
                Text(txnId, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("From", fontSize = 14.sp, color = NeutralGray)
                Text(from, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
            }
        }
    }
}
