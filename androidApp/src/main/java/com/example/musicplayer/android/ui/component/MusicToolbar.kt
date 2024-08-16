package com.example.musicplayer.android.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.android.R
import com.example.musicplayer.android.ui.theme.AppTheme

@Preview()
@Composable
fun MusicToolbarPreview() {
    AppTheme {
        MusicToolbar()
    }
}

@Composable
fun MusicToolbar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 6.dp, bottom = 6.dp, start = 10.dp, end = 10.dp
            )
    ) {
        Text(
            text = stringResource(id = R.string.toolbar_title)
        )
    }
}