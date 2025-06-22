package com.biritum.compose.color

import androidx.compose.ui.graphics.Color
import kotlin.random.Random


fun Color.Companion.random() = Color( (Random.nextLong(0xFFFFFF) + 0xFF000000).toInt())
