
package com.hasankaya.livingstickman.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class StickmanOverlayView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 7f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        // Kafa
        canvas.drawCircle(centerX, centerY - 45f, 18f, paint)

        // Gövde
        canvas.drawLine(
            centerX, centerY - 27f,
            centerX, centerY + 25f,
            paint
        )

        // Kollar
        canvas.drawLine(
            centerX, centerY - 10f,
            centerX - 25f, centerY + 10f,
            paint
        )
        canvas.drawLine(
            centerX, centerY - 10f,
            centerX + 25f, centerY + 10f,
            paint
        )

        // Bacaklar
        canvas.drawLine(
            centerX, centerY + 25f,
            centerX - 20f, centerY + 55f,
            paint
        )
        canvas.drawLine(
            centerX, centerY + 25f,
            centerX + 20f, centerY + 55f,
            paint
        )

        // Taç
        paint.color = Color.rgb(255, 190, 0)
        canvas.drawLine(centerX - 15f, centerY - 65f, centerX + 15f, centerY - 65f, paint)
        canvas.drawLine(centerX - 15f, centerY - 65f, centerX - 12f, centerY - 82f, paint)
        canvas.drawLine(centerX - 12f, centerY - 82f, centerX, centerY - 70f, paint)
        canvas.drawLine(centerX, centerY - 70f, centerX + 12f, centerY - 82f, paint)
        canvas.drawLine(centerX + 12f, centerY - 82f, centerX + 15f, centerY - 65f, paint)

        paint.color = Color.BLACK
    }
}
