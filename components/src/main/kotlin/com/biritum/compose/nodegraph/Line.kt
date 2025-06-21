package com.biritum.compose.nodegraph

import androidx.compose.ui.geometry.Offset
import kotlin.math.atan2

data class Line(val start: Offset, val end: Offset) {
    val dy = end.y - start.y
    val dx = end.x - start.x

    val angle: Angle by lazy { Angle.fromRadians(atan2(dy, dx)) }
    private val m: Float by lazy { dy / dx }

    fun xAt(y: Float) = (y - start.y) / m + start.x
    fun yAt(x: Float) = (x - start.x) * m + start.y
}

