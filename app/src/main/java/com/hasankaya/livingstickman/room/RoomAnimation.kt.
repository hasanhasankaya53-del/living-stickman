
package com.hasankaya.livingstickman.room

import android.os.SystemClock
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// ======================================================
// 1. ANİMASYON TÜRLERİ
// ======================================================

enum class RoomAnimationType {
    NONE,
    FLOAT,
    PULSE,
    ROTATE,
    GLOW,
    SHAKE,
    FADE,
    BOUNCE,
    PARTICLES,
    PORTAL,
    LIGHT_FLICKER,
    WINDOW_BREEZE,
    CURTAIN_WAVE
}

// ======================================================
// 2. ANİMASYON DURUMU
// ======================================================

enum class RoomAnimationStatus {
    STOPPED,
    PLAYING,
    PAUSED,
    COMPLETED
}

// ======================================================
// 3. ANİMASYON KARE VERİSİ
// ======================================================

data class RoomAnimationFrame(

    val type: RoomAnimationType,

    val progress: Float,

    val offsetX: Float = 0f,

    val offsetY: Float = 0f,

    val rotation: Float = 0f,

    val scale: Float = 1f,

    val alpha: Float = 1f,

    val glow: Float = 0f,

    val brightness: Float = 1f,

    val particleIntensity: Float = 0f,

    val colorShift: Float = 0f
)

// ======================================================
// 4. ANİMASYON AYARLARI
// ======================================================

data class RoomAnimationConfig(

    val durationMillis: Long = 2000L,

    val repeat: Boolean = true,

    val reverse: Boolean = false,

    val intensity: Float = 1f,

    val enabled: Boolean = true
) {

    fun validated(): RoomAnimationConfig {

        return copy(
            durationMillis = durationMillis.coerceAtLeast(1L),
            intensity = intensity.coerceIn(0f, 2f)
        )
    }
}

// ======================================================
// 5. ANİMASYON YÖNETİCİSİ
// ======================================================

class RoomAnimation {

    private data class AnimationEntry(
        val id: String,
        val type: RoomAnimationType,
        val config: RoomAnimationConfig,
        val startTime: Long,
        var pausedAt: Long? = null,
        var elapsedBeforePause: Long = 0L,
        var status: RoomAnimationStatus =
            RoomAnimationStatus.PLAYING
    )

    private val animations =
        mutableMapOf<String, AnimationEntry>()

    private var globalPaused = false

    private var globalPauseTime = 0L

    // ==================================================
    // 6. ANİMASYON BAŞLAT
    // ==================================================

    fun start(
        id: String,
        type: RoomAnimationType,
        config: RoomAnimationConfig =
            RoomAnimationConfig()
    ) {

        if (type == RoomAnimationType.NONE) return

        val validatedConfig = config.validated()

        if (!validatedConfig.enabled) return

        animations[id] = AnimationEntry(
            id = id,
            type = type,
            config = validatedConfig,
            startTime = SystemClock.uptimeMillis()
        )
    }

    // ==================================================
    // 7. ANİMASYON DURDUR
    // ==================================================

    fun stop(id: String) {

        animations.remove(id)
    }

    // ==================================================
    // 8. TÜM ANİMASYONLARI DURDUR
    // ==================================================

    fun stopAll() {

        animations.clear()
    }

    // ==================================================
    // 9. ANİMASYON DURUMUNU AL
    // ==================================================

    fun getStatus(
        id: String
    ): RoomAnimationStatus? {

        return animations[id]?.status
    }

    // ==================================================
    // 10. ANİMASYON ÇALIŞIYOR MU?
    // ==================================================

    fun isRunning(id: String): Boolean {

        return animations[id]?.status ==
            RoomAnimationStatus.PLAYING
    }

    // ==================================================
    // 11. ANİMASYONU DURAKLAT
    // ==================================================

    fun pause(id: String) {

        val entry = animations[id] ?: return

        if (entry.status != RoomAnimationStatus.PLAYING) {
            return
        }

        val now = SystemClock.uptimeMillis()

        entry.elapsedBeforePause =
            elapsedTime(entry, now)

        entry.pausedAt = now
        entry.status = RoomAnimationStatus.PAUSED
    }

    // ==================================================
    // 12. ANİMASYONU DEVAM ETTİR
    // ==================================================

    fun resume(id: String) {

        val entry = animations[id] ?: return

        if (entry.status != RoomAnimationStatus.PAUSED) {
            return
        }

        val now = SystemClock.uptimeMillis()

        entry.pausedAt = null
        entry.status = RoomAnimationStatus.PLAYING
    }

    // ==================================================
    // 13. TÜM ANİMASYONLARI DURAKLAT
    // ==================================================

    fun pauseAll() {

        if (globalPaused) return

        globalPaused = true
        globalPauseTime = SystemClock.uptimeMillis()

        animations.values.forEach { entry ->

            if (entry.status == RoomAnimationStatus.PLAYING) {

                entry.elapsedBeforePause =
                    elapsedTime(entry, globalPauseTime)

                entry.pausedAt = globalPauseTime
                entry.status = RoomAnimationStatus.PAUSED
            }
        }
    }

    // ==================================================
    // 14. TÜM ANİMASYONLARI DEVAM ETTİR
    // ==================================================

    fun resumeAll() {

        if (!globalPaused) return

        val now = SystemClock.uptimeMillis()

        animations.values.forEach { entry ->

            if (entry.status == RoomAnimationStatus.PAUSED) {

                entry.pausedAt = null
                entry.status = RoomAnimationStatus.PLAYING
            }
        }

        globalPaused = false
    }

    // ==================================================
    // 15. GEÇEN SÜREYİ HESAPLA
    // ==================================================

    private fun elapsedTime(
        entry: AnimationEntry,
        now: Long
    ): Long {

        val pausedAt = entry.pausedAt

        return if (pausedAt != null) {
            entry.elapsedBeforePause
        } else {
            entry.elapsedBeforePause +
                (now - entry.startTime)
        }
    }

    // ==================================================
    // 16. ANİMASYON İLERLEMESİ
    // ==================================================

    private fun calculateProgress(
        entry: AnimationEntry,
        now: Long
    ): Float {

        val duration =
            entry.config.durationMillis.coerceAtLeast(1L)

        val elapsed =
            elapsedTime(entry, now)

        if (!entry.config.repeat && elapsed >= duration) {
            return 1f
        }

        val rawProgress =
            (elapsed % duration).toFloat() / duration

        if (entry.config.reverse) {
            val cycle = (elapsed / duration) % 2L

            return if (cycle == 1L) {
                1f - rawProgress
            } else {
                rawProgress
            }
        }

        return rawProgress
    }

    // ==================================================
    // 17. ANİMASYON KARESİ ÜRET
    // ==================================================

    fun getFrame(
        id: String
    ): RoomAnimationFrame? {

        val entry = animations[id] ?: return null

        val now = SystemClock.uptimeMillis()

        val progress =
            calculateProgress(entry, now)

        if (
            !entry.config.repeat &&
            elapsedTime(entry, now) >=
                entry.config.durationMillis
        ) {
            entry.status = RoomAnimationStatus.COMPLETED
        }

        return calculateFrame(
            entry.type,
            progress,
            entry.config.intensity
        )
    }

    // ==================================================
    // 18. TÜM ANİMASYON KARELERİ
    // ==================================================

    fun getAllFrames(): Map<String, RoomAnimationFrame> {

        val result =
            mutableMapOf<String, RoomAnimationFrame>()

        val ids = animations.keys.toList()

        ids.forEach { id ->

            getFrame(id)?.let { frame ->
                result[id] = frame
            }
        }

        return result
    }

    // ==================================================
    // 19. ANİMASYON HESAPLAMA
    // ==================================================

    private fun calculateFrame(
        type: RoomAnimationType,
        progress: Float,
        intensity: Float
    ): RoomAnimationFrame {

        val angle =
            progress * 2.0 * PI

        val wave =
            sin(angle).toFloat()

        val smoothWave =
            (1f - cos(angle).toFloat()) / 2f

        val power =
            intensity.coerceIn(0f, 2f)

        return when (type) {

            RoomAnimationType.NONE -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress
                )
            }

            RoomAnimationType.FLOAT -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    offsetY = wave * 8f * power
                )
            }

            RoomAnimationType.PULSE -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    scale = 1f + wave * 0.08f * power
                )
            }

            RoomAnimationType.ROTATE -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    rotation = progress * 360f
                )
            }

            RoomAnimationType.GLOW -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    glow = smoothWave * power
                )
            }

            RoomAnimationType.SHAKE -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    offsetX = wave * 5f * power
                )
            }

            RoomAnimationType.FADE -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    alpha = 0.35f + smoothWave * 0.65f
                )
            }

            RoomAnimationType.BOUNCE -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    offsetY = -abs(wave) * 15f * power
                )
            }

            RoomAnimationType.PARTICLES -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    particleIntensity = smoothWave * power
                )
            }

            RoomAnimationType.PORTAL -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    rotation = progress * 360f,
                    glow = 0.55f + smoothWave * 0.45f,
                    scale = 1f + wave * 0.025f,
                    particleIntensity = smoothWave * power,
                    colorShift = progress
                )
            }

            RoomAnimationType.LIGHT_FLICKER -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    brightness = 0.85f +
                        abs(wave) * 0.15f * power
                )
            }

            RoomAnimationType.WINDOW_BREEZE -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    offsetX = wave * 2f * power,
                    offsetY = smoothWave * 3f * power
                )
            }

            RoomAnimationType.CURTAIN_WAVE -> {
                RoomAnimationFrame(
                    type = type,
                    progress = progress,
                    rotation = wave * 4f * power,
                    offsetX = wave * 3f * power
                )
            }
        }
    }

    // ==================================================
    // 20. ANİMASYON SAYISI
    // ==================================================

    fun getAnimationCount(): Int {

        return animations.size
    }

    // ==================================================
    // 21. TÜM ANİMASYONLAR DURDU MU?
    // ==================================================

    fun areAllStopped(): Boolean {

        return animations.values.none {
            it.status == RoomAnimationStatus.PLAYING
        }
    }

    // ==================================================
    // 22. GENEL DURAKLATMA DURUMU
    // ==================================================

    fun isGloballyPaused(): Boolean {

        return globalPaused
    }

    // ==================================================
    // 23. SİSTEMİ SIFIRLA
    // ==================================================

    fun reset() {

        animations.clear()

        globalPaused = false
        globalPauseTime = 0L
    }
}
