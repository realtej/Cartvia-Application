package com.cartvia.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cartvia.app.R
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

val ElectricIndigo = Color(0xFF635BFF)
val CustomInterFont = FontFamily.Default // Clean system typography applied globally to all text elements

data class OnboardingItem(
    @DrawableRes val imageRes: Int,
    val title: String,
    val subtitle: String,
    val cardBgColor: Color
)

val onboardingPages = listOf(
    OnboardingItem(
        imageRes = R.drawable.onboarding_step1,
        title = "Scan items in real time as you shop in store",
        subtitle = "Instantly read barcodes, track your budget, and enjoy fully digital updates directly as you walk through aisles.",
        cardBgColor = Color(0xFF637C95)
    ),
    OnboardingItem(
        imageRes = R.drawable.onboarding_step2,
        title = "Easily add products & manage your cart together",
        subtitle = "Share lists dynamically with friends or family members, sync live updates, and bundle your groceries perfectly.",
        cardBgColor = Color(0xFFECA374)
    ),
    OnboardingItem(
        imageRes = R.drawable.onboarding_step3,
        title = "Skip the line with fast digital checkouts",
        subtitle = "Process payments instantly within the app, generate secure receipts, and breeze past traditional lines safely.",
        cardBgColor = Color(0xFFF1B781)
    ),
    OnboardingItem(
        imageRes = R.drawable.onboarding_step4,
        title = "Navigate aisles & discover smart store insights",
        subtitle = "Locate hard-to-find goods with live routing and explore active real-time traffic maps of nearby supermarket branches.",
        cardBgColor = Color(0xFFA5A8D3)
    )
)

class CurvedBottomShape(private val curveDepthDp: Float = 40f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val curveDepth = with(density) { curveDepthDp.dp.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - curveDepth)
            quadraticTo(
                size.width / 2f, size.height + curveDepth,
                0f, size.height - curveDepth
            )
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

    // Back Handler: intercept back clicks gracefully to go back one slide instead of exiting the app abruptly
    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Horizontal Pager takes up top part of screen
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                // Calculate slide page offsets for a high-quality parallax & fade visual effect
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // High quality transition effects
                            alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                            val scale = 1f - (pageOffset.absoluteValue * 0.15f).coerceIn(0f, 0.15f)
                            scaleX = scale
                            scaleY = scale
                            translationX = pageOffset * size.width * 0.2f
                        }
                ) {
                    OnboardingStep(
                        item = onboardingPages[page]
                    )
                }
            }

            // Fixed Height Bottom Navigation Area to prevent resizing or jumping when the button conditionally shows up
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Premium Pagination Dots with animated widths
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(onboardingPages.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            val dotWidth by animateDpAsState(
                                targetValue = if (isSelected) 22.dp else 8.dp,
                                animationSpec = tween(durationMillis = 300),
                                label = "dotWidth"
                            )
                            
                            Box(
                                modifier = Modifier
                                    .height(8.dp)
                                    .width(dotWidth)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) ElectricIndigo
                                        else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }
                    }

                    // Button only appears when user reaches the final slide
                    AnimatedVisibility(
                        visible = isLastPage,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        Button(
                            onClick = onFinishOnboarding,
                            modifier = Modifier
                                .height(50.dp)
                                .padding(start = 16.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricIndigo,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Join CartVia",
                                fontFamily = CustomInterFont,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingStep(
    item: OnboardingItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top Container & Image taking up about 55% height with a custom curved shape
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .clip(CurvedBottomShape())
                .background(item.cardBgColor)
        ) {
            // Sits exactly on the bottom edge using ContentScale.Crop so it bleeds fully to edges and gets masked by the curve
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Typography: Bold, centered main Title (22sp) below header using the specified FontFamily
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = CustomInterFont,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 32.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Typography: Muted secondary Subtitle/Description (15sp, centered) filling vertical whitespace
        Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = CustomInterFont,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        )
    }
}
