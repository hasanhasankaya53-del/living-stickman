
package com.hasankaya.livingstickman.ui.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CharacterView(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        val scale = minOf(size.width / 140f, size.height / 220f)
        val cx = size.width / 2f
        val top = (size.height - 220f * scale) / 2f

        fun p(x: Float, y: Float) =
            Offset(cx + (x - 70f) * scale, top + y * scale)

        val black = Color(0xFF08080D)
        val red = Color(0xFFE32636)
        val gold = Color(0xFFFFC928)

        // Kırmızı pelerin
        val cape = Path().apply {
            moveTo(p(57f, 85f).x, p(57f, 85f).y)
            lineTo(p(83f, 85f).x, p(83f, 85f).y)
            lineTo(p(99f, 190f).x, p(99f, 190f).y)
            lineTo(p(72f, 177f).x, p(72f, 177f).y)
            lineTo(p(42f, 190f).x, p(42f, 190f).y)
            close()
        }
        drawPath(cape, red)

        // Bacaklar
        drawLine(black, p(64f, 145f), p(54f, 207f), 9f * scale, cap = StrokeCap.Round)
        drawLine(black, p(76f, 145f), p(86f, 207f), 9f * scale, cap = StrokeCap.Round)

        // Ayaklar
        drawLine(red, p(54f, 207f), p(43f, 207f), 10f * scale, cap = StrokeCap.Round)
        drawLine(red, p(86f, 207f), p(97f, 207f), 10f * scale, cap = StrokeCap.Round)

        // Gövde
        drawLine(black, p(70f, 78f), p(70f, 148f), 15f * scale, cap = StrokeCap.Round)

        // Kollar
        drawLine(black, p(59f, 88f), p(43f, 125f), 9f * scale, cap = StrokeCap.Round)
        drawLine(black, p(81f, 88f), p(97f, 125f), 9f * scale, cap = StrokeCap.Round)

        // Eller
        drawCircle(black, 6f * scale, p(43f, 125f))
        drawCircle(black, 6f * scale, p(97f, 125f))

        // Kafa
        drawCircle(black, 24f * scale, p(70f, 55f))

        // Kırmızı gözler
        drawLine(red, p(57f, 53f), p(65f, 56f), 4f * scale, cap = StrokeCap.Round)
        drawLine(red, p(75f, 56f), p(83f, 53f), 4f * scale, cap = StrokeCap.Round)

        // Atkı
        val scarf = Path().apply {
            moveTo(p(48f, 78f).x, p(48f, 78f).y)
            lineTo(p(92f, 78f).x, p(92f, 78f).y)
            lineTo(p(87f, 91f).x, p(87f, 91f).y)
            lineTo(p(53f, 91f).x, p(53f, 91f).y)
            close()
        }
        drawPath(scarf, red)

        // Atkının sarkan ucu
        drawLine(red, p(84f, 87f), p(92f, 112f), 7f * scale, cap = StrokeCap.Round)

        // Altın taç
        val crown = Path().apply {
            moveTo(p(48f, 31f).x, p(48f, 31f).y)
            lineTo(p(43f, 13f).x, p(43f, 13f).y)
            lineTo(p(57f, 21f).x, p(57f, 21f).y)
            lineTo(p(70f, 5f).x, p(70f, 5f).y)
            lineTo(p(83f, 21f).x, p(83f, 21f).y)
            lineTo(p(97f, 13f).x, p(97f, 13f).y)
            lineTo(p(92f, 31f).x, p(92f, 31f).y)
            close()
        }
        drawPath(crown, gold)
        drawLine(gold, p(48f, 32f), p(92f, 32f), 5f * scale)
    }
}
