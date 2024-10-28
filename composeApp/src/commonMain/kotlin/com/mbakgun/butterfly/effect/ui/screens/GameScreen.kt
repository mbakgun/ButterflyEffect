import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mbakgun.butterfly.effect.game.GameEngine
import com.mbakgun.butterfly.effect.model.GameState
import com.mbakgun.butterfly.effect.model.Vector2D
import com.mbakgun.butterfly.effect.resources.Res
import com.mbakgun.butterfly.effect.resources.house
import com.mbakgun.butterfly.effect.resources.worldmap
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kottieComposition.KottieCompositionSpec
import kottieComposition.animateKottieCompositionAsState
import kottieComposition.rememberKottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun GameScreen(gameState: GameState, gameEngine: GameEngine) {
    val showDialog = remember { mutableStateOf(false) }
    val showHouseImage = remember { mutableStateOf(false) }
    val isExploding by derivedStateOf { gameState.gameData.chaosLevel > 99 }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val x = offset.x
                            val y = offset.y

                            gameState.gameData = gameState.gameData.copy(
                                userLocation = Vector2D(x, y)
                            )

                            val heightPercentage = y / size.height
                            showHouseImage.value = heightPercentage > 0.14f && heightPercentage < 0.81f
                        }
                    },
                contentScale = ContentScale.FillWidth,
                painter = painterResource(Res.drawable.worldmap),
                contentDescription = null
            )

            if (showHouseImage.value) {
                ExplodingHouseImage(
                    gameState = gameState,
                    isExploding = isExploding,
                ) {
                    gameState.gameData = gameState.gameData.copy(
                        houseExploded = true
                    )
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            gameState.gameData.butterflies.forEach { butterfly ->
                drawCircle(
                    color = Color.Red,
                    radius = 10f,
                    center = Offset(
                        butterfly.position.x * size.width,
                        butterfly.position.y * size.height
                    )
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Produced Weapon: ${gameState.gameData.weaponsProduced}",
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Chaos Level: ${minOf(100, gameState.gameData.chaosLevel.toInt())}%",
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Bottom
        ) {
            if (showHouseImage.value.not()) {
                Text(
                    "Please select a location to build a house",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                enabled = showHouseImage.value,
                onClick = {
                    if (gameState.dialogShown.not() && gameState.gameData.weaponsProduced == 1) {
                        showDialog.value = true
                    } else {
                        gameEngine.produceWeapon()
                    }
                }
            ) {
                Text(if (gameState.gameData.weaponsProduced == 0) "Produce Weapon" else "Produce More Weapon")
            }
        }


        if (showDialog.value) {
            gameEngine.pauseGame()
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Warning") },
                text = { Text("More you produce weapons, more butterflies will spawn and chaos level will increase faster :/") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            gameEngine.resumeGame()
                            gameEngine.produceWeapon()
                            showDialog.value = false
                            gameState.dialogShown = true
                        }
                    ) {
                        Text("Acknowledged")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            gameEngine.resumeGame()
                            showDialog.value = false
                        }
                    ) {
                        Text("No !")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ExplodingHouseImage(
    gameState: GameState,
    isExploding: Boolean,
    finishedListener: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isExploding) 4f else 1f,
        animationSpec = tween(1000),
        finishedListener = { finishedListener.invoke() }
    )

    val alpha by animateFloatAsState(
        targetValue = if (isExploding) 0f else 1f,
        animationSpec = tween(1000)
    )

    var animation by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        animation = Res.readBytes("drawable/blast.json").decodeToString()
    }

    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(animation)
    )

    val animationState by animateKottieCompositionAsState(
        composition = composition,
        isPlaying = isExploding
    )

    Box {
        Image(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = gameState.gameData.userLocation.x.roundToInt() - 15.dp.toPx().roundToInt(),
                        y = gameState.gameData.userLocation.y.roundToInt() - 15.dp.toPx().roundToInt()
                    )
                }
                .size(35.dp)
                .scale(scale)
                .alpha(alpha),
            painter = painterResource(Res.drawable.house),
            contentDescription = null
        )

        KottieAnimation(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = gameState.gameData.userLocation.x.roundToInt() / 2 - 35.dp.toPx().roundToInt(),
                        y = gameState.gameData.userLocation.y.roundToInt() / 2 - 35.dp.toPx().roundToInt()
                    )
                },
            composition = composition,
            progress = { animationState.progress },
        )

        if (isExploding) {
            ExplosionParticles(
                center = Offset(
                    x = gameState.gameData.userLocation.x,
                    y = gameState.gameData.userLocation.y
                )
            )
        }
    }
}

@Composable
fun ExplosionParticles(center: Offset) {
    val particles = 12

    repeat(particles) { index ->
        val angle = (360f / particles) * index
        val distance by animateFloatAsState(
            targetValue = 100f,
            animationSpec = tween(500, easing = LinearEasing)
        )

        val x = center.x + (distance * cos(3.14 * angle / 180)).toFloat()
        val y = center.y + (distance * sin(3.14 * angle / 180)).toFloat()

        val particleAlpha by animateFloatAsState(
            targetValue = 0f,
            animationSpec = tween(500)
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                .size(4.dp)
                .alpha(particleAlpha)
                .background(Color.Red, CircleShape)
        )
    }
}