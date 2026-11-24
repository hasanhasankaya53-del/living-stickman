
package com.hasankaya.livingstickman.room

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.max
import kotlin.math.min

// ======================================================
// 1. ODA ÇİZİM MOTORU
// ======================================================

class RoomRenderer(
    private val layout: RoomLayout,
    private val lighting: RoomLighting
) {

    // --------------------------------------------------
    // ANA ODA ÇİZİMİ
    // --------------------------------------------------

    fun drawRoom(
        drawScope: DrawScope,
        snapshot: RoomSnapshot
    ) {

        with(drawScope) {

            val scaleX =
                size.width / layout.roomWidth

            val scaleY =
                size.height / layout.roomHeight

            val scale =
                min(scaleX, scaleY)

            val offsetX =
                (size.width - layout.roomWidth * scale) / 2f

            val offsetY =
                (size.height - layout.roomHeight * scale) / 2f

            val background =
                snapshotBackground(snapshot)

            drawRect(
                color = background,
                size = size
            )

            drawContext.transform.translate(
                left = offsetX,
                top = offsetY
            )

            drawContext.transform.scale(
                scaleX = scale,
                scaleY = scale,
                pivot = Offset.Zero
            )

            drawWalls(snapshot)
            drawFloor(snapshot)
            drawWindow(snapshot)
            drawDoor(snapshot)
            drawBookshelf()
            drawLamp(snapshot)
            drawTelevision(snapshot)
            drawCarpet()
            drawBed()
            drawSofa()
            drawComputerDesk(snapshot)
            drawPlant()
            drawPortal(snapshot)
            drawWallDecorations()

            drawContext.transform.scale(
                scaleX = 1f / scale,
                scaleY = 1f / scale,
                pivot = Offset.Zero
            )

            drawContext.transform.translate(
                left = -offsetX,
                top = -offsetY
            )
        }
    }

    // ==================================================
    // 2. ARKA PLAN
    // ==================================================

    private fun snapshotBackground(
        snapshot: RoomSnapshot
    ): Color {

        return when (snapshot.lightState) {

            RoomLightState.DAYLIGHT ->
                Color(0xFFB9D9F2)

            RoomLightState.EVENING ->
                Color(0xFF6C668C)

            RoomLightState.NIGHT ->
                Color(0xFF151B38)

            RoomLightState.DARK ->
                Color(0xFF090B18)

            RoomLightState.CUSTOM ->
                lighting.getCurrentProfile().backgroundColor
        }
    }

    // ==================================================
    // 3. DUVARLAR
    // ==================================================

    private fun DrawScope.drawWalls(
        snapshot: RoomSnapshot
    ) {

        val wallColor =
            lighting.getCurrentProfile().wallColor

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    wallColor,
                    wallColor.copy(alpha = 0.85f)
                ),
                startY = 0f,
                endY = layout.floorTop
            ),
            topLeft = Offset.Zero,
            size = Size(
                layout.roomWidth,
                layout.floorTop
            )
        )

        drawRect(
            color = Color(0xFFB8A99A),
            topLeft = Offset(
                0f,
                layout.floorTop - 8f
            ),
            size = Size(
                layout.roomWidth,
                8f
            )
        )

        // Duvar panel çizgileri.

        for (x in 0..layout.roomWidth.toInt() step 200) {

            drawLine(
                color = Color.Black.copy(alpha = 0.035f),
                start = Offset(x.toFloat(), 0f),
                end = Offset(
                    x.toFloat(),
                    layout.floorTop - 8f
                ),
                strokeWidth = 2f
            )
        }

        // Üst duvar gölgesi.

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = 65f
            ),
            size = Size(
                layout.roomWidth,
                65f
            )
        )
    }

    // ==================================================
    // 4. ZEMİN
    // ==================================================

    private fun DrawScope.drawFloor(
        snapshot: RoomSnapshot
    ) {

        val floorColor =
            lighting.getCurrentProfile().floorColor

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    floorColor,
                    floorColor.copy(alpha = 0.85f)
                ),
                startY = layout.floorTop,
                endY = layout.roomHeight
            ),
            topLeft = Offset(
                0f,
                layout.floorTop
            ),
            size = Size(
                layout.roomWidth,
                layout.floorHeight
            )
        )

        // Ahşap zemin çizgileri.

        for (y in
            layout.floorTop.toInt()
                .rangeTo(layout.roomHeight.toInt())
                .step(45)
        ) {

            drawLine(
                color = Color.Black.copy(alpha = 0.10f),
                start = Offset(0f, y.toFloat()),
                end = Offset(
                    layout.roomWidth,
                    y.toFloat()
                ),
                strokeWidth = 2f
            )
        }

        // Zemin parlaması.

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                startY = layout.floorTop,
                endY = layout.floorTop + 90f
            ),
            topLeft = Offset(
                0f,
                layout.floorTop
            ),
            size = Size(
                layout.roomWidth,
                90f
            )
        )
    }

    // ==================================================
    // 5. PENCERE
    // ==================================================

    private fun DrawScope.drawWindow(
        snapshot: RoomSnapshot
    ) {

        val x = 700f
        val y = 60f
        val width = 330f
        val height = 230f

        drawRoundRect(
            color = Color(0xFF493A32),
            topLeft = Offset(x - 9f, y - 9f),
            size = Size(
                width + 18f,
                height + 18f
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
        )

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF79B9E8),
                    Color(0xFFCEE9FA)
                ),
                startY = y,
                endY = y + height
            ),
            topLeft = Offset(x, y),
            size = Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f)
        )

        // Dışarıdaki ufuk.

        drawRect(
            color = Color(0xFF6B9C70),
            topLeft = Offset(
                x,
                y + height * 0.72f
            ),
            size = Size(
                width,
                height * 0.28f
            )
        )

        // Pencere çerçevesi.

        drawLine(
            color = Color(0xFF493A32),
            start = Offset(
                x + width / 2f,
                y
            ),
            end = Offset(
                x + width / 2f,
                y + height
            ),
            strokeWidth = 9f
        )

        drawLine(
            color = Color(0xFF493A32),
            start = Offset(
                x,
                y + height / 2f
            ),
            end = Offset(
                x + width,
                y + height / 2f
            ),
            strokeWidth = 9f
        )

        // Cam yansıması.

        drawLine(
            color = Color.White.copy(alpha = 0.45f),
            start = Offset(
                x + 25f,
                y + 25f
            ),
            end = Offset(
                x + 95f,
                y + 25f
            ),
            strokeWidth = 4f
        )

        // Perde.

        drawCurtains(x, y, width, height)
    }

    // ==================================================
    // 6. PERDE
    // ==================================================

    private fun DrawScope.drawCurtains(
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {

        val curtainColor =
            Color(0xFF6D3F58)

        drawRect(
            color = curtainColor,
            topLeft = Offset(x - 16f, y - 10f),
            size = Size(20f, height + 25f)
        )

        drawRect(
            color = curtainColor,
            topLeft = Offset(
                x + width - 4f,
                y - 10f
            ),
            size = Size(20f, height + 25f)
        )

        for (i in 0..3) {

            val leftX =
                x - 12f + i * 5f

            val rightX =
                x + width + i * 5f

            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(leftX, y),
                end = Offset(
                    leftX,
                    y + height
                ),
                strokeWidth = 1.5f
            )

            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(rightX, y),
                end = Offset(
                    rightX,
                    y + height
                ),
                strokeWidth = 1.5f
            )
        }
    }

    // ==================================================
    // 7. KAPI
    // ==================================================

    private fun DrawScope.drawDoor(
        snapshot: RoomSnapshot
    ) {

        val x = 40f
        val y = 170f
        val width = 100f
        val height = 220f

        drawRoundRect(
            color = Color(0xFF35251C),
            topLeft = Offset(x - 5f, y - 5f),
            size = Size(
                width + 10f,
                height + 10f
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f)
        )

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF805333),
                    Color(0xFFB17A48),
                    Color(0xFF70452D)
                ),
                startX = x,
                endX = x + width
            ),
            topLeft = Offset(x, y),
            size = Size(width, height)
        )

        drawLine(
            color = Color(0xFF422C1C),
            start = Offset(x + 15f, y + 10f),
            end = Offset(
                x + 15f,
                y + height - 10f
            ),
            strokeWidth = 4f
        )

        drawLine(
            color = Color(0xFF422C1C),
            start = Offset(x + width - 15f, y + 10f),
            end = Offset(
                x + width - 15f,
                y + height - 10f
            ),
            strokeWidth = 4f
        )

        drawCircle(
            color = Color(0xFFFFD36A),
            radius = 5f,
            center = Offset(
                x + width - 17f,
                y + height / 2f
            )
        )
    }

    // ==================================================
    // 8. KİTAPLIK
    // ==================================================

    private fun DrawScope.drawBookshelf() {

        val x = 180f
        val y = 130f
        val width = 150f
        val height = 230f

        drawRoundRect(
            color = Color(0xFF51331F),
            topLeft = Offset(x, y),
            size = Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f)
        )

        val shelfYs = listOf(
            y + 75f,
            y + 150f
        )

        shelfYs.forEach { shelfY ->

            drawRect(
                color = Color(0xFF9A633B),
                topLeft = Offset(x, shelfY),
                size = Size(width, 9f)
            )
        }

        val bookColors = listOf(
            Color(0xFFB84444),
            Color(0xFF477A9B),
            Color(0xFFD6AA48),
            Color(0xFF59875D),
            Color(0xFF875A9B),
            Color(0xFFB77B49)
        )

        for (shelf in 0..2) {

            for (book in 0..4) {

                val bookX =
                    x + 10f + book * 27f

                val bookY =
                    y + 20f + shelf * 75f

                drawRoundRect(
                    color = bookColors[
                        (book + shelf) % bookColors.size
                    ],
                    topLeft = Offset(
                        bookX,
                        bookY
                    ),
                    size = Size(
                        20f,
                        48f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
                )
            }
        }
    }

    // ==================================================
    // 9. LAMBA
    // ==================================================

    private fun DrawScope.drawLamp(
        snapshot: RoomSnapshot
    ) {

        val x = 590f
        val y = 100f

        drawLine(
            color = Color(0xFF49372D),
            start = Offset(x, y + 35f),
            end = Offset(x, y + 125f),
            strokeWidth = 7f
        )

        drawCircle(
            color = Color(0xFF49372D),
            radius = 15f,
            center = Offset(x, y + 130f)
        )

        drawRoundRect(
            color = Color(0xFFFFD58A),
            topLeft = Offset(x - 30f, y),
            size = Size(60f, 35f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
        )

        if (snapshot.isLightingEnabled) {

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD27A).copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(x, y + 25f),
                    radius = 150f
                ),
                radius = 150f,
                center = Offset(x, y + 25f)
            )
        }
    }

    // ==================================================
    // 10. TELEVİZYON
    // ==================================================

    private fun DrawScope.drawTelevision(
        snapshot: RoomSnapshot
    ) {

        val x = 430f
        val y = 180f
        val width = 190f
        val height = 120f

        drawRoundRect(
            color = Color(0xFF17191E),
            topLeft = Offset(x, y),
            size = Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
        )

        val isOn =
            snapshot.televisionState == TelevisionState.ON

        val screenColor =
            if (isOn) {
                Color(0xFF274D76)
            } else {
                Color(0xFF090A0D)
            }

        drawRoundRect(
            brush = if (isOn) {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF173C68),
                        Color(0xFF477FA7),
                        Color(0xFF172A49)
                    ),
                    start = Offset(x, y),
                    end = Offset(x + width, y + height)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        screenColor,
                        screenColor
                    )
                )
            },
            topLeft = Offset(
                x + 9f,
                y + 9f
            ),
            size = Size(
                width - 18f,
                height - 18f
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
        )

        if (isOn) {

            drawCircle(
                color = Color(0xFF9DD9FF).copy(alpha = 0.45f),
                radius = 13f,
                center = Offset(
                    x + width / 2f,
                    y + height / 2f
                )
            )
        }

        drawCircle(
            color = if (isOn) Color.Green else Color.DarkGray,
            radius = 3f,
            center = Offset(
                x + width - 12f,
                y + height - 8f
            )
        )

        // TV ayağı.

        drawLine(
            color = Color(0xFF262626),
            start = Offset(
                x + width / 2f,
                y + height
            ),
            end = Offset(
                x + width / 2f,
                y + height + 18f
            ),
            strokeWidth = 8f
        )
    }

    // ==================================================
    // 11. HALI
    // ==================================================

    private fun DrawScope.drawCarpet() {

        drawRoundRect(
            color = Color(0xFF7C3038),
            topLeft = Offset(350f, 570f),
            size = Size(400f, 100f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f)
        )

        drawRoundRect(
            color = Color(0xFFD6A15D),
            topLeft = Offset(365f, 580f),
            size = Size(370f, 80f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f),
            style = Stroke(width = 3f)
        )

        drawLine(
            color = Color(0xFFD6A15D),
            start = Offset(550f, 580f),
            end = Offset(550f, 660f),
            strokeWidth = 2f
        )
    }

    // ==================================================
    // 12. YATAK
    // ==================================================

    private fun DrawScope.drawBed() {

        val x = 80f
        val y = 390f

        // Yatak gövdesi.

        drawRoundRect(
            color = Color(0xFF583A2A),
            topLeft = Offset(x, y + 45f),
            size = Size(250f, 105f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
        )

        // Yatak örtüsü.

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFB54B57),
                    Color(0xFF7E303D)
                )
            ),
            topLeft = Offset(x + 8f, y + 52f),
            size = Size(234f, 91f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(9f)
        )

        // Yastık.

        drawRoundRect(
            color = Color(0xFFE9E1D6),
            topLeft = Offset(x + 18f, y + 12f),
            size = Size(85f, 65f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
        )

