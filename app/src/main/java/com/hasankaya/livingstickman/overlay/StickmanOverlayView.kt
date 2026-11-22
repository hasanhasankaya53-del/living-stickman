package com.hasankaya.livingstickman.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class StickmanOverlayView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val density = resources.displayMetrics.density

    private fun dp(value: Float): Float = value * density

    private var animationFrame =
        StickmanAnimation.AnimationFrame(
            motion = StickmanAnimation.Motion.IDLE,
            progress = 0f,
            bodyBob = 0f,
            armSwing = 0f,
            legSwing = 0f,
            capeSwing = 0f,
            headTilt = 0f,
            jumpHeight = 0f,
            waveAngle = 0f,
            sleepOffset = 0f
        )

    fun setAnimationFrame(
        frame: StickmanAnimation.AnimationFrame
    ) {
        animationFrame = frame
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val save = canvas.save()

        val scale = minOf(width, height) / 180f

        canvas.translate(width / 2f, height * 0.53f)
        canvas.scale(scale, scale)

        val frame = animationFrame

        canvas.translate(
            0f,
            -frame.jumpHeight + frame.bodyBob
        )

        drawShadow(canvas, frame)
        drawCape(canvas, frame)
        drawLegs(canvas, frame)
        drawBoots(canvas)
        drawBody(canvas)
        drawArmorDetails(canvas)
        drawArms(canvas, frame)
        drawHands(canvas)
        drawHead(canvas, frame)
        drawCrown(canvas)
        drawEffects(canvas, frame)

        canvas.restoreToCount(save)
    }

    private fun drawShadow(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        paint.color = Color.argb(65, 0, 0, 0)
        paint.style = Paint.Style.FILL

        val shadowScale = 1f - frame.jumpHeight / 120f

        canvas.save()
        canvas.scale(
            shadowScale.coerceIn(0.4f, 1f),
            1f
        )

        canvas.drawOval(
            RectF(-34f, 76f, 34f, 85f),
            paint
        )

        canvas.restore()
    }

    private fun drawCape(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        val movement = frame.capeSwing

        val cape = Path().apply {
            moveTo(-12f, -25f)

            cubicTo(
                -28f,
                -15f + movement,
                -36f,
                20f + movement,
                -43f,
                66f + movement
            )

            cubicTo(
                -20f,
                61f + movement,
                4f,
                57f - movement,
                16f,
                67f
            )

            lineTo(13f, -25f)
            close()
        }

        paint.color = Color.rgb(130, 5, 24)
        canvas.drawPath(cape, paint)

        paint.color = Color.rgb(195, 15, 39)

        val innerCape = Path().apply {
            moveTo(-10f, -20f)

            cubicTo(
                -22f,
                5f + movement,
                -26f,
                37f + movement,
                -35f,
                59f + movement
            )

            cubicTo(
                -12f,
                53f + movement,
                2f,
                56f - movement,
                10f,
                61f
            )

            lineTo(9f, -20f)
            close()
        }

        canvas.drawPath(innerCape, paint)

        paint.color = Color.rgb(245, 65, 75)
        paint.strokeWidth = 1.5f
        paint.style = Paint.Style.STROKE

        canvas.drawPath(cape, paint)

        paint.style = Paint.Style.FILL
    }

    private fun drawBody(canvas: Canvas) {

        val armor = Path().apply {
            moveTo(-13f, -29f)
            lineTo(13f, -29f)
            lineTo(19f, 20f)
            lineTo(12f, 37f)
            lineTo(-12f, 37f)
            lineTo(-19f, 20f)
            close()
        }

        paint.color = Color.rgb(17, 17, 24)
        canvas.drawPath(armor, paint)

        paint.color = Color.rgb(100, 100, 120)
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        canvas.drawPath(armor, paint)

        paint.style = Paint.Style.FILL
    }

    private fun drawArmorDetails(canvas: Canvas) {

        paint.color = Color.rgb(55, 55, 70)

        canvas.drawRoundRect(
            RectF(-12f, -22f, 12f, -12f),
            3f,
            3f,
            paint
        )

        paint.color = Color.rgb(205, 165, 55)

        canvas.drawRoundRect(
            RectF(-15f, 1f, 15f, 8f),
            2f,
            2f,
            paint
        )

        paint.color = Color.rgb(255, 220, 95)

        canvas.drawCircle(0f, 4.5f, 4f, paint)

        paint.color = Color.rgb(90, 90, 105)
        paint.strokeWidth = 2f

        canvas.drawLine(-12f, 15f, 12f, 15f, paint)
    }

    private fun drawLegs(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        drawLeg(canvas, -1, frame.legSwing)
        drawLeg(canvas, 1, frame.legSwing)
    }

    private fun drawLeg(
        canvas: Canvas,
        side: Int,
        swing: Float
    ) {
        canvas.save()

        canvas.translate(side * 9f, 30f)
        canvas.rotate(side * swing)

        paint.color = Color.rgb(18, 18, 25)
        paint.strokeWidth = 10f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

        canvas.drawLine(0f, 0f, 0f, 34f, paint)

        paint.color = Color.rgb(105, 105, 120)
        paint.strokeWidth = 2f

        canvas.drawLine(-4f, 10f, 4f, 10f, paint)
        canvas.drawLine(-4f, 22f, 4f, 22f, paint)

        paint.style = Paint.Style.FILL

        canvas.restore()
    }

    private fun drawBoots(canvas: Canvas) {

        paint.color = Color.rgb(12, 12, 17)

        canvas.drawRoundRect(
            RectF(-23f, 64f, -5f, 73f),
            3f,
            3f,
            paint
        )

        canvas.drawRoundRect(
            RectF(5f, 64f, 23f, 73f),
            3f,
            3f,
            paint
        )

        paint.color = Color.rgb(190, 145, 45)

        canvas.drawLine(-20f, 68f, -7f, 68f, paint)
        canvas.drawLine(7f, 68f, 20f, 68f, paint)
    }

    private fun drawArms(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        drawArm(-1, frame.armSwing, frame.waveAngle)
        drawArm(1, frame.armSwing, 0f)
    }

    private fun drawArm(
        side: Int,
        swing: Float,
        wave: Float
    ) {
        canvas.save()

        canvas.translate(side * 15f, -20f)

        val angle = if (side < 0) {
            -swing - 25f - wave
        } else {
            swing + 25f
        }

        canvas.rotate(angle)

        paint.color = Color.rgb(20, 20, 27)
        paint.strokeWidth = 9f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

        canvas.drawLine(0f, 0f, 0f, 24f, paint)

        paint.strokeWidth = 7f

        canvas.drawLine(0f, 24f, 0f, 35f, paint)

        paint.color = Color.rgb(105, 105, 120)
        paint.strokeWidth = 2f

        canvas.drawLine(-3f, 12f, 3f, 12f, paint)

        paint.style = Paint.Style.FILL

        canvas.restore()
    }

    private fun drawHands(canvas: Canvas) {

        paint.color = Color.rgb(15, 15, 20)

        canvas.drawCircle(-15f, 17f, 4f, paint)
        canvas.drawCircle(15f, 17f, 4f, paint)
    }

    private fun drawHead(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        canvas.save()
        canvas.rotate(frame.headTilt)

        paint.color = Color.rgb(12, 12, 18)
        canvas.drawCircle(0f, -45f, 17f, paint)

        paint.color = Color.rgb(100, 100, 120)
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        canvas.drawCircle(0f, -45f, 17f, paint)

        paint.style = Paint.Style.FILL

        if (frame.motion == StickmanAnimation.Motion.SLEEP) {
            paint.color = Color.WHITE
            paint.strokeWidth = 2f

            canvas.drawLine(-8f, -45f, -3f, -45f, paint)
            canvas.drawLine(3f, -45f, 8f, -45f, paint)
        } else {
            paint.color = Color.rgb(255, 205, 70)

            canvas.drawCircle(-6f, -46f, 2.5f, paint)
            canvas.drawCircle(6f, -46f, 2.5f, paint)
        }

        canvas.restore()
    }

    private fun drawCrown(canvas: Canvas) {

        val crown = Path().apply {
            moveTo(-15f, -59f)
            lineTo(-18f, -78f)
            lineTo(-8f, -69f)
            lineTo(0f, -83f)
            lineTo(8f, -69f)
            lineTo(18f, -78f)
            lineTo(15f, -59f)
            close()
        }

        paint.color = Color.rgb(255, 185, 25)
        canvas.drawPath(crown, paint)

        paint.color = Color.rgb(255, 230, 120)
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        canvas.drawPath(crown, paint)

        paint.style = Paint.Style.FILL

        paint.color = Color.RED
        canvas.drawCircle(0f, -71f, 3f, paint)

        paint.color = Color.WHITE
        canvas.drawCircle(-10f, -69f, 1.5f, paint)
        canvas.drawCircle(10f, -69f, 1.5f, paint)
    }

    private fun drawEffects(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        when (frame.motion) {

            StickmanAnimation.Motion.SLEEP -> {
                paint.color = Color.WHITE
                paint.textSize = 14f

                canvas.drawText(
                    "Z",
                    22f,
                    -58f + frame.sleepOffset,
                    paint
                )

                canvas.drawText(
                    "z",
                    33f,
                    -72f + frame.sleepOffset,
                    paint
                )
            }

            StickmanAnimation.Motion.JUMP -> {
                paint.color = Color.rgb(255, 220, 100)
                paint.strokeWidth = 2f
                paint.style = Paint.Style.STROKE

                canvas.drawCircle(0f, 0f, 28f, paint)

                paint.style = Paint.Style.FILL
            }

            else -> Unit
        }
    }
}
