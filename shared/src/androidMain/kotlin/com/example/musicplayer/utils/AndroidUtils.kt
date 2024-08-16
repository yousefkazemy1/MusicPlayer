package com.example.musicplayer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Int.toDp(density: Float) = (this / density).dp

fun Float.toDp(density: Float) = (this / density).dp

fun Dp.toPx(density: Float) = (value * density)

fun isSdkEqualOrGreaterThanO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
val isSdkEqualOrGreaterThanTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun Context.registerBroadcastReceiver(receiver: BroadcastReceiver?, intentFilter: IntentFilter){
    if (isSdkEqualOrGreaterThanTiramisu) {
        registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
    } else {
        registerReceiver(receiver, intentFilter)
    }
}