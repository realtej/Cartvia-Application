package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cartvia.app.data.model.CartItemDto
import com.cartvia.app.ui.theme.BackgroundGray
import com.cartvia.app.ui.theme.BorderGray
import com.cartvia.app.ui.theme.InputGray
import com.cartvia.app.ui.theme.LightOrangeBg
import com.cartvia.app.ui.theme.NeutralDark
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SuccessGreen
import com.cartvia.app.ui.theme.WarningOrange
import com.cartvia.app.util.ApiResult
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    sessionId: String,
    viewModel: ShopViewModel,
    onCheckout: (String) -> Unit
) {
    val cartState by viewModel.cartState.collectAsState()
    val shoppingStatus by viewModel.shoppingStatus.collectAsState()
    val orderId by viewModel.orderId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var isPushAssistOn by remember { mutableStateOf(true) }
    var isCartStoppedWarning by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        viewModel.initSession(sessionId)
    }

    LaunchedEffect(sessionId) {
        viewModel.uiEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(orderId) {
        orderId?.let { onCheckout(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundGray,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundGray)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Cartvia Logo Header
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)) { append("C") }
                        withStyle(SpanStyle(color = Color(0xFF10B981), fontWeight = FontWeight.Bold)) { append("a") }
                        withStyle(SpanStyle(color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)) { append("r") }
                        withStyle(SpanStyle(color = Color(0xFF059669), fontWeight = FontWeight.Bold)) { append("t") }
                        withStyle(SpanStyle(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)) { append("v") }
                        withStyle(SpanStyle(color = Color(0xFF10B981), fontWeight = FontWeight.Bold)) { append("i") }
                        withStyle(SpanStyle(color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)) { append("a") }
                    },
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Smart-Cart #5351 connected",
                    fontSize = 14.sp,
                    color = NeutralGray
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Push Assist / Cart Warning Banner
            if (isCartStoppedWarning) {
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { isCartStoppedWarning = false },
                    shape = RoundedCornerShape(12.dp),
                    color = LightOrangeBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Cart stopped — please check your cart screen",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { isPushAssistOn = !isPushAssistOn },
                    shape = RoundedCornerShape(12.dp),
                    color = InputGray,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isPushAssistOn) SuccessGreen else NeutralGray)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isPushAssistOn) "Push Assist: On" else "Push Assist: Off",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPushAssistOn) SuccessGreen else NeutralDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (val state = cartState) {
                is ApiResult.Success -> {
                    val cartItems = state.data.items.ifEmpty {
                        listOf(
                            CartItemDto("1", "Whole Milk 1 Gallon", 1, "350.00", aisleNumber = "A1"),
                            CartItemDto("2", "Organic Sourdough Bread", 2, "420.00", aisleNumber = "B3"),
                            CartItemDto("3", "Fresh Strawberries 500g", 1, "280.00", aisleNumber = "A2")
                        )
                    }

                    Text(
                        text = "Items in Cart (${cartItems.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems) { item ->
                            var isChecked by remember { mutableStateOf(item.productId == "2") }
                            ActiveCartItemCard(
                                name = item.name,
                                quantity = item.quantity,
                                price = "₹${item.unitPrice}",
                                isChecked = isChecked,
                                onCheckToggle = { isChecked = !isChecked }
                            )
                        }
                    }
                }
                else -> {
                    val defaultItems = listOf(
                        CartItemDto("1", "Whole Milk 1 Gallon", 1, "350.00"),
                        CartItemDto("2", "Organic Sourdough Bread", 2, "420.00"),
                        CartItemDto("3", "Fresh Strawberries 500g", 1, "280.00")
                    )

                    Text(
                        text = "Items in Cart (${defaultItems.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(defaultItems) { item ->
                            var isChecked by remember { mutableStateOf(item.productId == "2") }
                            ActiveCartItemCard(
                                name = item.name,
                                quantity = item.quantity,
                                price = "₹${item.unitPrice}",
                                isChecked = isChecked,
                                onCheckToggle = { isChecked = !isChecked }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveCartItemCard(
    name: String,
    quantity: Int,
    price: String,
    isChecked: Boolean,
    onCheckToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCheckToggle),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCheckToggle,
                modifier = Modifier.size(28.dp)
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Checked",
                        tint = SuccessGreen,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(InputGray)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) NeutralGray else NeutralDark,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Qty: $quantity",
                    fontSize = 13.sp,
                    color = NeutralGray
                )
            }

            Text(
                text = price,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )
        }
    }
}
