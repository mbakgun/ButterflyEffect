package com.mbakgun.butterfly.effect.game

import com.mbakgun.butterfly.effect.model.Butterfly
import com.mbakgun.butterfly.effect.model.GameState
import com.mbakgun.butterfly.effect.model.Vector2D
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameEngine(private val gameState: GameState) {
    private val gameScope = CoroutineScope(Dispatchers.Default + Job())

    fun startGame() {
        gameState.resetGameStats()
        gameState.currentScreen = GameState.Screen.Playing
        gameScope.launch {
            runGameLoop()
        }
    }

    private suspend fun runGameLoop() {
        while (gameState.currentScreen == GameState.Screen.Playing) {
            update()
            delay(16) // ~60 FPS
        }
    }

    private fun update() {
        val weaponsProduced = gameState.gameData.weaponsProduced
        if (weaponsProduced > 0) {
            repeat(weaponsProduced) { spawnButterfly() }
            updateButterflies()
            checkGameOver()
        }
    }

    private fun spawnButterfly() {
        val events = listOf(
            "Social unrest",
            "Economic crisis",
            "Political tension",
            "Environmental disaster",
            "Resource depletion",
            "International sanctions",
            "Technological failures",
            "Labor shortage"
        )

        val x = Random.nextFloat() * 0.98f
        val y = 0.13f + Random.nextFloat() * 0.65f

        val butterfly = Butterfly(
            position = Vector2D(x, y),
            event = events.random()
        )


        if (gameState.gameData.chaosLevel < 100f) {
            gameState.gameData = gameState.gameData.copy(
                butterflies = gameState.gameData.butterflies + butterfly,
                chaosLevel = gameState.gameData.chaosLevel + 0.2f
            )
        }
    }

    private fun updateButterflies() {
        gameState.gameData = gameState.gameData.copy(
            butterflies = gameState.gameData.butterflies.map { butterfly ->
                butterfly.copy(position = getPosition(butterfly))
            }
        )
    }

    private fun getPosition(butterfly: Butterfly): Vector2D = butterfly.position

    private fun checkGameOver() {
        with(gameState.gameData) {
            if (chaosLevel >= 100f && houseExploded) {
                gameScope.coroutineContext.cancelChildren()
                gameState.currentScreen = GameState.Screen.GameOver
            }
        }
    }

    // More you produce weapons, more butterflies will spawn and chaos level will increase faster :/
    fun produceWeapon() {
        gameState.gameData = gameState.gameData.copy(
            weaponsProduced = gameState.gameData.weaponsProduced + 1
        )
    }

    fun pauseGame() {
        gameScope.coroutineContext.cancelChildren()
    }

    fun resumeGame() {
        gameScope.launch {
            runGameLoop()
        }
    }
}