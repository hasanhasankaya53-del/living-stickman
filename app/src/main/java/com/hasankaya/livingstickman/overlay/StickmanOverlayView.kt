package com.hasankaya.livingstickman.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class StickmanOverlayView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val density = resources.displayMetrics.density

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

    private fun dp(value: Float): Float = value * density

    fun setAnimationFrame(
        frame: StickmanAnimation.AnimationFrame
    ) {
        animationFrame = frame
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width <= 0 || height <= 0) return

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
        drawCapeDetails(canvas, frame)
        drawLegs(canvas, frame)
        drawBoots(canvas, frame)
        drawBody(canvas)
        drawArmorDetails(canvas)
        drawBelt(canvas)
        drawArms(canvas, frame)
        drawHands(canvas, frame)
        drawHead(canvas, frame)
        drawFace(canvas, frame)
        drawCrown(canvas, frame)
        drawEffects(canvas, frame)

        canvas.restoreToCount(save)
    }

    private fun drawShadow(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(75, 0, 0, 0)

        val shadowScale =
            (1f - frame.jumpHeight / 120f).coerceIn(0.35f, 1f)

        canvas.save()
        canvas.scale(shadowScale, 1f)

        canvas.drawOval(
            RectF(-35f, 76f, 35f, 85f),
            paint
        )

        canvas.restore()
    }

    private fun drawCape(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        val movement = frame.capeSwing
        val running = frame.motion == StickmanAnimation.Motion.RUN

        val cape = Path().apply {
            moveTo(-12f, -25f)

            cubicTo(
                -27f,
                -16f + movement,
                -38f,
                12f + movement,
                if (running) -53f else -43f,
                66f + movement
            )

            cubicTo(
                -24f,
                65f + movement,
                2f,
                57f - movement,
                16f,
                67f
            )

            lineTo(13f, -25f)
            close()
        }

        paint.color = Color.rgb(100, 3, 20)
        paint.style = Paint.Style.FILL
        canvas.drawPath(cape, paint)

        val innerCape = Path().apply {
            moveTo(-10f, -21f)

            cubicTo(
                -21f,
                2f + movement,
                -28f,
                34f + movement,
                -37f,
                59f + movement
            )

            cubicTo(
                -12f,
                57f + movement,
                3f,
                55f - movement,
                10f,
                62f
            )

            lineTo(9f, -21f)
            close()
        }

        paint.color = Color.rgb(190, 8, 36)
        canvas.drawPath(innerCape, paint)

        paint.color = Color.rgb(240, 50, 70)
        paint.strokeWidth = 1.5f
        paint.style = Paint.Style.STROKE

        canvas.drawPath(cape, paint)

        paint.style = Paint.Style.FILL
    }

    private fun drawCapeDetails(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        val movement = frame.capeSwing

        paint.color = Color.rgb(235, 65, 78)
        paint.strokeWidth = 1.2f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

        canvas.drawLine(
            -16f,
            -8f + movement * 0.2f,
            -29f,
            40f + movement,
            paint
        )

        canvas.drawLine(
            -9f,
            0f + movement * 0.2f,
            -18f,
            49f + movement,
            paint
        )

        paint.style = Paint.Style.FILL
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

        paint.color = Color.rgb(16, 16, 23)
        paint.strokeWidth = 11f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

        canvas.drawLine(0f, 0f, 0f, 25f, paint)

        paint.color = Color.rgb(65, 65, 80)
        paint.strokeWidth = 8f

        canvas.drawLine(0f, 23f, 0f, 34f, paint)

        paint.color = Color.rgb(120, 120, 135)
        paint.strokeWidth = 2f

        canvas.drawLine(-4f, 10f, 4f, 10f, paint)
        canvas.drawLine(-4f, 20f, 4f, 20f, paint)

        paint.style = Paint.Style.FILL

        canvas.restore()
    }

    private fun drawBoots(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        drawBoot(canvas, -1, frame.legSwing)
        drawBoot(canvas, 1, frame.legSwing)
    }

    private fun drawBoot(
        canvas: Canvas,
        side: Int,
        swing: Float
    ) {
        canvas.save()

        canvas.translate(side * 9f, 64f)
        canvas.rotate(side * swing)

        paint.color = Color.rgb(10, 10, 16)

        canvas.drawRoundRect(
            RectF(-5f, -2f, 14f, 8f),
            3f,
            3f,
            paint
        )

        paint.color = Color.rgb(190, 145, 45)
        paint.strokeWidth = 1.5f

        canvas.drawLine(0f, 2f, 10f, 2f, paint)

        canvas.restore()
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

        paint.color = Color.rgb(13, 13, 21)
        paint.style = Paint.Style.FILL
        canvas.drawPath(armor, paint)

        paint.color = Color.rgb(95, 95, 115)
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        canvas.drawPath(armor, paint)

        paint.style = Paint.Style.FILL

        paint.color = Color.rgb(35, 35, 48)

        canvas.drawRoundRect(
            RectF(-10f, -25f, 10f, 32f),
            4f,
            4f,
            paint
        )
    }

    private fun drawArmorDetails(canvas: Canvas) {
        paint.color = Color.rgb(75, 75, 92)

        canvas.drawRoundRect(
            RectF(-12f, -22f, 12f, -12f),
            3f,
            3f,
            paint
        )

        paint.color = Color.rgb(160, 160, 175)
        paint.strokeWidth = 1.5f

        canvas.drawLine(-10f, -18f, 10f, -18f, paint)

        paint.color = Color.rgb(120, 120, 140)

        canvas.drawCircle(-13f, -13f, 3f, paint)
        canvas.drawCircle(13f, -13f, 3f, paint)
    }

    private fun drawBelt(canvas: Canvas) {
        paint.color = Color.rgb(110, 75, 28)

        canvas.drawRoundRect(
            RectF(-17f, 1f, 17f, 8f),
            2f,
            2f,
            paint
        )

        paint.color = Color.rgb(230, 180, 55)

        canvas.drawRoundRect(
            RectF(-5f, 0f, 5f, 9f),
            2f,
            2f,
            paint
        )

        paint.color = Color.rgb(255, 225, 115)
        canvas.drawCircle(0f, 4.5f, 2f, paint)
    }

    private fun drawArms(
    canvas: Canvas,
    frame: StickmanAnimation.AnimationFrame
) {
    drawArm(
        canvas = canvas,
        side = -1,
        swing = frame.armSwing,
        wave = frame.waveAngle
    )

    drawArm(
        canvas = canvas,
        side = 1,
        swing = frame.armSwing,
        wave = 0f
    )
    }

    private fun drawArm(
    canvas: Canvas,
    side: Int,
    swing: Float,
    wave: Float
) {
    canvas.save()

    canvas.translate(
        side * 15f,
        -20f
    )

    val angle = if (side < 0) {
        -swing - 25f - wave
    } else {
        swing + 25f
    }

    canvas.rotate(angle)

    paint.color = Color.rgb(17, 17, 24)
    paint.strokeWidth = 10f
    paint.strokeCap = Paint.Cap.ROUND
    paint.style = Paint.Style.STROKE

    canvas.drawLine(
        0f,
        0f,
        0f,
        21f,
        paint
    )

    paint.color = Color.rgb(70, 70, 88)
    paint.strokeWidth = 7f

    canvas.drawLine(
        0f,
        19f,
        0f,
        32f,
        paint
    )

    paint.color = Color.rgb(125, 125, 145)
    paint.strokeWidth = 2f

    canvas.drawLine(
        -3f,
        12f,
        3f,
        12f,
        paint
    )

    paint.style = Paint.Style.FILL

    canvas.restore()
    }
    
    private fun drawHands(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        drawHand(
            canvas,
            -1,
            frame.armSwing,
            frame.waveAngle
        )

        drawHand(
            canvas,
            1,
            frame.armSwing,
            0f
        )
    }

    private fun drawHand(
        canvas: Canvas,
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
        canvas.translate(0f, 32f)

        paint.color = Color.rgb(12, 12, 18)
        canvas.drawCircle(0f, 0f, 5f, paint)

        paint.color = Color.rgb(125, 125, 145)
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE

        canvas.drawCircle(0f, 0f, 5f, paint)

        paint.style = Paint.Style.FILL

        canvas.restore()
    }

    private fun drawHead(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        canvas.save()
        canvas.rotate(frame.headTilt)

        paint.color = Color.rgb(10, 10, 16)
        canvas.drawCircle(0f, -45f, 17f, paint)

        paint.color = Color.rgb(105, 105, 125)
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        canvas.drawCircle(0f, -45f, 17f, paint)

        paint.style = Paint.Style.FILL

        canvas.restore()
    }

    private fun drawFace(
        canvas: Canvas,
        frame: StickmanAnimation.AnimationFrame
    ) {
        canvas.save()
        canvas.rotate(frame.headTilt)

        when (frame.motion) {
            StickmanAnimation.Motion.SLEEP -> {
                paint.color = Color.WHITE
                paint.strokeWidth = 2f
                paint.style = Paint.Style.STROKE

                canvas.drawLine(-8f, -45f, -3f, -45f, paint)
                canvas.drawLine(3f, -45f, 8f, -45f, paint)

                paint.style = Paint.Style.FILL
            }

            else -> {
                paint.color = Color.rgb(255, 205, 70)

                canvas.drawCircle(-6f, -46f, 2.5f, paint)
                canvas.drawCircle(6f, -46f, 2.5f, paint)

                if (frame.motion == StickmanAnimation.Motion.WAVE) {
                    paint.color = Color.WHITE
                    paint.strokeWidth = 1.5f
                    paint.style = Paint.Style.STROKE

                    canvas.drawArc(
                        RectF(-5f, -42f, 5f, -36f),
                        0f,
                        180f,
                        false,
                        paint
                    )

                    paint.style = Paint.Style.FILL
                }
            }
        }

        canvas.restore()
    }

    private fun drawCrown(canvas: Canvas, frame: StickmanAnimation.AnimationFrame) {
        canvas.save()
        canvas.rotate(frame.headTilt)

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

        paint.color = Color.rgb(210, 145, 15)
        canvas.drawPath(crown, paint)

        paint.color = Color.rgb(255, 195, 35)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f

        canvas.drawPath(crown, paint)

        paint.style = Paint.Style.FILL

        paint.color = Color.rgb(255, 225, 100)
        canvas.drawCircle(0f, -71f, 3f, paint)

        paint.color = Color.RED
        canvas.drawCircle(0f, -63f, 2f, paint)

        paint.color = Color.WHITE
        canvas.drawCircle(-10f, -69f, 1.5f, paint)
        canvas.drawCircle(10f, -69f, 1.5f, paint)

        canvas.restore()
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

                val pulse =
                    26f + abs(sin(frame.progress * PI * 2)).toFloat() * 5f

                canvas.drawCircle(0f, 0f, pulse, paint)

                paint.style = Paint.Style.FILL
            }

            else -> Unit
        }
    }
}
