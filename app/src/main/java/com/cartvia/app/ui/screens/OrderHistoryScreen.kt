package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.cartvia.app.ui.theme.LightGreenBg
import com.cartvia.app.ui.theme.NeutralDark
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SecondaryBlue
import com.cartvia.app.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onNavigateToReceipt: (String) -> Unit,
    onBack: () -> Unit
) {
    val historyItems = listOf(
        HistoryCardData(
            id = "INV-2026-004",
            cartTitle = "Cart #12 (Current)",
            badge = "SUCCESS",
            isSuccess = true,
            isCurrent = true,
            date = "Oct 24, 2026 • 11:32 AM",
            summary = "AeroSound Max, Hydro Fla...",
            price = "₹28,406.46"
        ),
        HistoryCardData(
            id = "INV-2026-003",
            cartTitle = "Cart #09",
            badge = "DELIVERED",
            isSuccess = false,
            isCurrent = false,
            date = "Oct 10, 2026 • 04:15 PM",
            summary = "Tech Organiser Pouch",
            price = "₹1,299.00"
        ),
        HistoryCardData(
            id = "INV-2026-002",
            cartTitle = "Cart #05",
            badge = "DELIVERED",
            isSuccess = false,
            isCurrent = false,
            date = "Sep 28, 2026 • 02:45 PM",
            summary = "Minimalist Wallet, USB-C D...",
            price = "₹12,450.00"
        )
    )

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Order History",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp)
        ) {
            items(historyItems) { item ->
                OrderHistoryCard(item, onClick = { onNavigateToReceipt(item.id) })
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    data: HistoryCardData,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            if (data.isCurrent) 1.5.dp else 1.dp,
            if (data.isCurrent) PrimaryBlue else BorderGray
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = data.cartTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.date,
                        fontSize = 13.sp,
                        color = NeutralGray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (data.isSuccess) LightGreenBg else SecondaryBlue
                ) {
                    Text(
                        text = data.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (data.isSuccess) SuccessGreen else PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = BorderGray)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.summary,
                        fontSize = 14.sp,
                        color = NeutralGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.price,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "View",
                        tint = NeutralDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

data class HistoryCardData(
    val id: String,
    val cartTitle: String,
    val badge: String,
    val isSuccess: Boolean,
    val isCurrent: Boolean,
    val date: String,
    val summary: String,
    val price: String
)
