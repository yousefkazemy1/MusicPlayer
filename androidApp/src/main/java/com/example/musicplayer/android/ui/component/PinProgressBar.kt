package com.example.musicplayer.android.ui.component

import android.view.MotionEvent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.util.calculateProgress
import com.example.musicplayer.util.calculateViewProgress
import com.example.musicplayer.util.formatTime
import com.example.musicplayer.utils.toDp
import com.example.musicplayer.utils.toPx

@Preview
@Composable
fun PinProgressBarPreview() {
    PinProgressBar(
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        maxProgress = 300,
        currentProgress = 20,
        formatText = {
            return@PinProgressBar formatTime(it)
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PinProgressBar(
    modifier: Modifier = Modifier,
    maxProgress: Int = 0,
    currentProgress: Int = 0,
    progressBackgroundColor: Color = MaterialTheme.colorScheme.tertiary,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    strokeSize: Dp = 6.dp,
    boldStrokeSize: Dp = 12.dp,
    pinRadius: Dp = 10.dp,
    pinColor: Color = MaterialTheme.colorScheme.onSurface,
    progressTextSize: TextUnit = 16.sp,
    progressTextColor: Color = MaterialTheme.colorScheme.onSurface,
    inProgressTextSize: TextUnit = 14.sp,
    inProgressTextColor: Color = MaterialTheme.colorScheme.onSurface,
    formatText: ((Int) -> String),
    onProgressTouched: ((Int) -> Unit)? = null,
    onProgressReleased: ((Int) -> Unit)? = null
) {
    val density = LocalDensity.current.density
    val cornerRadius = CornerRadius(8.dp.value * density, 8.dp.value * density)

    // Progress view max width and height
    var progressMaxWidth by remember { mutableFloatStateOf(0f) }
    val progressMaxHeight by remember {
        mutableStateOf(maxOf(strokeSize, boldStrokeSize, pinRadius * 2))
    }

    // Progress texts
    val maxProgressText by remember(maxProgress) {
        derivedStateOf {
            formatText(maxProgress)
        }
    }
    var progressText by remember {
        mutableStateOf(formatText(currentProgress))
    }

    // Control touching and offset x
    var isTouching by remember {
        mutableStateOf(false)
    }
    var offsetX by remember {
        mutableFloatStateOf(0f)
    }

    // Animations
    val progressTextAlphaAnimation: Float by animateFloatAsState(
        targetValue = if (isTouching) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )
    val progressStrokeSize = if (isTouching) boldStrokeSize else strokeSize
    val progressStrokeSizeAnimation by animateDpAsState(
        targetValue = progressStrokeSize,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )
    val progressOffsetYAnimation by animateDpAsState(
        targetValue = calculateProgressOffsetY(
            progressMaxHeight,
            progressStrokeSize
        ),
        animationSpec = tween(durationMillis = 300),
        label = ""
    )
    val pinSizeAnimation by animateDpAsState(
        targetValue = if (isTouching) pinRadius else (pinRadius / 2),
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult by remember {
        derivedStateOf {
            textMeasurer.measure(
                text = progressText,
                style = TextStyle(
                    fontSize = progressTextSize
                )
            )
        }
    }

    LaunchedEffect(key1 = currentProgress) {
        if (!isTouching) {
            offsetX = calculateViewProgress(
                maxValue = maxProgress,
                currentValue = currentProgress,
                maxViewSize = progressMaxWidth.toInt()
            ).toFloat()
            println("LaunchedEffect:offsetX: $currentProgress | $offsetX")
            progressText = formatText(
                calculateProgress(
                    maxValue = maxProgress,
                    currentViewSize = offsetX,
                    maxViewSize = progressMaxWidth
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(textLayoutResult.size.height.toDp(density))
                .alpha(progressTextAlphaAnimation)
        ) {
            drawText(
                textMeasurer = textMeasurer,
                text = progressText,
                style = TextStyle(
                    fontSize = progressTextSize,
                    color = progressTextColor
                ),
                topLeft = Offset(
                    x = validateProgressTextOffsetX(
                        offsetX,
                        textLayoutResult.size.width.toFloat(),
                        size.width
                    ),
                    y = 0f,
                ),
                maxLines = 1,
            )
        }

        Canvas(
            modifier = Modifier
                .padding(
                    top = textLayoutResult.size.height.toDp(density) + 8.dp
                )
                .fillMaxWidth()
                .height(progressMaxHeight)
                .onGloballyPositioned {
                    progressMaxWidth = it.size.width.toFloat()
                }
                .pointerInteropFilter { motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            offsetX = validateOffsetX(
                                offsetX = motionEvent.x,
                                maxWidth = progressMaxWidth
                            )
                            val progress = calculateProgress(
                                maxValue = maxProgress,
                                currentViewSize = offsetX,
                                maxViewSize = progressMaxWidth
                            )
                            progressText = formatText(progress)
                            onProgressTouched?.invoke(progress)
                            isTouching = true
                        }

                        MotionEvent.ACTION_UP -> {
                            offsetX = validateOffsetX(
                                offsetX = motionEvent.x,
                                maxWidth = progressMaxWidth
                            )
                            val progress = calculateProgress(
                                maxValue = maxProgress,
                                currentViewSize = offsetX,
                                maxViewSize = progressMaxWidth
                            )
                            onProgressReleased?.invoke(progress)
                            isTouching = false
                        }

                        MotionEvent.ACTION_MOVE -> {
                            offsetX = validateOffsetX(
                                offsetX = motionEvent.x,
                                maxWidth = progressMaxWidth
                            )
                            progressText = formatText(
                                calculateProgress(
                                    maxValue = maxProgress,
                                    currentViewSize = offsetX,
                                    maxViewSize = progressMaxWidth
                                )
                            )
                        }
                    }
                    return@pointerInteropFilter true
                },
        ) {
            // Draw background progress
            drawRoundRect(
                color = progressBackgroundColor,
                topLeft = Offset(
                    x = 0f,
                    y = progressOffsetYAnimation.toPx()
                ),
                size = Size(
                    size.width,
                    progressStrokeSizeAnimation.toPx()
                ),
                cornerRadius = cornerRadius,
            )

            // draw progress
            drawRoundRect(
                color = progressColor,
                topLeft = Offset(
                    x = 0f,
                    y = progressOffsetYAnimation.toPx()
                ),
                size = Size(
                    offsetX,
                    progressStrokeSizeAnimation.toPx()
                ),
                cornerRadius = cornerRadius,
            )

            drawCircle(
                color = pinColor,
                radius = pinSizeAnimation.toPx(),
                center = Offset(
                    x = offsetX,
                    y = calculatePinOffsetY(
                        progressMaxHeight,
                        density
                    ),
                )
            )
        }

        Canvas(
            modifier = Modifier
                .padding(
                    top = textLayoutResult.size.height.toDp(density) + 10.dp + progressMaxHeight,
                    start = 2.dp,
                    end = 2.dp
                )
                .fillMaxWidth()
                .height(textLayoutResult.size.height.toDp(density))
        ) {
            drawText(
                textMeasurer = textMeasurer,
                text = progressText,
                style = TextStyle(
                    fontSize = inProgressTextSize,
                    color = inProgressTextColor
                ),
                topLeft = Offset(
                    x = 0f,
                    y = 0f,
                ),
                maxLines = 1,
            )

            drawText(
                textMeasurer = textMeasurer,
                text = maxProgressText,
                style = TextStyle(
                    fontSize = inProgressTextSize,
                    color = inProgressTextColor
                ),
                topLeft = Offset(
                    x = size.width - textLayoutResult.size.width,
                    y = 0f,
                ),
                maxLines = 1,
            )
        }
    }
}

fun validateOffsetX(offsetX: Float, maxWidth: Float): Float {
    return if (offsetX < 0) {
        0f
    } else if (offsetX > maxWidth) {
        maxWidth
    } else {
        offsetX
    }
}

fun validateProgressTextOffsetX(
    offsetX: Float,
    textWidth: Float,
    maxWidth: Float,
): Float {
    return if ((offsetX - (textWidth / 2)) < 0) {
        0f
    } else if ((offsetX + (textWidth / 2)) > maxWidth) {
        maxWidth - textWidth
    } else {
        offsetX - (textWidth / 2)
    }
}

private fun calculateProgressOffsetY(
    progressMaxHeight: Dp,
    strokeSize: Dp,
): Dp {
    return (progressMaxHeight / 2) - (strokeSize / 2)
}

private fun calculatePinOffsetY(
    progressMaxHeight: Dp,
    density: Float,
): Float {
    val pinOffsetY = (progressMaxHeight / 2)
    return pinOffsetY.toPx(density)
}