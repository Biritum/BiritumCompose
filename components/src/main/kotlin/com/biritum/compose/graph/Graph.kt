package com.biritum.compose.graph

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

interface GraphItem{
    fun draw(scope: DrawScope, leftAxis: Axis, bottomAxis: Axis)
}

interface Axis {
    val min: Float
    val max: Float
    val screenSize: Int
    fun toScreen(value: Float): Float
    fun toAxis(value: Float): Float
}

@Composable
fun Graph(
    modifier: Modifier = Modifier,
    items: Iterable<GraphItem>,
    xMin: Float = 0f,
    yMin: Float = 0f,
    xMax: Float = 100f,
    yMax: Float = 100f,
) {
    val textMeasurer = rememberTextMeasurer()
    val leftAxisGraphics = LeftAxisGraphics()
    val bottomAxisGraphics = BottomAxisGraphics()

    Canvas(modifier) {
        val bottomAxisHeight = bottomAxisGraphics.height(textMeasurer)
        val leftAxis = VerticalAxisModel(yMin, yMax, size.height.toInt())
        val leftAxisWidth = leftAxisGraphics.width(textMeasurer, leftAxis)

        val graphWidth = size.width - leftAxisWidth
        val graphHeight = size.height - bottomAxisHeight

        if( graphHeight > 0 && graphWidth > 0 ) {
            val bottomAxis = HorizontalAxisModel(xMin, xMax, size.width.toInt())
            inset(leftAxisWidth, 0f, 0f, bottomAxisHeight) {
                clipRect {
                    items.forEach {
                        it.draw(this, leftAxis, bottomAxis)
                    }
                }
            }

            inset(leftAxisWidth, graphHeight, 0f, 0f) {
                bottomAxisGraphics.draw(this, textMeasurer, bottomAxis)
            }

            inset(0f, 0f, graphWidth, 0f) {
                leftAxisGraphics.draw(this, textMeasurer, leftAxis, graphHeight)
            }
        }
    }
}

private class HorizontalAxisModel(
    override val min: Float,
    override val max: Float,
    override val screenSize: Int
) : Axis {
    val scale = (max - min) / screenSize

    override fun toScreen(value: Float) = (value - min) / scale
    override fun toAxis(value: Float) = (value * scale) + min
}

private class VerticalAxisModel(
    override val min: Float,
    override val max: Float,
    override val screenSize: Int
) : Axis {
    val scale = (max - min) / screenSize

    override fun toScreen(value: Float) = screenSize - (value - min) / scale
    override fun toAxis(value: Float) = ((screenSize - value) * scale) + min
}

class LeftAxisGraphics {

    fun width(textMeasurer: TextMeasurer, axis: Axis): Float {
        val ticks = ticks(axis)
        val maxTextWidth = ticks.maxOfOrNull {
            textMeasurer.measure(it.toString()).size.width
        } ?: 0
        return maxTextWidth + tickSize + tickMargin
    }


    private fun ticks(axis : Axis ) : Sequence<Tick> {
        val minDistanceBetweenTicks = 30f
        val maxTickCount = (axis.screenSize / minDistanceBetweenTicks).toInt()
        return axis.tickPositions(maxTickCount)
    }

    fun draw(scope : DrawScope, textMeasurer : TextMeasurer, axis : Axis, graphHeight : Float) {
        with(scope) {
            upArrow(
                Offset(size.width, graphHeight),
                Offset(size.width, 0f)
            )

            ticks(axis).forEach {
                val y = axis.toScreen(it.position)
                val x = size.width - tickSize
                drawLine(Color.Black, Offset(x,y), Offset(size.width, y))

                val charSize = textMeasurer.measure("0").size
                drawText( textMeasurer, it.toString(),
                    Offset(
                        size.width - textMeasurer.measure(it.toString()).size.width - tickSize - tickMargin,
                        y -charSize.height / 2
                    ))

            }
        }
    }

    fun DrawScope.upArrow(start : Offset, end : Offset) {
        drawLine(Color.Black, end, start)
        drawLine(Color.Black, end, Offset(end.x - 3, end.y + 10 ))
        drawLine(Color.Black, end, Offset(end.x + 3, end.y + 10 ))
    }
}

typealias Formatter = (Float) -> String
data class Tick(val position : Float, val formatter : Formatter){
    override fun toString(): String {
        return formatter( position )
    }
}


const val tickSize = 5f
const val tickMargin = 3f

class BottomAxisGraphics {
    fun height(textMeasurer: TextMeasurer): Float {
        val charSize = textMeasurer.measure("0").size
        return charSize.height + tickSize + tickMargin
    }

    private fun ticks(axis: Axis): Sequence<Tick> {
        val minDistanceBetweenTicks = 30f
        val maxTickCount = (axis.screenSize / minDistanceBetweenTicks).toInt()
        return axis.tickPositions(maxTickCount)
    }

    fun draw(scope : DrawScope, textMeasurer :TextMeasurer, axis : Axis) {
        with(scope) {
            rightArrow(
                Offset(0f, 0f),
                Offset(size.width, 0f)
            )
            ticks(axis).forEach {
                val x = axis.toScreen( it.position)
                drawLine( Color.Black, Offset(x, 0f), Offset(x, tickSize) )
                val textX = x - textMeasurer.measure(it.toString()).size.width / 2

                if( textX < size.width) {
                    drawText(
                        textMeasurer, it.toString(),
                        Offset(
                            textX,
                            tickSize + tickMargin
                        )
                    )
                }
            }
        }
    }

    fun DrawScope.rightArrow(start: Offset, end:Offset) {
        drawLine(Color.Black, end, start)
        drawLine(Color.Black, end, Offset(end.x - 10, end.y - 3))
        drawLine(Color.Black, end, Offset(end.x - 10, end.y + 3))
    }
}

private fun Axis.tickPositions(maxTickCount: Int): Sequence<Tick> {
    return sequence {
        val (tickDistance, formatter) = findTickDistance(maxTickCount)
        val candidate = tickDistance * (min / tickDistance).toInt()
        if( !candidate.isNaN()) {
            var tick = if( candidate < min) candidate + tickDistance else candidate
            while(tick < max) {
                yield(Tick(tick, formatter))
                tick += tickDistance
            }
        }
    }
}

fun Axis.findTickDistance(maxTickCount: Int): Pair<Float, Formatter> {
    val maxTickSize = (max - min) /  maxTickCount
    val magnitude = maxTickSize.magnitude()
    val closestWholeNumber = 10f.pow(magnitude+1f)

    fun formatter(extraDecimals : Int) : Formatter {
        if( magnitude >= 0) return {it.toInt().toString()}
        val decimals = abs(magnitude) + extraDecimals
        return { "%.${decimals}f".format(it)}
    }

    return when {
        closestWholeNumber / 4f > maxTickSize -> Pair( closestWholeNumber / 4f, formatter(1))
        closestWholeNumber / 2f > maxTickSize -> Pair(closestWholeNumber / 2f, formatter(0))
        else -> Pair(closestWholeNumber, formatter(-1))
    }
}

private fun Number.magnitude(): Int {
    val v = floor( log10(this.toFloat()))
    return if( v.isNaN() ) 0 else v.toInt()
}
