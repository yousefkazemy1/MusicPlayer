package com.example.musicplayer.android.ui.tracks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.musicplayer.android.ui.component.MediaPlayFloatView
import com.example.musicplayer.android.ui.component.MusicToolbar
import com.example.musicplayer.android.ui.component.MusicTrackClickEvents
import com.example.musicplayer.android.ui.component.MusicTracksListView
import com.example.musicplayer.android.ui.tracks.model.MusicDurationDataState
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun MusicTracksScreen(
    viewModel: MusicTracksViewModel = koinViewModel(),
) {
    val musicTracksState by viewModel.musicTracks.collectAsState()
    val currentTrackState by viewModel.currentTrack.collectAsState()
    val playerStatus by viewModel.playerStatus.collectAsState(initial = false)
    val musicDuration by viewModel.duration.collectAsState(initial = MusicDurationDataState())

    var padding by remember {
        mutableStateOf(2.dp)
    }

    var parentLayoutSize by remember {
        mutableStateOf(IntSize(0, 0))
    }

    val onMusicTrackClickEvents = remember {
        { clickEvent: MusicTrackClickEvents ->
            when (clickEvent) {
                is MusicTrackClickEvents.OnItemClick -> {
                    viewModel.startMusic(clickEvent.index)
                }
                else -> {}
            }
        }
    }

    val onPlayPauseClick = remember {
        {
            viewModel.startPauseMusic()
        }
    }

    val onNextClick = remember {
        {
            viewModel.goNextMusic()
        }
    }

    val onPreviousClick = remember {
        {
            viewModel.goPreviousMusic()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned {
                parentLayoutSize = it.size
            }
    ) {
        Column {
            MusicToolbar()

            MusicTracksListView(
                musicTracks = musicTracksState.toImmutableList(),
                onClick = onMusicTrackClickEvents
            )
        }

        currentTrackState?.let {
            MediaPlayFloatView(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = padding),
                screenWidth = parentLayoutSize.width,
                screenHeight = parentLayoutSize.height,
                musicTrack = it,
                isPlaying = playerStatus,
                onPlayPauseClick = onPlayPauseClick,
                onNextClick = onNextClick,
                onPreviousClick = onPreviousClick,
                duration = musicDuration.duration,
                progress = musicDuration.progress,
                paddingChanged = {
                    padding = it
                },
                onProgressReleased = { progress ->
                    viewModel.seekMusic(progress)
                }
            )
        }
    }
}

