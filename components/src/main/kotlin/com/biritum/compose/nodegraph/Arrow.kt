package com.biritum.compose.nodegraph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

data class Arrow(val start: Offset,
                 val end: Offset,
                 val color: Color = Color.Blue,
                 val arrowHead : ArrowHead = ArrowHead()
)

data class ArrowHead(val length: Float = 10f,
                     val angle: Angle = Angle.fromDegrees(25),
                     val fill: Boolean = false)

fun DrawScope.drawArrow(start: Offset, end: Offset, color: Color = Color.Blue) {
    drawLine(color = color, start = start, end = end)
    drawArrowhead(start, end, color )
}

fun DrawScope.drawArrowhead(
    start: Offset,
    end: Offset,
    color: Color = Color.Blue,
    arrowHead : ArrowHead = ArrowHead()
) {
    val lineAngle = Line(start, end).angle
    fun drawHeadSide(angle: Angle): Offset {
        val angle2 = lineAngle + Angle.pi + angle
        val x2 = end.x + angle2.cos * arrowHead.length
        val y2 = end.y + angle2.sin * arrowHead.length
        return Offset(x2, y2)
    }

    val p1 = drawHeadSide(arrowHead.angle)
    val p2 = drawHeadSide(-arrowHead.angle)

    if (arrowHead.fill) {
        val trianglePath = Path().apply {
            moveTo(p1.x, p1.y)
            lineTo(end.x, end.y)
            lineTo(p2.x, p2.y)
        }
        drawPath(path = trianglePath, color = color)
    } else {
        drawLine(color, end, p1)
        drawLine(color, end, p2)
    }
}
