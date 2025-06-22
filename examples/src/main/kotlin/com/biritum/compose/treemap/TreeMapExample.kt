package com.biritum.compose.treemap

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.biritum.compose.color.random

@Composable
@Preview
fun App() {
    val items = listOf("hi", "hello", "Greetings", "yo", "take care", "KISS", "Good bye winter.")
    TreeMap(Modifier.fillMaxSize()) {
        items.forEach {
            Text(it, Modifier.background(Color.random()).treeMapSize(it.length.toFloat()))
        }
    }
}

fun main() = application {
    MaterialTheme {
        Window(onCloseRequest = ::exitApplication) {
            App()
        }
    }
}
