package com.biritum.compose.nodegraph

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class Angle(val radians: Float) {
    val cos: Float by lazy { cos(radians) }
    val sin: Float by lazy { sin(radians) }

    operator fun unaryMinus() = Angle(-radians)
    operator fun plus(other: Angle) = Angle(radians + other.radians)

    companion object {
        val pi = fromRadians(PI)

        fun fromDegrees(degrees: Number) = Angle((degrees.toFloat() * PI / 180f).toFloat())
        fun fromRadians(radians: Number) = Angle(radians.toFloat())
    }
}

