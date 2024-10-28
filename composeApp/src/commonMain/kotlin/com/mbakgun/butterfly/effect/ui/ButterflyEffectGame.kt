package com.mbakgun.butterfly.effect.ui

import GameScreen
import HappyEndScreen
import MainMenuScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import com.mbakgun.butterfly.effect.game.GameEngine
import com.mbakgun.butterfly.effect.model.GameState
import com.mbakgun.butterfly.effect.ui.screens.GameOverScreen

@Composable
fun ButterflyEffectGame() {
    val gameState = remember { GameState() }
    val gameEngine = remember { GameEngine(gameState) }
    val uriHandler = LocalUriHandler.current

    when (gameState.currentScreen) {
        is GameState.Screen.MainMenu -> MainMenuScreen(gameState, gameEngine) {
            uriHandler.openUri("https://github.com/mbakgun/ButterflyEffect")
        }

        is GameState.Screen.Playing -> GameScreen(gameState, gameEngine)
        is GameState.Screen.HappyEnd -> HappyEndScreen(gameState::toMainMenu)
        is GameState.Screen.GameOver -> GameOverScreen(gameState, gameState::toMainMenu)
    }
}
