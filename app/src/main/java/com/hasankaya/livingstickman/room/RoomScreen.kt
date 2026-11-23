
package com.hasankaya.livingstickman.room

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun RoomScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    val layout = remember {
        RoomLayout()
    }

    val roomState = remember(layout) {
        RoomState(layout)
    }

    val lighting = remember {
        RoomLighting()
    }

    val snapshot by roomState.state.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF11121B)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            RoomHeader(
                characterAction = snapshot.character.action,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF20202B),
                        shape = MaterialTheme.shapes.large
                    )
            ) {

                RoomCanvas(
                    layout = layout,
                    lighting = lighting,
                    snapshot = snapshot,
                    modifier = Modifier.fillMaxSize()
                )

                CharacterCanvas(
                    layout = layout,
                    character = snapshot.character,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "STICKMAN HAREKETLERİ",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            CharacterControls(
                onIdle = {
                    roomState.stopCharacter()
                    roomState.setCharacterAction(
                        CharacterRoomAction.IDLE
                    )
                },
                onSleep = {
                    roomState.setCharacterAction(
                        CharacterRoomAction.SLEEPING
                    )
                },
                onSit = {
                    roomState.setCharacterAction(
                        CharacterRoomAction.SITTING
                    )
                },
                onComputer = {
                    roomState.setCharacterAction(
                        CharacterRoomAction.USING_COMPUTER
                    )
                },
                onWatchTv = {
                    roomState.setCharacterAction(
                        CharacterRoomAction.WATCHING_TV
                    )
                },
                onPortal = {
                    roomState.setCharacterAction(
                        CharacterRoomAction.APPROACHING_PORTAL
                    )
                }
            )
        }
    }
}

@Composable
private fun RoomHeader(
    characterAction: CharacterRoomAction,
    onBack: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(
                text = "LIVING STICKMAN",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Karakter: ${characterAction.name}",
                color = Color(0xFFB9B9C8),
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (onBack != null) {
            Button(
                onClick = onBack
            ) {
                Text("Geri")
            }
        }
    }
}

@Composable
private fun CharacterControls(
    onIdle: () -> Unit,
    onSleep: () -> Unit,
    onSit: () -> Unit,
    onComputer: () -> Unit,
    onWatchTv: () -> Unit,
    onPortal: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Button(onClick = onIdle) {
            Text("Bekle")
        }

        Button(onClick = onSleep) {
            Text("Uyu")
        }

        Button(onClick = onSit) {
            Text("Otur")
        }

        Button(onClick = onComputer) {
            Text("Bilgisayar")
        }

        Button(onClick = onWatchTv) {
            Text("TV izle")
        }

        Button(onClick = onPortal) {
            Text("Portal")
        }
    }
}

@Composable
private fun CharacterCanvas(
    layout: RoomLayout,
    character: CharacterRoomState,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        val scale = min(
            size.width / layout.roomWidth,
            size.height / layout.roomHeight
        )

        val offsetX =
            (size.width - layout.roomWidth * scale) / 2f

        val offsetY =
            (size.height - layout.roomHeight * scale) / 2f

        fun screenPoint(x: Float, y: Float): Offset {
            return Offset(
                offsetX + x * scale,
                offsetY + y * scale
            )
        }

        val characterX = character.position.x
        val characterY = character.position.y

        drawStickman(
            center = screenPoint(characterX, characterY),
            scale = scale,
            facingRight = character.facingRight,
            action = character.action
        )
    }
}

private fun DrawScope.drawStickman(
    center: Offset,
    scale: Float,
    facingRight: Boolean,
    action: CharacterRoomAction
) {
    val black = Color(0xFF101014)
    val red = Color(0xFFB51F32)
    val darkRed = Color(0xFF721526)
    val gold = Color(0xFFFFD45A)
    val white = Color.White

    val direction = if (facingRight) 1f else -1f

    fun point(x: Float, y: Float): Offset {
        return Offset(
            center.x + x * scale * direction,
            center.y + y * scale
        )
    }

    val lineWidth = 9f * scale

    // Pelerin
    val cape = Path().apply {
        moveTo(point(-9f, -55f).x, point(-9f, -55f).y)
        lineTo(point(-34f, -43f).x, point(-34f, -43f).y)
        lineTo(point(-43f, 8f).x, point(-43f, 8f).y)
        lineTo(point(-24f, 18f).x, point(-24f, 18f).y)
        lineTo(point(0f, -28f).x, point(0f, -28f).y)
        close()
    }

    drawPath(
        path = cape,
        color = red
    )

    // Pelerin iç gölgesi
    drawLine(
        color = darkRed,
        start = point(-29f, -35f),
        end = point(-35f, 3f),
        strokeWidth = 3f * scale,
        cap = StrokeCap.Round
    )

    // Bacaklar
    when (action) {
        CharacterRoomAction.SITTING,
        CharacterRoomAction.SLEEPING -> {
            drawLine(
                color = black,
                start = point(0f, -20f),
                end = point(25f, 0f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = black,
                start = point(25f, 0f),
                end = point(48f, 0f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = black,
                start = point(0f, -20f),
                end = point(-18f, 0f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = black,
                start = point(-18f, 0f),
                end = point(8f, 0f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )
        }

        else -> {
            drawLine(
                color = black,
                start = point(-4f, -20f),
                end = point(-17f, 13f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = black,
                start = point(-17f, 13f),
                end = point(-25f, 18f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = black,
                start = point(4f, -20f),
                end = point(17f, 13f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = black,
                start = point(17f, 13f),
                end = point(25f, 18f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )
        }
    }

    // Gövde
    drawLine(
        color = black,
        start = point(0f, -55f),
        end = point(0f, -20f),
        strokeWidth = 11f * scale,
        cap = StrokeCap.Round
    )

    // Kollar
    if (action == CharacterRoomAction.USING_COMPUTER) {

        drawLine(
            color = black,
            start = point(0f, -48f),
            end = point(22f, -30f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = black,
            start = point(22f, -30f),
            end = point(38f, -27f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = black,
            start = point(0f, -48f),
            end = point(-22f, -30f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = black,
            start = point(-22f, -30f),
            end = point(-38f, -27f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

    } else {

        drawLine(
            color = black,
            start = point(0f, -48f),
            end = point(-23f, -28f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = black,
            start = point(-23f, -28f),
            end = point(-25f, -8f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = black,
            start = point(0f, -48f),
            end = point(23f, -28f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = black,
            start = point(23f, -28f),
            end = point(25f, -8f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )
    }

    // Kafa
    drawCircle(
        color = black,
        radius = 17f * scale,
        center = point(0f, -75f)
    )

    // Yüz
    drawCircle(
        color = white,
        radius = 2f * scale,
        center = point(6f, -78f)
    )

    // Taç
    val crown = Path().apply {
        moveTo(point(-13f, -90f).x, point(-13f, -90f).y)
        lineTo(point(-13f, -105f).x, point(-13f, -105f).y)
        lineTo(point(-5f, -97f).x, point(-5f, -97f).y)
        lineTo(point(0f, -108f).x, point(0f, -108f).y)
        lineTo(point(6f, -97f).x, point(6f, -97f).y)
        lineTo(point(13f, -105f).x, point(13f, -105f).y)
        lineTo(point(13f, -90f).x, point(13f, -90f).y)
        close()
    }

    drawPath(
        path = crown,
        color = gold
    )

    drawLine(
        color = gold,
        start = point(-13f, -90f),
        end = point(13f, -90f),
        strokeWidth = 4f * scale,
        cap = StrokeCap.Round
    )
}
