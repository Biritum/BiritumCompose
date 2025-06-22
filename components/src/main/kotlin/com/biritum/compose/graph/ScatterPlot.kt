package com.biritum.compose.graph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

data class ScatterPlot(val color: Color, val points: List<Offset>) : GraphItem {
    override fun draw(scope: DrawScope, leftAxis: Axis, bottomAxis: Axis) {
        with(scope) {
            points.map {
                Offset(bottomAxis.toScreen(it.x), leftAxis.toScreen(it.y))
            }.forEach {
                drawCircle(color = color, center = it, radius = 4f)
            }
        }
    }
}