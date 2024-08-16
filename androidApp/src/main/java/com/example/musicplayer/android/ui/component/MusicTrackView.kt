package com.example.musicplayer.android.ui.component

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.example.musicplayer.android.R
import com.example.musicplayer.android.ui.tracks.model.MusicDataState

@Preview
@Composable
fun MusicTrackPreview() {
    MusicTrackView(
        modifier = Modifier,
        musicTrack = MusicDataState(
            id = 1, title = "Dooset Daram", artistName = "Majid Razavi", imageUri = "", musicUri = ""
        ),
        index = 0
    ) {}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicTrackView(
    modifier: Modifier = Modifier,
    musicTrack: MusicDataState,
    index: Int,
    onClick: (MusicTrackClickEvents) -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp
            )
            .combinedClickable(
                onClick = {
                    onClick(MusicTrackClickEvents.OnItemClick(musicTrack, index))
                },
                onLongClick = {
                    onClick(MusicTrackClickEvents.OnItemLongClick(musicTrack))
                }
            )
    ) {
        val (musicImage, musicLabels, moreIcon) = createRefs()
        AsyncImage(
            modifier = Modifier
                .padding(2.dp)
                .width(56.dp)
                .height(56.dp)
                .clip(MaterialTheme.shapes.small)
                .constrainAs(musicImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.person),
            error = painterResource(id = R.drawable.person),
            model = Uri.parse(musicTrack.imageUri),
            contentDescription = "Music image",
        )

        Column(
            modifier = Modifier
                .constrainAs(musicLabels) {
                    top.linkTo(musicImage.top)
                    start.linkTo(musicImage.end)
                    bottom.linkTo(musicImage.bottom)
                    end.linkTo(moreIcon.start)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 4.dp, end = 4.dp)
        ) {
            Text(
                text = musicTrack.title,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = musicTrack.artistName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}