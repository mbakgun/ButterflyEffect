package com.mbakgun.butterfly.effect

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.mbakgun.butterfly.effect.ui.ButterflyEffectGame
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        ButterflyEffectGame()
    }
}