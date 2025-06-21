package com.biritum.compose.nodegraph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.biritum.compose.nodegraph.NodeGraphScope.Companion.nodeId

val names = listOf(
    "Allan",
    "Bert",
    "Charles",
    "David",
    "Eric",
    "Frank",
    "Gary"
)

val relations = listOf(
    Edge(names[0], names[2]),
    Edge(names[2], names[1]),
    Edge(names[3], names[1]),
    Edge(names[4], names[0]),
    Edge(names[6], names[5]),
    Edge(names[6], names[0]),
)

@Composable
@Preview
fun ExampleNameNode() {
    NameNode("Sue", Modifier)
}

@Composable
fun NameNode(name: String, modifier: Modifier) {
    Text(name, modifier.border(width = 4.dp, color = Color.Blue).background(color = Color.LightGray).padding(8.dp))
}

@Composable
fun NameGraph() {
    NodeGraph(relations) {
        names.forEach {
            NameNode(it, Modifier.nodeId(it))
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            NameGraph()
        }
    }
}
