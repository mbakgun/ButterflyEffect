package com.mbakgun.butterfly.effect.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Stable
class GameState {
    var currentScreen by mutableStateOf<Screen>(Screen.MainMenu)
    var gameData by mutableStateOf(GameData())
    var dialogShown by mutableStateOf(false)
    var storyTold by mutableStateOf(false)

    fun resetGameStats() {
        gameData = GameData()
        dialogShown = false
    }

    fun toMainMenu() {
        currentScreen = Screen.MainMenu
    }

    sealed class Screen {
        data object MainMenu : Screen()
        data object Playing : Screen()
        data object GameOver : Screen()
        data object HappyEnd : Screen()
    }
}

data class GameData(
    val butterflies: List<Butterfly> = emptyList(),
    val weaponsProduced: Int = 0,
    val chaosLevel: Float = 0f,
    val userLocation: Vector2D = Vector2D(0f, 0f),
    val houseExploded: Boolean = false
)

data class Vector2D(val x: Float, val y: Float)

data class Butterfly @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val position: Vector2D,
    val event: String,
    val path: List<Vector2D> = emptyList()
)
