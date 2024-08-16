package com.example.musicplayer.util

import kotlin.math.round

fun calculateViewProgress(
    maxValue: Int,
    currentValue: Int,
    maxViewSize: Int
): Int = round((currentValue.toDouble() / maxValue * maxViewSize)).toInt()

fun calculateProgress(
    maxValue: Int,
    currentViewSize: Float,
    maxViewSize: Float
): Int = round(((currentViewSize / maxViewSize) * maxValue)).toInt()