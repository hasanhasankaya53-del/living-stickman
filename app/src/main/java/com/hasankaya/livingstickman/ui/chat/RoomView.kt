
package com.hasankaya.livingstickman.ui.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoomView(
    modifier: Modifier = Modifier
) {
    val wall = Color(0xFF252536)
    val floor = Color(0xFF493A35)
    val wood = Color(0xFF805638)
    val dark = Color(0xFF171722)
    val purple = Color(0xFF8B4DDB)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(wall, RoundedCornerShape(12.dp))
    ) {
        val w = size.width
        val h = size.height

        // Duvar ve zemin
        drawRect(wall, size = Size(w, h))
        drawRect(
            color = floor,
            topLeft = Offset(0f, h * 0.78f),
            size = Size(w, h * 0.22f)
        )

        // Zemin çizgileri
        for (i in 1..5) {
            val x = w * i / 6f
            drawLine(
                color = Color(0xFF665047),
                start = Offset(x, h * 0.79f),
                end = Offset(x, h),
                strokeWidth = 2f
            )
        }

        // Pencere
        drawRect(
            color = Color(0xFF101A35),
            topLeft = Offset(w * 0.07f, h * 0.12f),
            size = Size(w * 0.25f, h * 0.29f)
        )
        drawLine(
            color = Color(0xFF8DB9E8),
            start = Offset(w * 0.195f, h * 0.12f),
            end = Offset(w * 0.195f, h * 0.41f),
            strokeWidth = 4f
        )
        drawLine(
            color = Color(0xFF8DB9E8),
            start = Offset(w * 0.07f, h * 0.265f),
            end = Offset(w * 0.32f, h * 0.265f),
            strokeWidth = 4f
        )

        // Kitaplık
        drawRect(
            color = wood,
            topLeft = Offset(w * 0.72f, h * 0.18f),
            size = Size(w * 0.19f, h * 0.48f)
        )
        for (i in 1..3) {
            drawLine(
                color = dark,
                start = Offset(w * 0.72f, h * (0.18f + i * 0.12f)),
                end = Offset(w * 0.91f, h * (0.18f + i * 0.12f)),
                strokeWidth = 5f
            )
        }

        // Kitaplar
        val bookColors = listOf(
            Color(0xFFCF5252),
            Color(0xFF5489C7),
            Color(0xFFE0B64C),
            Color(0xFF55A879)
        )
        for (i in bookColors.indices) {
            drawRect(
                color = bookColors[i],
                topLeft = Offset(w * (0.74f + i * 0.04f), h * 0.21f),
                size = Size(w * 0.025f, h * 0.08f)
            )
        }

        // Kanepe
        drawRoundRect(
            color = Color(0xFF654C91),
            topLeft = Offset(w * 0.35f, h * 0.61f),
            size = Size(w * 0.29f, h * 0.15f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f)
        )
        drawRoundRect(
            color = Color(0xFF8065B0),
            topLeft = Offset(w * 0.37f, h * 0.57f),
            size = Size(w * 0.25f, h * 0.11f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
        )

        // Portal
        drawOval(
            color = purple,
            topLeft = Offset(w * 0.82f, h * 0.68f),
            size = Size(w * 0.12f, h * 0.10f)
        )
        drawOval(
            color = Color(0xFF27134A),
            topLeft = Offset(w * 0.845f, h * 0.695f),
            size = Size(w * 0.07f, h * 0.06f)
        )

        // Bilgisayar masası
        drawRect(
            color = wood,
            topLeft = Offset(w * 0.39f, h * 0.43f),
            size = Size(w * 0.22f, h * 0.025f)
        )
        drawRect(
            color = dark,
            topLeft = Offset(w * 0.47f, h * 0.34f),
            size = Size(w * 0.08f, h * 0.09f)
        )
        drawRect(
            color = Color(0xFF58B7D8),
            topLeft = Offset(w * 0.48f, h * 0.35f),
            size = Size(w * 0.06f, h * 0.065f)
        )
    }
}
