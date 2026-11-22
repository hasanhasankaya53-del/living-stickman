
package com.hasankaya.livingstickman.overlay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
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

    private var paused = false

    private var animationStartedAt = 0L

    private var generation = 0

    private var lastFrame = createIdleFrame()

    private val frame: AnimationFrame
        get() = calculateFrame(currentMotion, progress)

    companion object {
        private const val FULL_CIRCLE = (2.0 * PI).toFloat()
    }

    // --------------------------------------------------
    // ANİMASYON BAŞLATMA
    // --------------------------------------------------

    fun play(motion: Motion) {

        if (
            currentMotion == motion &&
            animator?.isRunning == true
        ) {
            return
        }

        generation++

        stopAnimator()

        currentMotion = motion
        progress = 0f
        paused = false

        animationStartedAt = SystemClock.uptimeMillis()

        val currentGeneration = generation

        val duration = getDuration(motion)

        val animatorInterpolator = getInterpolator(motion)

        val oneShot = isOneShot(motion)

        val newAnimator = ValueAnimator.ofFloat(0f, 1f).apply {

            this.duration = duration

            repeatCount =
                if (oneShot) 0 else ValueAnimator.INFINITE

            repeatMode = ValueAnimator.RESTART

            interpolator = animatorInterpolator

            addUpdateListener { valueAnimator ->

                if (currentGeneration != generation) {
                    return@addUpdateListener
                }

                progress =
                    valueAnimator.animatedValue as Float

                val newFrame =
                    calculateFrame(currentMotion, progress)

                lastFrame = newFrame

                view.setAnimationFrame(newFrame)
            }

            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {

                    if (currentGeneration != generation) {
                        return
                    }

                    if (oneShot) {
                        play(Motion.IDLE)
                    }
                }
            })
        }

        animator = newAnimator

        view.setAnimationFrame(
            calculateFrame(motion, 0f)
        )

        newAnimator.start()
    }

    // --------------------------------------------------
    // ANİMASYON SÜRELERİ
    // --------------------------------------------------

    private fun getDuration(motion: Motion): Long {

        return when (motion) {

            Motion.IDLE -> 2200L

            Motion.WALK -> 650L

            Motion.RUN -> 320L

            Motion.JUMP -> 850L

            Motion.WAVE -> 1200L

            Motion.SLEEP -> 3000L
        }
    }

    // --------------------------------------------------
    // HAREKET EĞRİLERİ
    // --------------------------------------------------

    private fun getInterpolator(
        motion: Motion
    ): android.animation.TimeInterpolator {

        return when (motion) {

            Motion.IDLE ->
                AccelerateDecelerateInterpolator()

            Motion.WALK ->
                LinearInterpolator()

            Motion.RUN ->
                LinearInterpolator()

            Motion.JUMP ->
                OvershootInterpolator(0.15f)

            Motion.WAVE ->
                AccelerateDecelerateInterpolator()

            Motion.SLEEP ->
                AccelerateDecelerateInterpolator()
        }
    }

    private fun isOneShot(motion: Motion): Boolean {

        return when (motion) {

            Motion.JUMP,
            Motion.WAVE -> true

            Motion.IDLE,
            Motion.WALK,
            Motion.RUN,
            Motion.SLEEP -> false
        }
    }

    // --------------------------------------------------
    // ANA ANİMASYON HESAPLAMA
    // --------------------------------------------------

    private fun calculateFrame(
        motion: Motion,
        t: Float
    ): AnimationFrame {

        val angle = t * FULL_CIRCLE

        val wave = sin(angle)

        val cosineWave = cos(angle)

        return when (motion) {

            Motion.IDLE ->
                calculateIdle(t, angle, wave)

            Motion.WALK ->
                calculateWalk(t, angle, wave)

            Motion.RUN ->
                calculateRun(t, angle, wave)

            Motion.JUMP ->
                calculateJump(t)

            Motion.WAVE ->
                calculateWave(t, angle)

            Motion.SLEEP ->
                calculateSleep(t, angle, wave)
        }
    }

    // --------------------------------------------------
    // NORMAL BEKLEME
    // --------------------------------------------------

    private fun calculateIdle(
        t: Float,
        angle: Float,
        wave: Float
    ): AnimationFrame {

        val breathing =
            sin(angle * 0.5f)

        val bodyMovement =
            sin(angle) * 2.8f

        val capeMovement =
            sin(angle * 0.8f) * 6f

        val headMovement =
            sin(angle * 0.35f) * 2.2f

        return AnimationFrame(

            motion = Motion.IDLE,

            progress = t,

            bodyBob =
                bodyMovement + breathing * 1.2f,

            armSwing =
                sin(angle * 0.5f) * 3.5f,

            legSwing = 0f,

            capeSwing = capeMovement,

            headTilt = headMovement,

            jumpHeight = 0f,

            waveAngle = 0f,

            sleepOffset = 0f
        )
    }

    // --------------------------------------------------
    // YÜRÜME
    // --------------------------------------------------

    private fun calculateWalk(
        t: Float,
        angle: Float,
        wave: Float
    ): AnimationFrame {

        val legMovement =
            sin(angle) * 30f

        val oppositeLeg =
            -legMovement

        val armMovement =
            sin(angle + PI.toFloat()) * 22f

        val bodyMovement =
            abs(sin(angle)) * 5f

        val capeMovement =
            sin(angle + 0.8f) * 14f

        val headMovement =
            sin(angle) * 2.5f

        return AnimationFrame(

            motion = Motion.WALK,

            progress = t,

            bodyBob = bodyMovement,

            armSwing = armMovement,

            legSwing =
                (legMovement + oppositeLeg) * 0.5f,

            capeSwing = capeMovement,

            headTilt = headMovement,

            jumpHeight = 0f,

            waveAngle = 0f,

            sleepOffset = 0f
        )
    }

    // --------------------------------------------------
    // KOŞMA
    // --------------------------------------------------

    private fun calculateRun(
        t: Float,
        angle: Float,
        wave: Float
    ): AnimationFrame {

        val runningLegs =
            sin(angle) * 48f

        val runningArms =
            sin(angle + PI.toFloat()) * 42f

        val bodyBounce =
            abs(sin(angle * 2f)) * 9f

        val capeMovement =
            sin(angle + 1.2f) * 27f

        val headMovement =
            sin(angle) * 5f

        val runningHeight =
            abs(sin(angle)) * 3f

        return AnimationFrame(

            motion = Motion.RUN,

            progress = t,

            bodyBob =
                bodyBounce + runningHeight,

            armSwing = runningArms,

            legSwing = runningLegs,

            capeSwing = capeMovement,

            headTilt = headMovement,

            jumpHeight = 0f,

            waveAngle = 0f,

            sleepOffset = 0f
        )
    }

    // --------------------------------------------------
    // ZIPLAMA
    // --------------------------------------------------

    private fun calculateJump(
        t: Float
    ): AnimationFrame {

        val jumpProgress =
            t.coerceIn(0f, 1f)

        val jumpCurve =
            sin(jumpProgress * PI.toFloat())

        val takeOff =
            (1f - jumpProgress).coerceIn(0f, 1f)

        val armMovement =
            jumpCurve * 38f

        val legMovement =
            jumpCurve * 20f

        val capeMovement =
            jumpCurve * 24f

        val headMovement =
            -jumpCurve * 6f

        val jumpHeight =
            jumpCurve * 58f

        return AnimationFrame(

            motion = Motion.JUMP,

            progress = t,

            bodyBob =
                jumpCurve * 3f,

            armSwing =
                armMovement,

            legSwing =
                legMovement,

            capeSwing =
                capeMovement,

            headTilt =
                headMovement,

            jumpHeight =
                jumpHeight,

            waveAngle = 0f,

            sleepOffset =
                takeOff * 0f
        )
    }

    // --------------------------------------------------
    // EL SALLAMA
    // --------------------------------------------------

    private fun calculateWave(
        t: Float,
        angle: Float
    ): AnimationFrame {

        val waveProgress =
            t.coerceIn(0f, 1f)

        val waveArm =
            sin(waveProgress * PI.toFloat() * 6f)

        val armAngle =
            waveArm * 68f

        val bodyMovement =
            sin(angle) * 2f

        val capeMovement =
            sin(angle * 1.5f) * 8f

        return AnimationFrame(

            motion = Motion.WAVE,

            progress = t,

            bodyBob =
                bodyMovement,

            armSwing = 0f,

            legSwing = 0f,

            capeSwing =
                capeMovement,

            headTilt = -3f,

            jumpHeight = 0f,

            waveAngle =
                armAngle,

            sleepOffset = 0f
        )
    }

    // --------------------------------------------------
    // UYUMA
    // --------------------------------------------------

    private fun calculateSleep(
        t: Float,
        angle: Float,
        wave: Float
    ): AnimationFrame {

        val breathing =
            sin(angle * 0.5f)

        val bodyMovement =
            breathing * 1.5f

        val headMovement =
            12f + breathing * 1.5f

        val capeMovement =
            sin(angle * 0.7f) * 2.5f

        val sleepMovement =
            sin(angle) * 3.5f

        return AnimationFrame(

            motion = Motion.SLEEP,

            progress = t,

            bodyBob =
                bodyMovement,

            armSwing = 0f,

            legSwing = 0f,

            capeSwing =
                capeMovement,

            headTilt =
                headMovement,

            jumpHeight = 0f,

            waveAngle = 0f,

            sleepOffset =
                sleepMovement
        )
    }

    // --------------------------------------------------
    // NORMAL DURUŞ
    // --------------------------------------------------

    private fun createIdleFrame(): AnimationFrame {

        return AnimationFrame(

            motion = Motion.IDLE,

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
    }

    // --------------------------------------------------
    // MEVCUT KAREYİ AL
    // --------------------------------------------------

    fun getCurrentFrame(): AnimationFrame {

        return lastFrame
    }

    // --------------------------------------------------
    // DURAKLAT
    // --------------------------------------------------

    fun pause() {

        if (
            animator?.isRunning == true &&
            !paused
        ) {

            animator?.pause()

            paused = true
        }
    }

    // --------------------------------------------------
    // DEVAM ET
    // --------------------------------------------------

    fun resume() {

        if (
            paused &&
            animator != null
        ) {

            animator?.resume()

            paused = false
        }
    }

    // --------------------------------------------------
    // DURAKLATILDI MI?
    // --------------------------------------------------

    fun isPaused(): Boolean {

        return paused
    }

    // --------------------------------------------------
    // MEVCUT HAREKET
    // --------------------------------------------------

    fun getCurrentMotion(): Motion {

        return currentMotion
    }

    // --------------------------------------------------
    // ANİMASYON ÇALIŞIYOR MU?
    // --------------------------------------------------

    fun isAnimating(): Boolean {

        return animator?.isRunning == true
    }

    // --------------------------------------------------
    // ANİMASYONU TAMAMEN DURDUR
    // --------------------------------------------------

    fun stop() {

        generation++

        stopAnimator()

        currentMotion = Motion.IDLE

        progress = 0f

        paused = false

        lastFrame = createIdleFrame()

        view.setAnimationFrame(lastFrame)
    }

    // --------------------------------------------------
    // ANİMASYON TEMİZLİĞİ
    // --------------------------------------------------

    private fun stopAnimator() {

        animator?.removeAllUpdateListeners()

        animator?.removeAllListeners()

        animator?.cancel()

        animator = null
    }
}
