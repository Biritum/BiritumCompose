package com.biritum.compose.nodegraph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntRect
import kotlin.test.Test
import kotlin.test.assertEquals

class IntersectTest{
    private val rectangle = IntRect(10, 10, 90, 90)

    @Test
    fun intersectOutside(){
        assertEquals( Offset(10f,10f), rectangle.intersectionFrom( Offset.Zero ))
        assertEquals( Offset(90f,90f), rectangle.intersectionFrom( Offset(100f,100f) ))
    }

    @Test
    fun intersectInside(){
        assertEquals( Offset(10f,10f), rectangle.intersectionFrom( Offset(20f,20f) ))
        assertEquals( Offset(10f,90f), rectangle.intersectionFrom( Offset(0f,100f) ))
    }

    @Test
    fun intersectCenter(){
        assertEquals( Offset(10f,90f), rectangle.intersectionFrom( Offset(50f,50f) ))
    }
}

