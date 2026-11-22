
package com.hasankaya.livingstickman.room

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min

// ======================================================
// 1. IŞIK KAYNAĞI TÜRLERİ
// ======================================================

enum class RoomLightSourceType {
    AMBIENT,
    SUNLIGHT,
    LAMP,
    COMPUTER,
    TELEVISION,
    NETHER_PORTAL,
    CUSTOM
}

// ======================================================
// 2. IŞIK KAYNAĞI
// ======================================================

data class RoomLightSource(
    val id: String,
    val type: RoomLightSourceType,
    val position: Offset,
    val color: Color,
    val intensity: Float = 1f,
    val radius: Float = 200f,
    val isEnabled: Boolean = true,
    val castsGlow: Boolean = true
) {
    fun normalizedIntensity(): Float =
        intensity.coerceIn(0f, 1f)

    fun distanceTo(point: Offset): Float {
        val dx = point.x - position.x
        val dy = point.y - position.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    fun influenceAt(point: Offset): Float {
        if (!isEnabled || radius <= 0f) return 0f

        val distance = distanceTo(point)
        if (distance >= radius) return 0f

        val falloff = 1f - distance / radius

        return normalizedIntensity() *
            falloff * falloff
    }
}

// ======================================================
// 3. IŞIKLANDIRMA PROFİLİ
// ======================================================

data class RoomLightingProfile(
    val ambientColor: Color,
    val ambientIntensity: Float,
    val backgroundColor: Color,
    val floorColor: Color,
    val wallColor: Color
) {

    companion object {

        fun daylight() = RoomLightingProfile(
            ambientColor = Color(0xFFFFF3D6),
            ambientIntensity = 0.95f,
            backgroundColor = Color(0xFFB9D9F2),
            floorColor = Color(0xFF8B6247),
            wallColor = Color(0xFFDED5C8)
        )

        fun evening() = RoomLightingProfile(
            ambientColor = Color(0xFFFFB66B),
            ambientIntensity = 0.70f,
            backgroundColor = Color(0xFF6C668C),
            floorColor = Color(0xFF684A3A),
            wallColor = Color(0xFFB7A18D)
        )

        fun night() = RoomLightingProfile(
            ambientColor = Color(0xFF8296D8),
            ambientIntensity = 0.35f,
            backgroundColor = Color(0xFF151B38),
            floorColor = Color(0xFF352D3B),
            wallColor = Color(0xFF42465D)
        )

        fun dark() = RoomLightingProfile(
            ambientColor = Color(0xFF4B4B70),
            ambientIntensity = 0.15f,
            backgroundColor = Color(0xFF090B18),
            floorColor = Color(0xFF201D2A),
            wallColor = Color(0xFF292B3A)
        )
    }
}

// ======================================================
// 4. IŞIKLANDIRMA SONUCU
// ======================================================

data class LightingResult(
    val ambientColor: Color,
    val ambientIntensity: Float,
    val sources: List<RoomLightSource>,
    val isDark: Boolean
)

// ======================================================
// 5. ODA IŞIKLANDIRMA YÖNETİCİSİ
// ======================================================

class RoomLighting {

    private val lightSources =
        mutableListOf<RoomLightSource>()

    private var currentProfile =
        RoomLightingProfile.daylight()

    private var globalIntensity = 1f

    // --------------------------------------------------
    // BAŞLANGIÇ IŞIKLARI
    // --------------------------------------------------

    init {
        addLight(
            RoomLightSource(
                id = "ambient_light",
                type = RoomLightSourceType.AMBIENT,
                position = Offset(600f, 300f),
                color = Color(0xFFFFF3D6),
                intensity = 0.95f,
                radius = 1500f
            )
        )

        addLight(
            RoomLightSource(
                id = "sunlight",
                type = RoomLightSourceType.SUNLIGHT,
                position = Offset(900f, 80f),
                color = Color(0xFFFFE4A8),
                intensity = 0.65f,
                radius = 650f
            )
        )

        addLight(
            RoomLightSource(
                id = "room_lamp",
                type = RoomLightSourceType.LAMP,
                position = Offset(590f, 100f),
                color = Color(0xFFFFC76A),
                intensity = 0.80f,
                radius = 350f,
                isEnabled = false
            )
        )

        addLight(
            RoomLightSource(
                id = "computer_light",
                type = RoomLightSourceType.COMPUTER,
                position = Offset(790f, 400f),
                color = Color(0xFF55BFFF),
                intensity = 0.70f,
                radius = 180f,
                isEnabled = false
            )
        )

        addLight(
            RoomLightSource(
                id = "television_light",
                type = RoomLightSourceType.TELEVISION,
                position = Offset(520f, 240f),
                color = Color(0xFF75A9FF),
                intensity = 0.65f,
                radius = 260f,
                isEnabled = false
            )
        )

        addLight(
            RoomLightSource(
                id = "nether_portal_light",
                type = RoomLightSourceType.NETHER_PORTAL,
                position = Offset(1105f, 410f),
                color = Color(0xFF9B38FF),
                intensity = 0.95f,
                radius = 320f,
                isEnabled = false
            )
        )
    }

    // ==================================================
    // 6. IŞIK EKLE
    // ==================================================

    fun addLight(source: RoomLightSource) {

        lightSources.removeAll {
            it.id == source.id
        }

        lightSources.add(source)
    }

    // ==================================================
    // 7. IŞIK SİL
    // ==================================================

    fun removeLight(id: String): Boolean {

        return lightSources.removeAll {
            it.id == id
        }
    }

    // ==================================================
    // 8. IŞIK BUL
    // ==================================================

    fun getLight(id: String): RoomLightSource? {

        return lightSources.firstOrNull {
            it.id == id
        }
    }

    // ==================================================
    // 9. IŞIK AÇ / KAPAT
    // ==================================================

    fun setLightEnabled(
        id: String,
        enabled: Boolean
    ) {

        val index = lightSources.indexOfFirst {
            it.id == id
        }

        if (index == -1) return

        lightSources[index] =
            lightSources[index].copy(
                isEnabled = enabled
            )
    }

    // ==================================================
    // 10. IŞIK YOĞUNLUĞU
    // ==================================================

    fun setLightIntensity(
        id: String,
        intensity: Float
    ) {

        val index = lightSources.indexOfFirst {
            it.id == id
        }

        if (index == -1) return

        lightSources[index] =
            lightSources[index].copy(
                intensity = intensity.coerceIn(0f, 1f)
            )
    }

    // ==================================================
    // 11. IŞIK KONUMU
    // ==================================================

    fun moveLight(
        id: String,
        position: Offset
    ) {

        val index = lightSources.indexOfFirst {
            it.id == id
        }

        if (index == -1) return

        lightSources[index] =
            lightSources[index].copy(
                position = position
            )
    }

    // ==================================================
    // 12. IŞIK RENGİ
    // ==================================================

    fun setLightColor(
        id: String,
        color: Color
    ) {

        val index = lightSources.indexOfFirst {
            it.id == id
        }

        if (index == -1) return

        lightSources[index] =
            lightSources[index].copy(
                color = color
            )
    }

    // ==================================================
    // 13. IŞIK YARIÇAPI
    // ==================================================

    fun setLightRadius(
        id: String,
        radius: Float
    ) {

        val index = lightSources.indexOfFirst {
            it.id == id
        }

        if (index == -1) return

        lightSources[index] =
            lightSources[index].copy(
                radius = radius.coerceAtLeast(0f)
            )
    }

    // ==================================================
    // 14. TÜM IŞIKLARI AL
    // ==================================================

    fun getAllLights(): List<RoomLightSource> {

        return lightSources.toList()
    }

    // ==================================================
    // 15. AKTİF IŞIKLARI AL
    // ==================================================

    fun getActiveLights(): List<RoomLightSource> {

        return lightSources.filter {
            it.isEnabled
        }
    }

    // ==================================================
    // 16. GÜNDÜZ
    // ==================================================

    fun setDaylight() {

        currentProfile =
            RoomLightingProfile.daylight()

        globalIntensity = 1f

        setLightEnabled("sunlight", true)
        setLightEnabled("room_lamp", false)
    }

    // ==================================================
    // 17. AKŞAM
    // ==================================================

    fun setEvening() {

        currentProfile =
            RoomLightingProfile.evening()

        globalIntensity = 1f

        setLightEnabled("sunlight", true)
    }

    // ==================================================
    // 18. GECE
    // ==================================================

    fun setNight() {

        currentProfile =
            RoomLightingProfile.night()

        globalIntensity = 1f

        setLightEnabled("sunlight", false)
        setLightEnabled("room_lamp", true)
    }

    // ==================================================
    // 19. KARANLIK
    // ==================================================

    fun setDark() {

        currentProfile =
            RoomLightingProfile.dark()

        globalIntensity = 1f

        setLightEnabled("sunlight", false)
        setLightEnabled("room_lamp", false)
    }

    // ==================================================
    // 20. GENEL IŞIK YOĞUNLUĞU
    // ==================================================

    fun setGlobalIntensity(
        intensity: Float
    ) {

        globalIntensity =
            intensity.coerceIn(0f, 1f)
    }

    // ==================================================
    // 21. LAMBA
    // ==================================================

    fun turnOnLamp() {

        setLightEnabled("room_lamp", true)
    }

    fun turnOffLamp() {

        setLightEnabled("room_lamp", false)
    }

    // ==================================================
    // 22. BİLGİSAYAR IŞIĞI
    // ==================================================

    fun turnOnComputerLight() {

        setLightEnabled("computer_light", true)
    }

    fun turnOffComputerLight() {

        setLightEnabled("computer_light", false)
    }

    // ==================================================
    // 23. TELEVİZYON IŞIĞI
    // ==================================================

    fun turnOnTelevisionLight() {

        setLightEnabled("television_light", true)
    }

    fun turnOffTelevisionLight() {

        setLightEnabled("television_light", false)
    }

    // ==================================================
    // 24. NETHER PORTAL IŞIĞI
    // ==================================================

    fun turnOnPortalLight() {

        setLightEnabled("nether_portal_light", true)
    }

    fun turnOffPortalLight() {

        setLightEnabled("nether_portal_light", false)
    }

    // ==================================================
    // 25. PORTAL IŞIĞINI DALGALANDIR
    // ==================================================

    fun updatePortalPulse(
        pulse: Float
    ) {

        setLightIntensity(
            "nether_portal_light",
            pulse.coerceIn(0f, 1f)
        )
    }

    // ==================================================
    // 26. BİR NOKTADAKİ IŞIK ETKİSİ
    // ==================================================

    fun calculateLightAt(
        position: Offset
    ): Float {

        var total = 0f

        for (source in lightSources) {

            total += source.influenceAt(position)
        }

        return min(
            1f,
            max(
                0f,
                total * globalIntensity
            )
        )
    }

    // ==================================================
    // 27. BİR NOKTANIN IŞIK RENGİ
    // ==================================================

    fun calculateLightColorAt(
        position: Offset
    ): Color {

        var red = currentProfile.ambientColor.red *
            currentProfile.ambientIntensity

        var green = currentProfile.ambientColor.green *
            currentProfile.ambientIntensity

        var blue = currentProfile.ambientColor.blue *
            currentProfile.ambientIntensity

        var totalWeight =
            currentProfile.ambientIntensity

        for (source in lightSources) {

            val weight =
                source.influenceAt(position)

            if (weight <= 0f) continue

            red += source.color.red * weight
            green += source.color.green * weight
            blue += source.color.blue * weight

            totalWeight += weight
        }

        if (totalWeight <= 0f) {
            return Color.Black
        }

        return Color(
            red = (red / totalWeight).coerceIn(0f, 1f),
            green = (green / totalWeight).coerceIn(0f, 1f),
            blue = (blue / totalWeight).coerceIn(0f, 1f),
            alpha = 1f
        )
    }

    // ==================================================
    // 28. IŞIKLANDIRMA SONUCUNU AL
    // ==================================================

    fun getLightingResult(): LightingResult {

        return LightingResult(
            ambientColor = currentProfile.ambientColor,
            ambientIntensity =
                currentProfile.ambientIntensity *
                    globalIntensity,
            sources = getActiveLights(),
            isDark = currentProfile.ambientIntensity < 0.3f
        )
    }

    // ==================================================
    // 29. PROFİLİ AL
    // ==================================================

    fun getCurrentProfile(): RoomLightingProfile {

        return currentProfile
    }

    // ==================================================
    // 30. IŞIKLARI SIFIRLA
    // ==================================================

    fun reset() {

        lightSources.clear()

        currentProfile =
            RoomLightingProfile.daylight()

        globalIntensity = 1f

        // Başlangıç ışıklarını yeniden oluştur.
        addLight(
            RoomLightSource(
                id = "ambient_light",
                type = RoomLightSourceType.AMBIENT,
                position = Offset(600f, 300f),
                color = Color(0xFFFFF3D6),
                intensity = 0.95f,
                radius = 1500f
            )
        )

        addLight(
            RoomLightSource(
                id = "sunlight",
                type = RoomLightSourceType.SUNLIGHT,
                position = Offset(900f, 80f),
                color = Color(0xFFFFE4A8),
                intensity = 0.65f,
                radius = 650f
            )
        )

        addLight(
            RoomLightSource(
                id = "room_lamp",
                type = RoomLightSourceType.LAMP,
                position = Offset(590f, 100f),
                color = Color(0xFFFFC76A),
                intensity = 0.80f,
                radius = 350f,
                isEnabled = false
            )
        )

        addLight(
            RoomLightSource(
                id = "computer_light",
                type = RoomLightSourceType.COMPUTER,
                position = Offset(790f, 400f),
                color = Color(0xFF55BFFF),
                intensity = 0.70f,
                radius = 180f,
                isEnabled = false
            )
        )

        addLight(
            RoomLightSource(
                id = "television_light",
                type = RoomLightSourceType.TELEVISION,
                position = Offset(520f, 240f),
                color = Color(0xFF75A9FF),
                intensity = 0.65f,
                radius = 260f,
                isEnabled = false
            )
        )

        addLight(
            RoomLightSource(
                id = "nether_portal_light",
                type = RoomLightSourceType.NETHER_PORTAL,
                position = Offset(1105f, 410f),
                color = Color(0xFF9B38FF),
                intensity = 0.95f,
                radius = 320f,
                isEnabled = false
            )
        )
    }
}
