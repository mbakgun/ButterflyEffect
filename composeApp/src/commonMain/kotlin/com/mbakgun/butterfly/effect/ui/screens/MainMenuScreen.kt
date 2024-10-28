import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mbakgun.butterfly.effect.game.GameEngine
import com.mbakgun.butterfly.effect.model.GameState
import com.mbakgun.butterfly.effect.resources.Res
import com.mbakgun.butterfly.effect.resources.teaser
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainMenuScreen(
    gameState: GameState,
    gameEngine: GameEngine,
    onGitHubButtonClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize().graphicsLayer {
                scaleX = -1f
            },
            contentScale = ContentScale.FillWidth,
            painter = painterResource(Res.drawable.teaser),
            contentDescription = null
        )

        if (!gameState.storyTold) {
            var showDialog by remember { mutableStateOf(true) }
            var isTextFullyLoaded by remember { mutableStateOf(false) }

            if (showDialog) {
                Dialog(
                    onDismissRequest = {
                        if (isTextFullyLoaded) {
                            showDialog = false
                            gameState.storyTold = true
                        }
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 120.dp)
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.large,
                            color = Color.White
                        ) {
                            Box(
                                modifier = Modifier.padding(
                                    start = 24.dp,
                                    top = 24.dp,
                                    end = 40.dp,
                                    bottom = 24.dp
                                )
                            ) {
                                val storyText = remember {
                                    buildAnnotatedString {
                                        append("You are a worker in a tank factory producing weapons. In these days when the world is being dragged into dark times, there are decisions you can make individually.\n\n• ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Resign")
                                        }
                                        append(" and help make the world a better place.\n• ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Continue Working")
                                        }
                                        append(", produce weapons that will kill more people, and let chaos prevail.\n\nRemember, people use the weapons they produce against themselves.\nTell me, young person, who can guarantee that a weapon won't be used against you once it's produced?")
                                    }
                                }

                                var visibleLength by remember { mutableStateOf(0) }

                                LaunchedEffect(Unit) {
                                    while (visibleLength < storyText.length) {
                                        visibleLength++
                                        delay(25)
                                    }
                                    isTextFullyLoaded = true
                                }

                                Text(
                                    text = storyText.subSequence(0, visibleLength),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Left,
                                    color = Color.Black
                                )
                            }
                        }

                        if (isTextFullyLoaded) {
                            IconButton(
                                onClick = {
                                    showDialog = false
                                    gameState.storyTold = true
                                },
                                modifier = Modifier
                                    .padding(end = 24.dp, top = 24.dp)
                                    .align(Alignment.TopEnd)
                                    .background(
                                        Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .size(36.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.size(16.dp),
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopEnd).padding(
                top = 64.dp,
                end = 24.dp
            ),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onGitHubButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("GitHub Repo", color = Color.White)
            }
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = "Butterfly Effect",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.weight(0.5f))

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val screenWidth = maxWidth
                val horizontalPadding = screenWidth * 0.06f
                val spacing = screenWidth * 0.11f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = horizontalPadding, bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            gameState.currentScreen = GameState.Screen.HappyEnd
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Resign", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    }

                    Button(
                        onClick = gameEngine::startGame,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            "Continue Working",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}