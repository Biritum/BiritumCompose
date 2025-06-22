package com.biritum.compose.piechart

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.biritum.compose.color.random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

val model = AppViewModel()

class ValueItem internal constructor(startValue: Float, val color: Color, val onValueChanged: () -> Unit) {
    private val _value = MutableStateFlow(startValue)
    val value = _value.asStateFlow()

    fun update(v: Float) {
        _value.value = v
        onValueChanged()
    }
}

class AppViewModel {
    private val _items =
        MutableStateFlow(listOf(4f, 3f, 2f, 1f).map { ValueItem(it, Color.random(), this::onValueChange) })
    val items = _items.asStateFlow()

    private val _slices = MutableStateFlow(toSlices())
    val slices = _slices.asStateFlow()

    private fun onValueChange() {
        _slices.value = toSlices()
    }

    private fun toSlices(): List<PieChartSlice<Float>> =
        items.value.map { PieChartSlice(it.value.value, it.color) }

    fun addSlice() {
        _items.value = items.value.toMutableList().also { it.add(ValueItem(1f, Color.random(), this::onValueChange)) }
        onValueChange()
    }

    fun removeSlice() {
        _items.value = items.value.take(items.value.size - 1)
        onValueChange()
    }
}

@Composable
fun SlideControls(items: List<ValueItem>) {
    items.forEach { item ->
        val value: Float by item.value.collectAsState()
        Slider(
            modifier = Modifier.height(25.dp),
            value = value,
            onValueChange = item::update,
            valueRange = 1f..10f,
            colors = SliderDefaults.colors(
                thumbColor = item.color,
                activeTrackColor = item.color
            )
        )
    }
}

@Composable
fun Legend(items: List<ValueItem>) {
    Column {
        items.forEachIndexed { index, slice ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier.size(10.dp)
                        .align(Alignment.CenterVertically)
                        .background(slice.color)
                )
                val value by slice.value.collectAsState()
                Text("Item ${index + 1} : $value")
            }
        }
    }
}

@Composable
@Preview
fun PieChartExample() {
    Column(modifier = Modifier.padding(30.dp)) {
        val items by model.items.collectAsState()
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { model.addSlice() }, enabled = items.size < 6) {
                Text("Add")
            }
            Button(onClick = { model.removeSlice() }, enabled = items.size > 1) {
                Text("Remove")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val slices by model.slices.collectAsState()
            PieChart(slices, Modifier.size(200.dp, 200.dp))
            Legend(items)
        }
        SlideControls(items)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            PieChartExample()
        }
    }
}