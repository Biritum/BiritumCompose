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
fun XyPlotGraph() {
    Graph(
        Modifier.fillMaxSize().background(Color.LightGray),
        listOf(
            XyPlotLine(
                Color.Red, listOf(
                    Offset(0f, 0f),
                    Offset(10f, 0f),
                    Offset(20f, 10f),
                    Offset(30f, 10f),
                    Offset(40f, 5f),
                    Offset(50f, 2f),
                )
            ),
            XyPlotLine(
                Color.Blue, listOf(
                    Offset(0f, 10f),
                    Offset(10f, 5f),
                    Offset(20f, 2f),
                    Offset(30f, 1f),
                    Offset(40f, 2f),
                    Offset(50f, 5f),
                )
            )
        ),
        0f, -1f, 50f, 12f
    )
}

fun main() = application {
    MaterialTheme {
        Window(onCloseRequest = ::exitApplication) {
            XyPlotGraph()
        }
    }
}