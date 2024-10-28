package com.mbakgun.butterfly.effect.ui.screens

import KottieAnimation
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mbakgun.butterfly.effect.model.GameState
import com.mbakgun.butterfly.effect.resources.Res
import com.mbakgun.butterfly.effect.resources.worldmap
import kotlinx.coroutines.delay
import kottieComposition.KottieCompositionSpec
import kottieComposition.animateKottieCompositionAsState
import kottieComposition.rememberKottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameOverScreen(gameState: GameState, callback: () -> Unit) {
    var animation by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        animation = Res.readBytes("drawable/rip.json").decodeToString()
    }

    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(animation)
    )

    val animationState by animateKottieCompositionAsState(
        composition = composition,
        isPlaying = true
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            alpha = 0.25f,
            colorFilter = tint(Color.Red),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(Res.drawable.worldmap),
            contentDescription = null
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            var displayedText by remember { mutableStateOf("") }
            val fullText = "You have failed to save the world, the chaos has consumed everything.."

            LaunchedEffect(fullText) {
                fullText.forEachIndexed { index, _ ->
                    displayedText = fullText.substring(0, index + 1)
                    delay(30)
                }
            }

            Text(
                text = displayedText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Game Over",
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                buildAnnotatedString {
                    append("Produced Weapon: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${gameState.gameData.weaponsProduced}")
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Chaos Level: 100%",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(32.dp))

            Button(onClick = callback::invoke) { Text("Restart") }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center,
            ) {
                KottieAnimation(
                    composition = composition,
                    progress = { animationState.progress },
                )
            }
        }
    }
}