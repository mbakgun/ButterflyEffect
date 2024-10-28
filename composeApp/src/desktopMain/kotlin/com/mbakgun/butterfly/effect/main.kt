package com.mbakgun.butterfly.effect

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.mbakgun.butterfly.effect.ui.ButterflyEffectGame

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ButterflyEffect",
        state = WindowState(width = 1440.dp, height = 1080.dp),
        resizable = false,
    ) {
        ButterflyEffectGame()
    }
}