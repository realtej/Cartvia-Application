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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cartvia.app.ui.theme.BackgroundGray
import com.cartvia.app.ui.theme.BorderGray
import com.cartvia.app.ui.theme.InputGray
import com.cartvia.app.ui.theme.NeutralDark
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Sessions, 2: Payments, 3: Offers

    val notifications = listOf(
        NotificationItem(
            icon = Icons.Default.ShoppingCart,
            iconBg = Color(0xFFEFF6FF),
            iconTint = PrimaryBlue,
            title = "Session Connected!",
            time = "Just now",
            isUnread = true,
            description = "Connected to Cart #1248 at SuperMart Downtown HQ. Enjoy your checkout-free shopping."
        ),
        NotificationItem(
            icon = Icons.Default.Security,
            iconBg = Color(0xFFFFF1F2),
            iconTint = Color(0xFFE11D48),
            title = "New Login Detected",
            time = "2h ago",
            isUnread = true,
            description = "Your account was accessed from an iPhone 15 Pro near New York, USA. If this wasn't you, secure your account."
        ),
        NotificationItem(
            icon = Icons.Default.CreditCard,
            iconBg = Color(0xFFF0FDF4),
            iconTint = Color(0xFF16A34A),
            title = "Invoice Shared",
            time = "Yesterday",
            isUnread = false,
            description = "Payment of ₹28,847.00 for Cart #1248 was successfully completed. Digital receipt sent to your mail."
        )
    )

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notifications",
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
            // Filter Chips Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val filters = listOf("All", "Sessions", "Payments", "Offers")
                    filters.forEachIndexed { index, name ->
                        val isSelected = selectedFilter == index
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PrimaryBlue else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryBlue else BorderGray),
                            modifier = Modifier.clickable { selectedFilter = index }
                        ) {
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else NeutralDark,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "RECENT UPDATES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(notifications) { item ->
                NotificationCard(item)
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(item.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.time,
                            fontSize = 12.sp,
                            color = NeutralGray
                        )
                        if (item.isUnread) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = NeutralGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

data class NotificationItem(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val time: String,
    val isUnread: Boolean,
    val description: String
)
