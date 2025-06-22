package com.biritum.compose.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
@Preview
fun EquationLineGraph() {
    Graph(
        Modifier.fillMaxSize().background(Color.LightGray),
        listOf(
            EquationLine(Color.Blue) { sin(it) },
            EquationLine(Color.Red) { cos(it * 2) / 2 },
        ),
        0f, -1.2f, 3f * PI.toFloat(), 1.2f
    )
}

fun main() = application {
    MaterialTheme {
        Window(onCloseRequest = ::exitApplication) {
            ScatterPlotExample()
        }
    }
}