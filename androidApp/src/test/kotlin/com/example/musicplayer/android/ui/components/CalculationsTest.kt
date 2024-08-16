package com.example.musicplayer.android.ui.components

import com.example.musicplayer.util.calculateViewProgress
import kotlin.test.Test
import kotlin.test.assertEquals


class ViewProgressCalculationsTest {
    @Test
    fun `test view progress sample 1`() {
        val result = calculateViewProgress(
            maxValue = 309,
            currentValue = 1,
            maxViewSize = 500
        )
        assertEquals(2, result)
    }

    @Test
    fun `test view progress sample 2`() {
        val result = calculateViewProgress(
            maxValue = 309,
            currentValue = 50,
            maxViewSize = 500
        )
        assertEquals(81, result)
    }

    @Test
    fun `test view progress sample 3`() {
        val result = calculateViewProgress(
            maxValue = 309,
            currentValue = 155,
            maxViewSize = 500
        )
        assertEquals(251, result)
    }

    @Test
    fun `test view progress sample 4`() {
        val result = calculateViewProgress(
            maxValue = 309,
            currentValue = 308,
            maxViewSize = 500
        )
        assertEquals(498, result)
    }

    @Test
    fun `test view progress sample 5`() {
        val result = calculateViewProgress(
            maxValue = 309,
            currentValue = 309,
            maxViewSize = 500
        )
        assertEquals(500, result)
    }
}

