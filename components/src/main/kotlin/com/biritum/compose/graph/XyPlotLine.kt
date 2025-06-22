package com.biritum.compose.graph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

data class XyPlotLine(val color : Color, val points : List<Offset> ) : GraphItem {
    override fun draw(scope: DrawScope, leftAxis: Axis, bottomAxis: Axis) {
        with(scope) {
            val p = Path()
            p.moveTo(bottomAxis.toScreen( points.first().x),
                leftAxis.toScreen(points.first().y))
            points.drop(1).forEach {
                p.lineTo(bottomAxis.toScreen( it.x),
                    leftAxis.toScreen(it.y))
            }
            drawPath(color = color, path = p, style = Stroke(width = 3f))
        }
    }
}