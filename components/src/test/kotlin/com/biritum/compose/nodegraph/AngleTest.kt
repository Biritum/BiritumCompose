package com.biritum.compose.nodegraph

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class AngleTest{
    @Test
    fun conversionFromDegrees(){
        assertEquals(Angle.fromRadians(0).radians, Angle.fromDegrees(0).radians, 0.001f)
        assertEquals(Angle.pi.radians, Angle.fromDegrees(180).radians, 0.001f)
        assertEquals(Angle.fromRadians(-PI).radians, Angle.fromDegrees(-180).radians, 0.001f)
    }
}