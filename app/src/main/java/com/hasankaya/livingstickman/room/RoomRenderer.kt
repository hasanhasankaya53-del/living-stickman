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
/
        // Yastık dikiş çizgisi.
        drawLine(
            color = Color(0xFFD1C5B8),
            start = Offset(x + 28f, y + 44f),
            end = Offset(x + 92f, y + 44f),
            strokeWidth = 2f
        )

        // Yatak başlığı.
        drawRoundRect(
            color = Color(0xFF493024),
            topLeft = Offset(x - 8f, y - 5f),
            size = Size(16f, 165f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f)
        )
    }

    // ==================================================
    // 13. KOLTUK
    // ==================================================

    private fun DrawScope.drawSofa() {
        val x = 390f
        val y = 455f

        drawRoundRect(
            color = Color(0xFF493044),
            topLeft = Offset(x, y - 42f),
            size = Size(230f, 92f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(22f)
        )

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF98617F),
                    Color(0xFF70445F)
                ),
                startY = y,
                endY = y + 62f
            ),
            topLeft = Offset(x + 18f, y + 4f),
            size = Size(194f, 62f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f)
        )

        drawRoundRect(
            color = Color(0xFF70445F),
            topLeft = Offset(x - 8f, y - 8f),
            size = Size(34f, 78f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(13f)
        )

        drawRoundRect(
            color = Color(0xFF70445F),
            topLeft = Offset(x + 204f, y - 8f),
            size = Size(34f, 78f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(13f)
        )

        drawLine(
            color = Color(0xFFD6A8BE).copy(alpha = 0.65f),
            start = Offset(x + 115f, y + 10f),
            end = Offset(x + 115f, y + 58f),
            strokeWidth = 2f
        )

        listOf(x + 25f, x + 195f).forEach { legX ->
            drawLine(
                color = Color(0xFF39251D),
                start = Offset(legX, y + 65f),
                end = Offset(legX - 3f, y + 82f),
                strokeWidth = 7f,
                cap = StrokeCap.Round
            )
        }
    }

    // ==================================================
    // 14. BİLGİSAYAR MASASI
    // ==================================================

    private fun DrawScope.drawComputerDesk(
        snapshot: RoomSnapshot
    ) {
        val x = 690f
        val y = 430f

        val computerIsOn =
            snapshot.computerState == ComputerState.ON ||
                snapshot.computerState == ComputerState.STARTING

        drawRoundRect(
            color = Color(0xFF5B3928),
            topLeft = Offset(x, y),
            size = Size(225f, 18f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f)
        )

        drawRect(
            color = Color(0xFF8A5B3B),
            topLeft = Offset(x + 8f, y + 4f),
            size = Size(209f, 5f)
        )

        drawRect(
            color = Color(0xFF493024),
            topLeft = Offset(x + 18f, y + 18f),
            size = Size(13f, 112f)
        )

        drawRect(
            color = Color(0xFF493024),
            topLeft = Offset(x + 195f, y + 18f),
            size = Size(13f, 112f)
        )

        drawRoundRect(
            color = Color(0xFF20242B),
            topLeft = Offset(x + 62f, y - 91f),
            size = Size(105f, 78f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f)
        )

        drawRoundRect(
            brush = if (computerIsOn) {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF164B70),
                        Color(0xFF45A6C9),
                        Color(0xFF172E55)
                    ),
                    start = Offset(x + 67f, y - 86f),
                    end = Offset(x + 162f, y - 18f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF080B10),
                        Color(0xFF171B22)
                    )
                )
            },
            topLeft = Offset(x + 68f, y - 85f),
            size = Size(93f, 65f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f)
        )

        if (computerIsOn) {
            drawLine(
                color = Color(0xFFB8F1FF).copy(alpha = 0.8f),
                start = Offset(x + 78f, y - 69f),
                end = Offset(x + 130f, y - 69f),
                strokeWidth = 3f
            )

            drawLine(
                color = Color(0xFFB8F1FF).copy(alpha = 0.55f),
                start = Offset(x + 78f, y - 58f),
                end = Offset(x + 117f, y - 58f),
                strokeWidth = 2f
            )
        }

        drawRect(
            color = Color(0xFF30343B),
            topLeft = Offset(x + 108f, y - 13f),
            size = Size(14f, 13f)
        )

        drawRoundRect(
            color = Color(0xFF30343B),
            topLeft = Offset(x + 91f, y - 2f),
            size = Size(48f, 5f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f)
        )

        drawRoundRect(
            color = Color(0xFF252932),
            topLeft = Offset(x + 76f, y + 5f),
            size = Size(92f, 8f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f)
        )

        drawOval(
            color = Color(0xFF343943),
            topLeft = Offset(x + 179f, y + 4f),
            size = Size(13f, 9f)
        )

        if (computerIsOn && snapshot.isLightingEnabled) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4ABBEF).copy(alpha = 0.22f),
                        Color.Transparent
                    ),
                    center = Offset(x + 115f, y - 35f),
                    radius = 145f
                ),
                topLeft = Offset(x - 20f, y - 170f),
                size = Size(265f, 250f)
            )
        }
    }

    // ==================================================
    // 15. BİTKİ
    // ==================================================

    private fun DrawScope.drawPlant() {
        val potX = 965f
        val potY = 500f

        val pot = Path().apply {
            moveTo(potX, potY)
            lineTo(potX + 62f, potY)
            lineTo(potX + 52f, potY + 65f)
            lineTo(potX + 10f, potY + 65f)
            close()
        }

        drawPath(
            path = pot,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFD46C4D),
                    Color(0xFF8E3E35)
                ),
                startY = potY,
                endY = potY + 65f
            )
        )

        drawOval(
            color = Color(0xFFE58A65),
            topLeft = Offset(potX - 3f, potY - 7f),
            size = Size(68f, 17f)
        )

        drawOval(
            color = Color(0xFF493126),
            topLeft = Offset(potX + 4f, potY - 4f),
            size = Size(54f, 10f)
        )

        drawLine(
            color = Color(0xFF426B3D),
            start = Offset(potX + 31f, potY - 5f),
            end = Offset(potX + 31f, potY - 118f),
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )

        val leafColor = Color(0xFF4F8B4B)
        val leafHighlight = Color(0xFF80B96A)

        rotate(-38f, pivot = Offset(potX + 29f, potY - 65f)) {
            drawOval(
                color = leafColor,
                topLeft = Offset(potX - 9f, potY - 100f),
                size = Size(28f, 62f)
            )

            drawLine(
                color = leafHighlight,
                start = Offset(potX + 5f, potY - 95f),
                end = Offset(potX + 5f, potY - 47f),
                strokeWidth = 2f
            )
        }

        rotate(38f, pivot = Offset(potX + 32f, potY - 83f)) {
            drawOval(
                color = Color(0xFF5D9D52),
                topLeft = Offset(potX + 30f, potY - 125f),
                size = Size(30f, 67f)
            )

            drawLine(
                color = leafHighlight,
                start = Offset(potX + 45f, potY - 120f),
                end = Offset(potX + 45f, potY - 68f),
                strokeWidth = 2f
            )
        }

        rotate(-18f, pivot = Offset(potX + 31f, potY - 105f)) {
            drawOval(
                color = Color(0xFF72A95A),
                topLeft = Offset(potX + 9f, potY - 158f),
                size = Size(30f, 62f)
            )
        }
    }

    // ==================================================
    // 16. NETHER PORTALI
    // ==================================================

    private fun DrawScope.drawPortal(
        snapshot: RoomSnapshot
    ) {
        val x = 1050f
        val y = 290f
        val width = 110f
        val height = 240f

        val portalIsActive =
            snapshot.portalState == PortalState.ACTIVATING ||
                snapshot.portalState == PortalState.ACTIVE ||
                snapshot.portalState == PortalState.DESTINATION_READY ||
                snapshot.portalState == PortalState.ENTERING ||
                snapshot.portalState == PortalState.TRANSITIONING

        drawRoundRect(
            color = Color(0xFF29252F),
            topLeft = Offset(x - 13f, y - 12f),
            size = Size(width + 26f, height + 24f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
        )

        drawRoundRect(
            color = Color(0xFF716477),
            topLeft = Offset(x - 5f, y - 5f),
            size = Size(width + 10f, height + 10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
            style = Stroke(width = 7f)
        )

        if (portalIsActive) {
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF40105F),
                        Color(0xFF8C35C4),
                        Color(0xFF3A145F),
                        Color(0xFFB35BE0)
                    ),
                    start = Offset(x, y),
                    end = Offset(x + width, y + height)
                ),
                topLeft = Offset(x, y),
                size = Size(width, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f)
            )

            for (index in 0..4) {
                val lineY = y + 28f + index * 43f

                val wave = Path().apply {
                    moveTo(x + 10f, lineY)
                    cubicTo(
                        x + 35f, lineY - 17f,
                        x + 72f, lineY + 17f,
                        x + width - 10f, lineY
                    )
                }

                drawPath(
                    path = wave,
                    color = Color(0xFFE8C4FF).copy(alpha = 0.55f),
                    style = Stroke(
                        width = 3f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            if (snapshot.isLightingEnabled) {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFB95CFF).copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(x + width / 2f, y + height / 2f),
                        radius = 230f
                    ),
                    topLeft = Offset(x - 105f, y - 100f),
                    size = Size(width + 210f, height + 200f)
                )
            }
        } else {
            drawRoundRect(
                color = Color(0xFF17121F),
                topLeft = Offset(x, y),
                size = Size(width, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f)
            )

            drawLine(
                color = Color(0xFF695B70),
                start = Offset(x + 12f, y + height - 12f),
                end = Offset(x + width - 12f, y + 12f),
                strokeWidth = 4f
            )
        }

        drawRoundRect(
            color = Color(0xFF4A414F),
            topLeft = Offset(x - 20f, y + height + 3f),
            size = Size(width + 40f, 13f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
        )
    }

    // ==================================================
    // 17. DUVAR DEKORASYONLARI
    // ==================================================

    private fun DrawScope.drawWallDecorations() {
        drawRoundRect(
            color = Color(0xFF4A3025),
            topLeft = Offset(370f, 65f),
            size = Size(145f, 92f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF7BA6B5),
                    Color(0xFFE3C58E)
                ),
                start = Offset(378f, 73f),
                end = Offset(507f, 149f)
            ),
            topLeft = Offset(378f, 73f),
            size = Size(129f, 76f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
        )

        drawCircle(
            color = Color(0xFFFFD477),
            radius = 15f,
            center = Offset(480f, 94f)
        )

        val mountain = Path().apply {
            moveTo(380f, 145f)
            lineTo(418f, 101f)
            lineTo(446f, 132f)
            lineTo(469f, 111f)
            lineTo(507f, 145f)
            close()
        }

        drawPath(
            path = mountain,
            color = Color(0xFF476B5A)
        )

        drawCircle(
            color = Color(0xFF493A32),
            radius = 29f,
            center = Offset(620f, 105f)
        )

        drawCircle(
            color = Color(0xFFE9DDC7),
            radius = 24f,
            center = Offset(620f, 105f)
        )

        drawLine(
            color = Color(0xFF493A32),
            start = Offset(620f, 105f),
            end = Offset(620f, 88f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color(0xFF493A32),
            start = Offset(620f, 105f),
            end = Offset(634f, 112f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        drawRect(
            color = Color(0xFF765039),
            topLeft = Offset(1040f, 190f),
            size = Size(135f, 9f)
        )

        drawRect(
            color = Color(0xFF533A2C),
            topLeft = Offset(1050f, 199f),
            size = Size(8f, 28f)
        )

        drawRect(
            color = Color(0xFF533A2C),
            topLeft = Offset(1157f, 199f),
            size = Size(8f, 28f)
        )
    }
}

// ======================================================
// 18. ROOM CANVAS
// ======================================================

@Composable
fun RoomCanvas(
    layout: RoomLayout,
    lighting: RoomLighting,
    snapshot: RoomSnapshot,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        RoomRenderer(
            layout = layout,
            lighting = lighting
        ).drawRoom(
            drawScope = this,
            snapshot = snapshot
        )
    }
}        
