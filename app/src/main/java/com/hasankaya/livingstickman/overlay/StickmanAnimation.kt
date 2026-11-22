
package com.hasankaya.livingstickman.overlay

import android.animation.ValueAnimator
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.math.PI
import kotlin.math.sin

class StickmanAnimation(
    private val view: StickmanOverlayView
) {

    enum class Motion {
        IDLE,
        WALK,
        RUN,
        JUMP,
        WAVE,
        SLEEP
    }

    data class AnimationFrame(
        val motion: Motion,
        val progress: Float,
        val bodyBob: Float,
        val armSwing: Float,
        val legSwing: Float,
        val capeSwing: Float,
        val headTilt: Float,
        val jumpHeight: Float,
        val waveAngle: Float,
        val sleepOffset: Float
    )

    private var animator: ValueAnimator? = null

    private var currentMotion = Motion.IDLE
    private var progress = 0f

    private var animationStartedAt = 0L
    private var paused = false

    private var pausedProgress = 0f

    private val frame: AnimationFrame
        get() = calculateFrame(currentMotion, progress)

    fun play(motion: Motion) {

        if (currentMotion == motion &&
            animator?.isRunning == true
        ) {
            return
        }

        stopAnimator()

        currentMotion = motion
        progress = 0f
        pausedProgress = 0f
        paused = false
        animationStartedAt = SystemClock.uptimeMillis()

        val duration = when (motion) {
            Motion.IDLE -> 1800L
            Motion.WALK -> 650L
            Motion.RUN -> 300L
            Motion.JUMP -> 750L
            Motion.WAVE -> 900L
            Motion.SLEEP -> 2400L
        }

        val interpolator = when (motion) {
            Motion.JUMP -> AccelerateDecelerateInterpolator()
            Motion.IDLE -> AccelerateDecelerateInterpolator()
            else -> LinearInterpolator()
        }

        animator = ValueAnimator.ofFloat(0f, 1f).apply {

            this.duration = duration
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            this.interpolator = interpolator

            addUpdateListener { valueAnimator ->

                progress =
                    valueAnimator.animatedValue as Float

                view.setAnimationFrame(
                    calculateFrame(currentMotion, progress)
                )
            }

            start()
        }
    }

    private fun calculateFrame(
        motion: Motion,
        t: Float
    ): AnimationFrame {

        val angle = t * 2f * PI.toFloat()
        val wave = sin(angle)

        return when (motion) {

            Motion.IDLE -> AnimationFrame(
                motion = motion,
                progress = t,
                bodyBob = sin(angle) * 2.5f,
                armSwing = sin(angle * 0.5f) * 3f,
                legSwing = 0f,
                capeSwing = sin(angle) * 5f,
                headTilt = sin(angle * 0.5f) * 2f,
                jumpHeight = 0f,
                waveAngle = 0f,
                sleepOffset = 0f
            )

            Motion.WALK -> AnimationFrame(
                motion = motion,
                progress = t,
                bodyBob = abs(sin(angle)) * 5f,
                armSwing = wave * 24f,
                legSwing = wave * 30f,
                capeSwing = wave * 13f,
                headTilt = sin(angle) * 3f,
                jumpHeight = 0f,
                waveAngle = 0f,
                sleepOffset = 0f
            )

            Motion.RUN -> AnimationFrame(
                motion = motion,
                progress = t,
                bodyBob = abs(sin(angle)) * 9f,
                armSwing = wave * 42f,
                legSwing = wave * 48f,
                capeSwing = wave * 25f,
                headTilt = sin(angle) * 6f,
                jumpHeight = 0f,
                waveAngle = 0f,
                sleepOffset = 0f
            )

            Motion.JUMP -> {
                val jump = sin(t * PI.toFloat())

                AnimationFrame(
                    motion = motion,
                    progress = t,
                    bodyBob = 0f,
                    armSwing = jump * 35f,
                    legSwing = jump * 18f,
                    capeSwing = jump * 20f,
                    headTilt = -jump * 5f,
                    jumpHeight = jump * 55f,
                    waveAngle = 0f,
                    sleepOffset = 0f
                )
            }

            Motion.WAVE -> {
                val waveArm =
                    sin(t * 4f * PI.toFloat())

                AnimationFrame(
                    motion = motion,
                    progress = t,
                    bodyBob = sin(angle) * 2f,
                    armSwing = 0f,
                    legSwing = 0f,
                    capeSwing = sin(angle) * 7f,
                    headTilt = -3f,
                    jumpHeight = 0f,
                    waveAngle = waveArm * 65f,
                    sleepOffset = 0f
                )
            }

            Motion.SLEEP -> AnimationFrame(
                motion = motion,
                progress = t,
                bodyBob = sin(angle) * 1.5f,
                armSwing = 0f,
                legSwing = 0f,
                capeSwing = sin(angle) * 2f,
                headTilt = 12f,
                jumpHeight = 0f,
                waveAngle = 0f,
                sleepOffset = sin(angle) * 3f
            )
        }
    }

    fun getCurrentFrame(): AnimationFrame {
        return frame
    }

    fun pause() {
        if (animator?.isRunning == true) {
            pausedProgress = progress
            animator?.pause()
            paused = true
        }
    }

    fun resume() {
        if (paused) {
            animator?.resume()
            paused = false
        }
    }

    fun isPaused(): Boolean = paused

    fun getCurrentMotion(): Motion = currentMotion

    fun isAnimating(): Boolean =
        animator?.isRunning == true

    fun stop() {
        stopAnimator()

        currentMotion = Motion.IDLE
        progress = 0f
        pausedProgress = 0f
        paused = false

        view.setAnimationFrame(
            calculateFrame(Motion.IDLE, 0f)
        )
    }

    private fun stopAnimator() {
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        animator = null
    }
}
