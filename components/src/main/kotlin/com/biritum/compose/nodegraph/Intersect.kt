package com.biritum.compose.nodegraph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.toOffset

fun IntRect.intersectionFrom(targetPoint: Offset): Offset {
    val line = Line(center.toOffset(), targetPoint)
    val yCandidate = if (line.dy < 0) {
        top
    } else {
        bottom
    }.toFloat()

    val xCandidate = line.xAt(yCandidate)
    return when {
        xCandidate.isNaN() -> Offset(left.toFloat(),yCandidate)
        xCandidate < left -> Offset(left.toFloat(), line.yAt(left.toFloat()))
        xCandidate > right -> Offset(right.toFloat(), line.yAt(right.toFloat()))
        else -> Offset(xCandidate, yCandidate)
    }
}
