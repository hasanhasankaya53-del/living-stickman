
package com.hasankaya.livingstickman.room

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

// ======================================================
// 1. ODA NESNESİ TÜRLERİ
// ======================================================

enum class RoomObjectType {

    BED,
    SOFA,
    COMPUTER,
    COMPUTER_DESK,
    TELEVISION,
    TV_STAND,
    WINDOW,
    NETHER_PORTAL,
    DOOR,
    BOOKSHELF,
    LAMP,
    CARPET,
    TABLE,
    CHAIR,
    PLANT,
    WALL_DECORATION,
    FLOOR_DECORATION,
    CUSTOM
}

// ======================================================
// 2. NESNE ETKİLEŞİM TÜRLERİ
// ======================================================

enum class RoomInteractionType {

    SLEEP,
    SIT,
    USE_COMPUTER,
    WATCH_TV,
    LOOK_OUTSIDE,
    ENTER_PORTAL,
    OPEN_DOOR,
    READ_BOOK,
    TURN_ON_LIGHT,
    TURN_OFF_LIGHT,
    WALK_TO,
    NONE
}

// ======================================================
// 3. NESNE DURUMU
// ======================================================

enum class RoomObjectState {

    NORMAL,
    ACTIVE,
    IN_USE,
    OPEN,
    CLOSED,
    ON,
    OFF,
    ANIMATING,
    DISABLED
}

// ======================================================
// 4. NESNE ETKİLEŞİM SONUCU
// ======================================================

enum class InteractionResult {

    SUCCESS,
    DISABLED,
    NOT_VISIBLE,
    OUT_OF_RANGE,
    NOT_INTERACTABLE,
    ALREADY_IN_USE,
    FAILED
}

// ======================================================
// 5. NESNE ANİMASYON TÜRÜ
// ======================================================

enum class RoomObjectAnimation {

    NONE,
    GLOW,
    PULSE,
    ROTATE,
    OPEN,
    CLOSE,
    SHAKE,
    FLOAT,
    PARTICLES
}

// ======================================================
// 6. ODA NESNESİ ANA VERİ SINIFI
// ======================================================

data class RoomObject(

    // Nesnenin benzersiz kimliği.
    val id: String,

    // Nesnenin ekranda kullanılacak adı.
    val name: String,

    // Nesnenin türü.
    val type: RoomObjectType,

    // Nesneye dokunulduğunda yapılacak işlem.
    val interaction: RoomInteractionType,

    // Nesnenin oda içindeki konumu.
    val position: Offset,

    // Nesnenin genişliği ve yüksekliği.
    val size: Size,

    // Nesnenin aktif olup olmadığı.
    val isEnabled: Boolean = true,

    // Nesnenin görünür olup olmadığı.
    val isVisible: Boolean = true,

    // Kullanıcı veya karakter nesneyle etkileşebilir mi?
    val isInteractable: Boolean = true,

    // Nesnenin başka nesnelerin önünde veya arkasında
    // çizilmesini belirleyen katman.
    val layer: Int = 0,

    // Nesnenin mevcut durumu.
    val state: RoomObjectState = RoomObjectState.NORMAL,

    // Nesnenin animasyon türü.
    val animation: RoomObjectAnimation =
        RoomObjectAnimation.NONE,

    // Karakterin nesneye yaklaşabileceği mesafe.
    val interactionRange: Float = 100f,

    // Nesnenin karakter tarafından kullanılıp kullanılmadığı.
    val isOccupied: Boolean = false,

    // Nesnenin açıklaması.
    val description: String = "",

    // İsteğe bağlı özel etiket.
    val tags: Set<String> = emptySet()

) {

    // ==================================================
    // 7. NESNENİN SINIRLARINI HESAPLA
    // ==================================================

    val left: Float
        get() = position.x

    val top: Float
        get() = position.y

    val right: Float
        get() = position.x + size.width

    val bottom: Float
        get() = position.y + size.height

    val center: Offset
        get() = Offset(
            x = position.x + size.width / 2f,
            y = position.y + size.height / 2f
        )

    // ==================================================
    // 8. NOKTA NESNENİN İÇİNDE Mİ?
    // ==================================================

    fun contains(point: Offset): Boolean {

        if (!isVisible || !isEnabled) {
            return false
        }

        return point.x >= left &&
            point.x <= right &&
            point.y >= top &&
            point.y <= bottom
    }

    // ==================================================
    // 9. NESNE ETKİLEŞİME AÇIK MI?
    // ==================================================

    fun canInteract(): Boolean {

        return isEnabled &&
            isVisible &&
            isInteractable &&
            state != RoomObjectState.DISABLED &&
            !isOccupied &&
            interaction != RoomInteractionType.NONE
    }

    // ==================================================
    // 10. İKİ NOKTA ARASINDAKİ MESAFE
    // ==================================================

    fun distanceTo(point: Offset): Float {

        val dx = center.x - point.x

        val dy = center.y - point.y

        return kotlin.math.sqrt(
            dx * dx + dy * dy
        )
    }

    // ==================================================
    // 11. KARAKTER NESNEYE YAKIN MI?
    // ==================================================

    fun isInRange(characterPosition: Offset): Boolean {

        return distanceTo(characterPosition) <=
            interactionRange
    }

    // ==================================================
    // 12. NESNEYE ETKİLEŞİM UYGULA
    // ==================================================

    fun interact(): InteractionResult {

        if (!isVisible) {
            return InteractionResult.NOT_VISIBLE
        }

        if (!isEnabled ||
            state == RoomObjectState.DISABLED
        ) {
            return InteractionResult.DISABLED
        }

        if (!isInteractable ||
            interaction == RoomInteractionType.NONE
        ) {
            return InteractionResult.NOT_INTERACTABLE
        }

        if (isOccupied) {
            return InteractionResult.ALREADY_IN_USE
        }

        return InteractionResult.SUCCESS
    }

    // ==================================================
    // 13. NESNEYİ GÜNCELLEME FONKSİYONLARI
    // ==================================================

    fun setVisible(visible: Boolean): RoomObject {

        return copy(
            isVisible = visible
        )
    }

    fun setEnabled(enabled: Boolean): RoomObject {

        return copy(
            isEnabled = enabled
        )
    }

    fun setOccupied(occupied: Boolean): RoomObject {

        return copy(
            isOccupied = occupied
        )
    }

    fun setState(
        newState: RoomObjectState
    ): RoomObject {

        return copy(
            state = newState
        )
    }

    fun setPosition(
        newPosition: Offset
    ): RoomObject {

        return copy(
            position = newPosition
        )
    }

    fun setSize(
        newSize: Size
    ): RoomObject {

        return copy(
            size = newSize
        )
    }

    fun moveBy(
        x: Float,
        y: Float
    ): RoomObject {

        return copy(
            position = Offset(
                position.x + x,
                position.y + y
            )
        )
    }

    // ==================================================
    // 14. NESNE TÜRÜNE GÖRE KONTROLLER
    // ==================================================

    fun isBed(): Boolean =
        type == RoomObjectType.BED

    fun isSofa(): Boolean =
        type == RoomObjectType.SOFA

    fun isComputer(): Boolean =
        type == RoomObjectType.COMPUTER

    fun isTelevision(): Boolean =
        type == RoomObjectType.TELEVISION

    fun isPortal(): Boolean =
        type == RoomObjectType.NETHER_PORTAL

    fun isWindow(): Boolean =
        type == RoomObjectType.WINDOW

    // ==================================================
    // 15. NESNE ETKİLEŞİMİNİ KONTROL ET
    // ==================================================

    fun supportsInteraction(
        requestedInteraction: RoomInteractionType
    ): Boolean {

        return canInteract() &&
            interaction == requestedInteraction
    }
}
