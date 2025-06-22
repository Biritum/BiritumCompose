package com.biritum.compose.piechart

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateSlicesTest {
    @Test
    fun oneSlice() {
        val sliced = listOf(PieChartSlice(2, Color.Blue)).calculateSlices().toList()

        assertEquals(-90f, sliced[0].start, 0.001f)
        assertEquals(360f, sliced[0].sweep, 0.001f)
    }

    @Test
    fun threeSlices() {
        val sliced = listOf(
            PieChartSlice(10f, Color.Blue),
            PieChartSlice(20f, Color.Blue),
            PieChartSlice(30f, Color.Blue)
        ).calculateSlices().toList()

        val expected = listOf(
            Slice(-90f, 60f, Color.Blue),
            Slice(-30f, 120f, Color.Blue),
            Slice(90f, 180f, Color.Blue),
        )
        assertEquals(expected, sliced)
    }
}