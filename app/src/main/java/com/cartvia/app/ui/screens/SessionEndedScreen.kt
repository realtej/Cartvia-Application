package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
fun SessionEndedScreen(
    cartId: String,
    finalBill: String,
    onDone: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundGray)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)) { append("C") }
                        withStyle(SpanStyle(color = Color(0xFF10B981), fontWeight = FontWeight.Bold)) { append("a") }
                        withStyle(SpanStyle(color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)) { append("r") }
                        withStyle(SpanStyle(color = Color(0xFF059669), fontWeight = FontWeight.Bold)) { append("t") }
                        withStyle(SpanStyle(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)) { append("v") }
                        withStyle(SpanStyle(color = Color(0xFF10B981), fontWeight = FontWeight.Bold)) { append("a") }
                        withStyle(SpanStyle(color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)) { append("i") }
                    },
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Smart-Cart #$cartId",
                    fontSize = 14.sp,
                    color = NeutralGray
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Session Ended Successfully",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Smart-Cart #$cartId has been returned and disconnected. Thank you for shopping with us!",
                fontSize = 14.sp,
                color = NeutralGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

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
                        Text("Cart Return Status", fontSize = 14.sp, color = NeutralGray)
                        Text("Confirmed", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Final Bill", fontSize = 14.sp, color = NeutralGray)
                        Text("₹$finalBill", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Done",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
