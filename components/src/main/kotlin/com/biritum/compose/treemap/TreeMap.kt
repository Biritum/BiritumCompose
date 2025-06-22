package com.biritum.compose.treemap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import kotlin.math.max
import kotlin.math.min

@Composable
fun TreeMap(modifier: Modifier = Modifier, content: @Composable (TreeMapScope.() -> Unit)) {
    Layout(content = { TreeMapScope.content() }, modifier) { measurable, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {

            val positions: Map<Measurable, IntRect> = position(
                measurable.sortedByDescending { it.treeMapSize() },
                IntRect(0, 0, constraints.maxWidth, constraints.maxHeight)
            )

            measurable.forEach { m ->
                positions[m]?.let {
                    m.measure(constraints.copy(minWidth = it.width, minHeight = it.height))
                        .place(it.left, it.top)
                }
            }
        }
    }
}
private data class TreeMapSize(val size: Float) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@TreeMapSize
}

private fun Measurable.treeMapSize() = (parentData as? TreeMapSize)?.size ?: 1f

interface TreeMapScope {
    fun Modifier.treeMapSize(size: Float) = this.then(TreeMapSize(size))

    companion object : TreeMapScope
}

private fun List<Measurable>.treeMapSize() = map { it.treeMapSize() }.sum()

private fun position(measurable: List<Measurable>, bounds: IntRect): Map<Measurable, IntRect> {
    return when (measurable.size) {
        0 -> emptyMap()
        1 -> mapOf(measurable[0] to bounds)
        else -> {
            val splitIndex = splitIndex(measurable, bounds)
            val part1 = measurable.take(splitIndex)
            val part2 = measurable.drop(splitIndex)

            val cutoff = part1.treeMapSize() / measurable.treeMapSize()
            val (bounds1, bounds2) = bounds.split(cutoff)

            position(part1, bounds1).plus(position(part2, bounds2))
        }
    }
}

private fun IntRect.split(cutoff: Float): Pair<IntRect, IntRect> = if (width >= height) {
    splitHorizontal(cutoff)
} else {
    splitVertical(cutoff)
}

private fun IntRect.splitVertical(cutoff: Float): Pair<IntRect, IntRect> {
    val h1 = (height * cutoff).toInt()
    return Pair(
        IntRect(left, top, right, top + h1),
        IntRect(left, top + h1, right, bottom)
    )
}

private fun IntRect.splitHorizontal(cutoff: Float): Pair<IntRect, IntRect> {
    val w1 = (width * cutoff).toInt()
    return Pair(
        IntRect(left, top, left + w1, bottom),
        IntRect(left + w1, top, right, bottom)
    )
}

private fun splitIndex(measurable: List<Measurable>, bounds: IntRect): Int {
    val cutoffRatio = min(0.5f, min(bounds.width, bounds.height).toFloat() / max(bounds.width, bounds.height))
    val total = measurable.treeMapSize()
    (1 until measurable.size).forEach {
        if (measurable.take(it).treeMapSize() / total >= cutoffRatio) {
            return it
        }
    }
    return measurable.size - 1
}