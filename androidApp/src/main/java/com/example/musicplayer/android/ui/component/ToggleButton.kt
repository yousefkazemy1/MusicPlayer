package com.example.musicplayer.android.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ToggleButton(
    modifier: Modifier = Modifier,
    isLeft: Boolean,
    onToggleChanged: (Boolean) -> Unit
) {
    Canvas(modifier = modifier.clickable {
        onToggleChanged(!isLeft)
    }) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val topLeft = Offset(0f, 0f)
        val cornerRadius = CornerRadius(24.dp.toPx())
        drawRoundRect(Color.Gray, topLeft = topLeft, size = Size(canvasWidth, canvasHeight), cornerRadius = cornerRadius)

        val cornerRadiusMargin = cornerRadius.x / 4
        val margin = 8.dp.toPx() + cornerRadiusMargin
        val radius = (canvasHeight / 2) - (6.dp.toPx())
        val center = if (isLeft) {
            Offset(radius + margin, canvasHeight / 2)
        } else {
            Offset(canvasWidth - (radius + margin), canvasHeight / 2)
        }
        drawCircle(Color.Blue, center = center, radius = radius)
    }
}