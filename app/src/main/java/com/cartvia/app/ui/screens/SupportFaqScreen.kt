package com.cartvia.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cartvia.app.ui.components.CartviaInputField
import com.cartvia.app.ui.theme.BackgroundGray
import com.cartvia.app.ui.theme.BorderGray
import com.cartvia.app.ui.theme.LightOrangeBg
import com.cartvia.app.ui.theme.NeutralDark
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportFaqScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("How do I resolve weight issue...") }

    val faqs = remember {
        mutableStateListOf(
            FaqItem("How does automated weight detection work?", "Sensors on the cart continuously measure weight against expected item weights to ensure accuracy.", false),
            FaqItem("What should I do on a weight mismatch error?", "Ensure all items inside the cart are scanned, or tap 'Scan Missing Item' or request staff help.", false),
            FaqItem("Can I scan multiple products at once?", "Yes, scan items one by one as you place them into the basket.", false)
        )
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Support & FAQ",
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
            item {
                CartviaInputField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "",
                    placeholder = "How do I resolve weight issue...",
                    trailingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = NeutralGray, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Report Cart Issue Alert Box
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = LightOrangeBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Report Cart Issue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WarningOrange)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Are you currently at a smart-cart with mechanical or weight sensor issues? Alert our support desk immediately.",
                            fontSize = 13.sp,
                            color = WarningOrange.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WarningOrange)
                        ) {
                            Text("Submit Ticket", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(faqs) { faq ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                val idx = faqs.indexOf(faq)
                                faqs[idx] = faq.copy(expanded = !faq.expanded)
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faq.question,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeutralDark,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (faq.expanded) Icons.Default.Remove else Icons.Default.Add,
                                contentDescription = null,
                                tint = NeutralDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        AnimatedVisibility(visible = faq.expanded) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = BorderGray)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = faq.answer,
                                    fontSize = 13.sp,
                                    color = NeutralGray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clickable { },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Live Chat", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clickable { },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Call Support", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                        }
                    }
                }
            }
        }
    }
}

data class FaqItem(
    val question: String,
    val answer: String,
    val expanded: Boolean
)
