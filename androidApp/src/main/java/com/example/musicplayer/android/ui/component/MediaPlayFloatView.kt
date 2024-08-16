package com.example.musicplayer.android.ui.component

import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.musicplayer.android.R
import com.example.musicplayer.android.ui.tracks.model.MusicDataState
import com.example.musicplayer.util.formatTime
import com.example.musicplayer.utils.toDp
import md_theme_light_onPrimary
import pale_blue
import kotlin.math.floor

private const val ANIMATION_DURATION = 500
private const val CORNER_RADIUS_MAX = 32
private const val PARENT_LAYOUT_BOTTOM_PADDING = 2
private const val IMAGE_START_PADDING = 24
private const val IMAGE_TOP_PADDING = 24
private const val PARENT_HORIZONTAL_PADDING = 8
private const val PARENT_VERTICAL_PADDING = 4

@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MediaPlayFloatView(
    modifier: Modifier = Modifier,
    screenWidth: Int, // parent layout width
    screenHeight: Int, // parent layout height
    musicTrack: MusicDataState,
    duration: Int,
    progress: Int,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    paddingChanged: (Dp) -> Unit,
    onProgressReleased: ((Int) -> Unit)? = null,
) {
    val density = LocalDensity.current.density

    // Parent layout sizes in dp
    val screenWidthInDp by remember {
        mutableStateOf(screenWidth.toDp(density))
    }
    val screenHeightInDp by remember {
        mutableStateOf(screenHeight.toDp(density))
    }

    // Minimum size of the float view in px
    val minimumHeight = 64.dp
    val minimumImageSize = 48.dp
    // Offset of float view height translation in px
    var offsetY by remember {
        mutableStateOf(minimumHeight)
    }

    /**
     * Indicates whether the float view is collapsed or expanded
     * collapsed: It is in minimum height
     * expanded: It is in maximum height
     */
    var state by remember {
        mutableStateOf(FloatingViewState.COLLAPSED)
    }

    /**
     * Indicates whether the float view is animating or not
     * When the float view is touched and released the view goes on smoothly by animation
     */
    var isAnimating by remember { mutableStateOf(false) }

    /**
     * The rawY of first touch on view
     */
    var initializedOffset by remember {
        mutableStateOf(0.dp)
    }

    /**
     * Holds the current value of offsetY in first touch of view
     */
    var initializedOffsetY by remember {
        mutableStateOf(0.dp)
    }

    val parentLayoutHeightAnimation by animateDpAsState(
        targetValue = offsetY,
        animationSpec = tween(durationMillis = if (isAnimating) ANIMATION_DURATION else 0),
        finishedListener = {
            val padding = calculateParentLayoutBottomPadding(
                offsetY = offsetY,
                minimumHeight = minimumHeight,
                screenHeight = screenHeightInDp
            )
            paddingChanged(padding)
        },
        label = ""
    )

    val parentLayoutRoundedCornerRadiusAnimation by animateDpAsState(
        targetValue = calculateParentLayoutRoundedCornerRadius(
            offsetY = offsetY,
            minimumHeight = minimumHeight,
            screenHeight = screenHeightInDp
        ),
        animationSpec = tween(durationMillis = if (isAnimating) ANIMATION_DURATION else 0),
        label = ""
    )

    // Music artist image padding start animation
    val imagePaddingStartAnimation by animateDpAsState(
        targetValue = calculateImageStartPadding(
            offsetY = offsetY,
            minimumHeight = minimumHeight,
            screenHeight = screenHeightInDp
        ),
        animationSpec = tween(durationMillis = if (isAnimating) ANIMATION_DURATION else 0),
        label = ""
    )

    // Music artis image padding top animation
    val imagePaddingTopAnimation by animateDpAsState(
        targetValue = calculateImageTopPadding(
            offsetY = offsetY,
            minimumHeight = minimumHeight,
            screenHeight = screenHeightInDp
        ),
        animationSpec = tween(durationMillis = if (isAnimating) ANIMATION_DURATION else 0),
        label = ""
    )

    // Music artist image size animation
    val imageSizeAnimation by animateDpAsState(
        targetValue = calculateImageSize(
            offsetY = offsetY,
            minimumHeight = minimumHeight,
            minimumImageSize = minimumImageSize,
            screenWidth = screenWidthInDp,
            screenHeight = screenHeightInDp
        ),
        animationSpec = tween(durationMillis = if (isAnimating) ANIMATION_DURATION else 0),
        label = ""
    )

    val collapsedViewsAlphaAnimation by animateFloatAsState(
        targetValue = calculateCollapsedViewsAlpha(
            offsetY = offsetY,
            minimumHeight = minimumHeight,
            screenHeight = screenHeightInDp
        ),
        animationSpec = tween(durationMillis = if (isAnimating) ANIMATION_DURATION else 0),
        label = ""
    )

    val expandedViewsAlphaAnimation by animateFloatAsState(
        targetValue = calculateExpandedViewsAlpha(
            offsetY = offsetY,
            minimumHeight = minimumHeight,
            screenHeight = screenHeightInDp
        ),
        animationSpec = tween(durationMillis = if (isAnimating) ANIMATION_DURATION else 0),
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(parentLayoutHeightAnimation)
            .background(
                color = pale_blue,
                shape = RoundedCornerShape(parentLayoutRoundedCornerRadiusAnimation)
            )
            .padding(
                top = PARENT_VERTICAL_PADDING.dp,
                bottom = PARENT_VERTICAL_PADDING.dp,
                start = PARENT_HORIZONTAL_PADDING.dp,
                end = PARENT_HORIZONTAL_PADDING.dp
            )
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { motion ->
                val rawY = motion.rawY.toDp(density)
                when (motion.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isAnimating = false
                        initializedOffset = rawY
                        initializedOffsetY = offsetY
                    }

                    MotionEvent.ACTION_MOVE -> {
                        isMotionEventActionMoveValid(
                            initializedOffsetY = initializedOffsetY,
                            initializedOffset = initializedOffset,
                            rawY = rawY,
                            minimumHeight = minimumHeight,
                            screenHeight = screenHeightInDp
                        )?.let {
                            offsetY = it
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        val swipeInfo = handleMotionEventActionUp(
                            state = state,
                            offsetY = offsetY,
                            screenHeight = screenHeightInDp,
                            minimumHeight = minimumHeight
                        )
                        offsetY = swipeInfo.offsetY
                        state = swipeInfo.state
                        isAnimating = true
                    }
                }
                return@pointerInteropFilter true
            })
        ConstraintLayout(
            modifier = modifier.fillMaxSize()
        ) {
            val (musicImage, musicLabels, playerButtonActions, bigPlayerButtonActions) = createRefs()

            MediaArtistImage(
                modifier = Modifier.constrainAs(musicImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
                uri = musicTrack.imageUri,
                paddingTopAnimation = imagePaddingTopAnimation,
                paddingStartAnimation = imagePaddingStartAnimation,
                sizeAnimation = imageSizeAnimation
            )

            MediaTexts(
                modifier = Modifier.constrainAs(musicLabels) {
                    top.linkTo(playerButtonActions.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(playerButtonActions.bottom)
                    end.linkTo(playerButtonActions.start)
                    width = Dimension.fillToConstraints
                },
                alphaAnimation = collapsedViewsAlphaAnimation,
                title = musicTrack.title,
                artistName = musicTrack.artistName
            )

            MediaActionButtons(
                modifier = Modifier.constrainAs(playerButtonActions) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                },
                alphaAnimation = collapsedViewsAlphaAnimation,
                isPlaying = isPlaying,
                onPreviousClick = onPreviousClick,
                onPlayPauseClick = onPlayPauseClick,
                onNextClick = onNextClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(bigPlayerButtonActions) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .padding(
                        top = calculatePinProgressTopPadding(
                            screenWidth = screenWidthInDp
                        ),
                        start = 16.dp,
                        end = 16.dp
                    )
                    .alpha(expandedViewsAlphaAnimation)
            ) {
                PinProgressBar(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    maxProgress = duration,
                    currentProgress = progress,
                    pinRadius = 12.dp,
                    progressColor = Color.Green,
                    progressBackgroundColor = Color.LightGray,
                    progressTextSize = 14.sp,
                    inProgressTextSize = 14.sp,
                    progressTextColor = Color.Red,
                    inProgressTextColor = Color.Red,
                    formatText = {
                        return@PinProgressBar formatTime(it)
                    },
                    onProgressReleased = onProgressReleased
                )

                Spacer(Modifier.height(8.dp))

                MediaActionButtons(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    alphaAnimation = expandedViewsAlphaAnimation,
                    isPlaying = isPlaying,
                    onPreviousClick = onPreviousClick,
                    onPlayPauseClick = onPlayPauseClick,
                    onNextClick = onNextClick
                )
            }
        }
    }
}

@Composable
fun MediaTexts(
    modifier: Modifier = Modifier,
    alphaAnimation: Float,
    title: String,
    artistName: String,
) {
    Column(
        modifier = modifier
            .padding(start = 54.dp, end = 4.dp)
            .alpha(alphaAnimation)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = md_theme_light_onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = artistName,
            style = MaterialTheme.typography.labelSmall,
            color = md_theme_light_onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MediaArtistImage(
    modifier: Modifier = Modifier,
    uri: String,
    paddingTopAnimation: Dp,
    paddingStartAnimation: Dp,
    sizeAnimation: Dp,
) {
    AsyncImage(
        modifier = modifier
            .padding(
                top = paddingTopAnimation,
                start = paddingStartAnimation
            )
            .size(sizeAnimation)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        placeholder = rememberAsyncImagePainter(model = R.drawable.person),
        error = rememberAsyncImagePainter(model = R.drawable.person),
        model = uri,
        contentDescription = "Music image",
    )
}

@Composable
fun MediaActionButtons(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    alphaAnimation: Float,
    isPlaying: Boolean,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    val isActionButtonEnabled = alphaAnimation == 1f
    Row(
        modifier = modifier
            .alpha(alphaAnimation),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        IconButton(onClick = {
            onPreviousClick()
        }, enabled = isActionButtonEnabled) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = android.R.drawable.ic_media_previous),
                contentDescription = "Previous",
                tint = md_theme_light_onPrimary
            )
        }

        IconButton(onClick = {
            onPlayPauseClick()
        }, enabled = isActionButtonEnabled) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play),
                contentDescription = "Play/Pause",
                tint = md_theme_light_onPrimary
            )
        }

        IconButton(onClick = {
            onNextClick()
        }, enabled = isActionButtonEnabled) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = android.R.drawable.ic_media_next),
                contentDescription = "Next",
                tint = md_theme_light_onPrimary
            )
        }
    }
}

enum class FloatingViewState {
    COLLAPSED, EXPANDED
}

private fun isMotionEventActionMoveValid(
    initializedOffsetY: Dp,
    initializedOffset: Dp,
    rawY: Dp,
    minimumHeight: Dp,
    screenHeight: Dp,
): Dp? {
    (initializedOffsetY + (initializedOffset - rawY)).let {
        if (it in minimumHeight..screenHeight) {
            return it
        }
        return null
    }
}

private fun handleMotionEventActionUp(
    state: FloatingViewState,
    offsetY: Dp,
    screenHeight: Dp,
    minimumHeight: Dp,
): SwipeInfo {
    return if (state == FloatingViewState.COLLAPSED) {
        handleCollapsedState(
            offsetY = offsetY,
            screenHeight = screenHeight,
            minimumHeight = minimumHeight
        )
    } else {
        handleExpandedState(
            offsetY = offsetY,
            screenHeight = screenHeight,
            minimumHeight = minimumHeight
        )
    }
}

private fun handleCollapsedState(
    offsetY: Dp,
    screenHeight: Dp,
    minimumHeight: Dp,
): SwipeInfo {
    val canSwipeUp = isSwipeUpBeyondThreshold(
        offsetY = offsetY,
        screenHeight = screenHeight,
        threshold = 30
    )
    return if (canSwipeUp) {
        // animate up
        createExpandViewSwipeInfo(screenHeight = screenHeight)
    } else {
        // animate down
        createCollapseViewSwipeInfo(minimumHeight = minimumHeight)
    }
}

private fun handleExpandedState(
    offsetY: Dp,
    screenHeight: Dp,
    minimumHeight: Dp,
): SwipeInfo {
    val canSwipeUp = isSwipeUpBeyondThreshold(
        offsetY = offsetY,
        screenHeight = screenHeight,
        threshold = 70
    )
    return if (canSwipeUp) {
        // animate up
        createExpandViewSwipeInfo(screenHeight = screenHeight)
    } else {
        // animate down
        createCollapseViewSwipeInfo(minimumHeight = minimumHeight)
    }
}

private fun isSwipeUpBeyondThreshold(
    offsetY: Dp,
    screenHeight: Dp,
    threshold: Int,
): Boolean {
    return (offsetY / screenHeight) * 100 >= threshold
}

private fun createExpandViewSwipeInfo(screenHeight: Dp) = SwipeInfo(
    state = FloatingViewState.EXPANDED,
    offsetY = screenHeight
)

private fun createCollapseViewSwipeInfo(minimumHeight: Dp) = SwipeInfo(
    state = FloatingViewState.COLLAPSED,
    offsetY = minimumHeight
)

fun calculateParentLayoutRoundedCornerRadius(
    offsetY: Dp,
    minimumHeight: Dp,
    screenHeight: Dp
): Dp {
    val heightOffset = (offsetY - minimumHeight) / (screenHeight - minimumHeight)
    val heightDiff = 1 - heightOffset
    return (CORNER_RADIUS_MAX * heightDiff).let {
        if (it >= 0f) it else 0f
    }.dp
}

fun calculateParentLayoutBottomPadding(
    offsetY: Dp,
    minimumHeight: Dp,
    screenHeight: Dp
): Dp {
    val heightOffset = (offsetY - minimumHeight) / (screenHeight - minimumHeight)
    val heightDiff = 1 - heightOffset
    var padding = (PARENT_LAYOUT_BOTTOM_PADDING * heightDiff).let {
        if (it >= 0f) it else 0f
    }
    return floor(padding).dp
}

fun calculateImageStartPadding(
    offsetY: Dp,
    minimumHeight: Dp,
    screenHeight: Dp
): Dp {
    val heightOffset = (offsetY - minimumHeight) / (screenHeight - minimumHeight)
    return (IMAGE_START_PADDING * heightOffset).dp
}

fun calculateImageTopPadding(
    offsetY: Dp,
    minimumHeight: Dp,
    screenHeight: Dp
): Dp {
    val heightOffset = (offsetY - minimumHeight) / (screenHeight - minimumHeight)
    return (4 + (IMAGE_TOP_PADDING * heightOffset)).dp
}

fun calculateImageSize(
    offsetY: Dp,
    minimumHeight: Dp,
    minimumImageSize: Dp,
    screenWidth: Dp,
    screenHeight: Dp
): Dp {
    val heightOffset = (offsetY - minimumHeight) / (screenHeight - minimumHeight)
    val paddingSize = 2 * IMAGE_START_PADDING
    val parentPadding = PARENT_HORIZONTAL_PADDING * 2
    val widthOffset = (screenWidth.value - paddingSize - parentPadding)
    val imageSize = (widthOffset * heightOffset) + ((1 - heightOffset) * minimumImageSize.value)
    return imageSize.dp
}

fun calculateCollapsedViewsAlpha(
    offsetY: Dp,
    minimumHeight: Dp,
    screenHeight: Dp
): Float {
    val foldHeightOffset = (screenHeight / 2) - minimumHeight
    val heightOffset = (offsetY - minimumHeight) / foldHeightOffset
    return 1 - heightOffset
}

fun calculateExpandedViewsAlpha(
    offsetY: Dp,
    minimumHeight: Dp,
    screenHeight: Dp
): Float {
    val screenAlphaOffset = screenHeight - minimumHeight - (screenHeight * 0.7f)
    val alphaOffset = (offsetY - minimumHeight) - (screenHeight * 0.7f)
    return alphaOffset / screenAlphaOffset
}

fun calculatePinProgressTopPadding(
    screenWidth: Dp
): Dp{
    val paddingSize = 2 * IMAGE_START_PADDING
    val parentPadding = PARENT_HORIZONTAL_PADDING * 2
    val imageSize = (screenWidth.value - paddingSize - parentPadding)
    val paddingTop = (imageSize + (IMAGE_TOP_PADDING * 2) + (PARENT_VERTICAL_PADDING * 2)).dp
    return paddingTop
}

data class SwipeInfo(
    val state: FloatingViewState,
    val offsetY: Dp,
)