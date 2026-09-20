package com.cartvia.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cartvia.app.ui.theme.ErrorRed
import kotlin.math.roundToInt

@Composable
fun SwipeDismissWrapper(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val revealWidth = with(LocalDensity.current) { 72.dp.toPx() }
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffset by animateFloatAsState(targetValue = offsetX)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ErrorRed)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        val newOffset = (offsetX + dragAmount).coerceIn(0f, revealWidth)
                        offsetX = newOffset
                    },
                    onDragEnd = {
                        offsetX = if (offsetX > revealWidth * 0.4f) revealWidth else 0f
                    },
                    onDragCancel = {
                        offsetX = 0f
                    }
                )
            }
    ) {
        // Background Delete block (on the Left)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(72.dp)
                .fillMaxHeight()
                .background(ErrorRed)
                .clickable {
                    offsetX = 0f
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Item",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // Foreground component block sliding right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .background(MaterialTheme.colorScheme.surface)
        ) {
            content()
        }
    }
}
