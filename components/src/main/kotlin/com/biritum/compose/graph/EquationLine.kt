package com.biritum.compose.graph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

data class EquationLine(val color: Color, val f: (Float) -> Float) : GraphItem {
    override fun draw(scope: DrawScope, leftAxis: Axis, bottomAxis: Axis) {
        with(scope) {
            fun xPixelToYPixel(xPixel: Float): Float {
                return leftAxis.toScreen(f(bottomAxis.toAxis(xPixel)))
            }

            val p = Path()
            p.moveTo(0f, xPixelToYPixel(0f))

            (0 until bottomAxis.screenSize).forEach { x ->
                p.lineTo(x.toFloat(), xPixelToYPixel(x.toFloat()))
            }
            drawPath(color = color, path = p, style = Stroke(width = 3f))
        }
    }
}