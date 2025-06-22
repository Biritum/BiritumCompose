package com.biritum.compose.nodegraph

import kotlin.test.Test
import kotlin.test.assertEquals

class LayersTest {

    @Test
    fun simpleGraph() {

        val nodes = listOf("A", "B")
        val edges = listOf(Edge("B", "A"))

        val layers = findLayers(nodes, edges)

        val expected = listOf(setOf("A"), setOf("B"))
        assertEquals(expected, layers)
    }

    @Test
    fun pushingRecursive() {

        val nodes = listOf("A", "B", "C")
        val edges = listOf(Edge("B", "A"), Edge("C", "B"))

        val layers = findLayers(nodes, edges)
        val expected = listOf(setOf("A"), setOf("B"), setOf("C"))
        assertEquals(expected, layers)
    }

    @Test
    fun cycleOfTwo() {

        val nodes = listOf("A", "B")
        val edges = listOf(Edge("B", "A"), Edge("A", "B"))

        val layers = findLayers(nodes, edges)
        val expected = listOf(setOf("A"), setOf("B"))
        assertEquals(expected, layers)
    }

    @Test
    fun cycleOfThree() {

        val nodes = listOf("A", "B", "C")
        val edges = listOf(Edge("B", "A"), Edge("C", "B"), Edge("A", "C"))

        val layers = findLayers(nodes, edges).toSet()
        val expected = setOf(setOf("A"), setOf("B"), setOf("C"))
        assertEquals(expected, layers)
    }

    @Test
    fun edgeToSelf() {

        val nodes = listOf("A")
        val edges = listOf(Edge("A", "A"))

        val layers = findLayers(nodes, edges)
        val expected = listOf(setOf("A"))
        assertEquals(expected, layers)
    }

    @Test
    fun unknownNodesIgnored() {

        val nodes = listOf("A")
        val edges = listOf(Edge("C", "B"), Edge("A", "C"))

        val layers = findLayers(nodes, edges)
        val expected = listOf(setOf("A"))
        assertEquals(expected, layers)
    }
}

