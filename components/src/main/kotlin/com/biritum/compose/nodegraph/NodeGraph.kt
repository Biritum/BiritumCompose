package com.biritum.compose.nodegraph

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Edge<T>(val start: T, val end: T)

@Composable
fun <T> NodeGraph(
    edges: List<Edge<T>>,
    modifier: Modifier = Modifier,
    horizontalLayerPadding: Dp = 20.dp,
    verticalLayerPadding : Dp = 20.dp,
    content: @Composable () -> Unit) {
    InnerNodeGraph(Graph(edges, horizontalLayerPadding, verticalLayerPadding), modifier, content)
}

interface NodeGraphScope {
    fun Modifier.nodeId(id: Any) = this.then(NodeId(id))

    companion object : NodeGraphScope
}

private data class NodeId(val id: Any) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@NodeId
}

@Composable
private fun <T> InnerNodeGraph(graphModel: Graph<T>, modifier: Modifier = Modifier, content: @Composable () -> Unit) {

    fun ContentDrawScope.drawEdges() {
        graphModel.edgeArrowsToDraw().forEach { drawArrow(it.start, it.end) }
    }

    val positions: Map<T, IntOffset> = graphModel.positions.collectAsState().value
    Layout(content, modifier.clipToBounds().drawWithCache {
        onDrawWithContent {
            drawContent()
            drawEdges()
        }
    }.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { position -> graphModel.dragStart(position) },
            onDrag = { _, dragAmount -> graphModel.onDrag(dragAmount) })
    }) { measurable, constraints ->
        fun Measurable.nodeId(): T {
            @Suppress("UNCHECKED_CAST") return (parentData as? NodeId)?.id as T
        }

        val measured = measurable.associate { it.nodeId() to it.measure(Constraints()) }
        val sizes = measured.mapValues { it.value.size }
        graphModel.nodeSizes(sizes)
        layout(constraints.maxWidth, constraints.maxHeight) {
            measured.forEach { item ->
                positions[item.key]?.let { position ->
                    item.value.placeRelative(position)
                }
            }
        }
    }
}


private val Measured.size: IntSize get() = IntSize(measuredWidth, measuredHeight)
private fun Offset.toIntOffset(): IntOffset = IntOffset(x.toInt(), y.toInt())

private class Graph<T>(private val edges: List<Edge<T>>, horizontalPadding: Dp, verticalPadding: Dp) {
    private var sizes: Map<T, IntSize> = emptyMap()
    private var nodeAreas: MutableMap<T, IntRect> = mutableMapOf()

    private val _positions = MutableStateFlow<Map<T, IntOffset>>(emptyMap())
    val positions: StateFlow<Map<T, IntOffset>> = _positions

//    val city: StateFlow<String>
//        field = MutableStateFlow("")


    private fun updatePositions() {
        _positions.value = nodeAreas.mapValues { v -> v.value.topLeft }
    }

    private val horizontalPadding = horizontalPadding.value.toInt()
    private val verticalPadding = verticalPadding.value.toInt()

    fun nodeSizes(newSizes: Map<T, IntSize>) {
        val needLayout = sizes.isEmpty()
        this.sizes = newSizes
        if (needLayout) {
            doLayout()
        }
    }

    fun edgeArrowsToDraw(): List<Arrow> {
        return edges.map { edge ->
            val startRect = findNode(edge, edge.start)
            val endRect = findNode(edge, edge.end)
            val start = startRect.center.toOffset()
            val end = endRect.center.toOffset()

            if (startRect.overlaps(endRect)) {
                Arrow(start, end)
            } else {
                Arrow(startRect.intersectionFrom(end), endRect.intersectionFrom(start))
            }
        }
    }

    private fun findNode(edge: Edge<T>, id: T) =
        nodeAreas[id] ?: throw IllegalArgumentException("No node with id '$id' found for '$edge'")

    private var doDrag: (Offset) -> Unit = { _ -> }

    fun dragStart(position: Offset) {
        val dragNode = nodeAt(position)
        doDrag = if (dragNode == null) this::moveAll else { o -> moveNode(dragNode, o) }
    }

    private fun nodeAt(position: Offset) =
        nodeAreas.filter { it.value.contains(position.toIntOffset()) }.keys.firstOrNull()

    fun onDrag(dragAmount: Offset) {
        doDrag(dragAmount)
        updatePositions()
    }

    private fun moveAll(amount: Offset) {
        nodeAreas.keys.forEach { moveNode(it, amount) }
    }

    private fun moveNode(node: T, amount: Offset) {
        nodeAreas[node]?.let { nodeAreas[node] = it.translate(amount.toIntOffset()) }
    }

    private fun doLayout() {
        val layers = splitIntoLayers(sizes.keys, edges)
        val widestLayer = layers.maxOfOrNull { it.measuredWidth } ?: 0

        var y = 0
        layers.forEach { layer ->
            var x = (widestLayer - layer.measuredWidth) / 2
            layer.nodes.forEach { node ->
                sizes[node]?.let { size ->
                    nodeAreas[node] = IntRect(IntOffset(x, y), size)
                    x += horizontalPadding + (sizes[node]?.width ?: 0)
                }
            }
            y += verticalPadding + layer.measuredHeight
        }
        updatePositions()
    }

    private class Layer<T>(val nodes: Set<T>, sizes: Map<T, IntSize>, horizontalPadding: Int) {
        val measuredWidth: Int = nodes.sumOf { sizes[it]?.width ?: 0 } + (nodes.size - 1) * horizontalPadding
        val measuredHeight: Int = (nodes.maxOfOrNull { sizes[it]?.height ?: 0 } ?: 0)
    }

    private fun splitIntoLayers(nodes: Collection<T>, edges: List<Edge<T>>): List<Layer<T>> =
        findLayers(nodes, edges).map { Layer(it, sizes, horizontalPadding) }
}

fun <T> findLayers(
    nodes: Collection<T>,
    edges: List<Edge<T>>
): List<Set<T>> {
    val layers = mutableListOf<Set<T>>()
    var currentLayer = nodes.toSet()
    do {
        val nextLayer = findNextLayer(currentLayer, edges)
        layers.add(currentLayer - nextLayer)
        currentLayer = nextLayer
    } while (currentLayer.isNotEmpty())
    return layers
}

private fun <T> findNextLayer(nodes: Collection<T>, edges: List<Edge<T>>): Set<T> {
    fun edgesToConsider(nodesToConsider: Collection<T>): List<Edge<T>> {
        return edges.filter { it.start != it.end }
            .filter { nodesToConsider.contains(it.start) && nodesToConsider.contains(it.end) }
    }

    val nodesToConsider = nodes.toMutableSet()
    val next = mutableSetOf<T>()
    // leaf nodes
    do {
        val edgesWithin = edgesToConsider(nodesToConsider)
        val start = edgesWithin.map { it.start }.toSet() - edgesWithin.map { it.end }.toSet()
        next.addAll(start)
        nodesToConsider.removeAll(next)
    } while (start.isNotEmpty())

    // cycles
    edgesToConsider(nodesToConsider).forEach { edge ->
        if (nodesToConsider.contains(edge.end) && !next.contains(edge.end)) {
            next.add(edge.start)
        }
    }
    return next
}

