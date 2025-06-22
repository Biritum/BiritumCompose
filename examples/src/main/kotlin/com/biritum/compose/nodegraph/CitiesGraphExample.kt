package com.biritum.compose.nodegraph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.biritum.compose.examples.generated.resources.Res
import com.biritum.compose.examples.generated.resources.city
import com.biritum.compose.nodegraph.NodeGraphScope.Companion.nodeId
import org.jetbrains.compose.resources.imageResource


data class City(val name: String, val continent: String)

val cities = listOf(
    City("London", "Europe"),
    City("New York", "North America"),
    City("Chicago", "North America"),
    City("Stockholm", "Europe"),
    City("Oslo", "Europe"),
)

val routes = listOf(
    Edge(cities[1], cities[0]),
    Edge(cities[0], cities[2]),
    Edge(cities[2], cities[1]),
    Edge(cities[3], cities[0]),
    Edge(cities[4], cities[3]),
)

@Composable
@Preview
fun CityNode() {
    CityNode(City("Berlin", "Europe"), Modifier)
}

@Composable
fun CityNode(node: City, modifier: Modifier) {
    val icon = imageResource(Res.drawable.city)

    Row(modifier.border(width = 1.dp, color = Color.Black).background(Color.Green).padding(2.dp)) {
        Canvas(modifier = Modifier.size(32.dp)) {
            drawImage(image = icon, dstSize = IntSize(size.width.toInt(), size.height.toInt()))
        }
        Column {
            Text(node.name, Modifier)
            Text(node.continent, Modifier)
        }
    }
}

@Composable
fun App() {
    NodeGraph(
        routes,
        modifier = Modifier.background(Color.White),
        verticalLayerPadding = 20.dp,
        horizontalLayerPadding = 10.dp
    ) {
        cities.forEach {
            CityNode(it, Modifier.nodeId(it))
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            App()
        }
    }
}
