package com.biritum.compose.piechart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class PieChartSlice<T>(val value: T, val color: Color) where T : Number

@Composable
fun <T : Number> PieChart(slices: Iterable<PieChartSlice<T>>, modifier: Modifier) {
    Canvas(modifier) {
        slices.calculateSlices().forEach {
            drawArc(it.color, it.start, it.sweep, true)
        }
    }
}

internal data class Slice(val start: Float, val sweep: Float, val color: Color)

internal fun <T : Number> Iterable<PieChartSlice<T>>.calculateSlices(): Iterable<Slice> {
    val sum = this.sumOf { it.value.toDouble() }.toFloat()
    val degreesPerValue = 360f / sum
    fun degreesTo(i: Int) = this.take(i).sumOf { it.value.toDouble() }.toFloat() * degreesPerValue
    return mapIndexed { index, it -> Slice(degreesTo(index) - 90, it.value.toFloat() * degreesPerValue, it.color) }
}


