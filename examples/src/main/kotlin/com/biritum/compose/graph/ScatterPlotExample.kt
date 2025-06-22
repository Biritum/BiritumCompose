package com.biritum.compose.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun ScatterPlotExample() {
    Graph(
        Modifier.fillMaxSize().background(Color.LightGray),
        listOf(
            ScatterPlot(
                Color.Blue, listOf(
                    Offset(0f, 0f),
                    Offset(1f, 1f),
                    Offset(2f, 0.5f),
                    Offset(3f, -0.2f),
                    Offset(5f, 0.2f),
                    Offset(0.1f, 0.3f),
                    Offset(1.4f, 1.2f),
                    Offset(2.7f, 0.5f),
                    Offset(3.8f, -0.2f),
                    Offset(5.8f, 1.2f),
                )
            )
        ),
        0f, -1.5f, 7f, 1.5f
    )
}

fun main() = application {
    MaterialTheme {
        Window(onCloseRequest = ::exitApplication) {
            ScatterPlotExample()
        }
    }
}