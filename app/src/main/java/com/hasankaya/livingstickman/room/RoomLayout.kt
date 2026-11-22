
package com.hasankaya.livingstickman.room

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/**
 * Living Stickman odasının bütün yerleşim bilgilerini yönetir.
 *
 * Bu sınıf:
 * - Odanın boyutlarını belirler.
 * - Duvar ve zemin ölçülerini tutar.
 * - Mobilyaların konumlarını belirler.
 * - Karakterin hareket alanını tanımlar.
 * - Nesnelerin çakışma kontrollerini sağlar.
 * - Oda koordinatlarını ekran koordinatlarına dönüştürür.
 */

class RoomLayout(

    val roomWidth: Float = 1200f,

    val roomHeight: Float = 800f

) {

    // ==================================================
    // 1. ODA TEMEL ÖLÇÜLERİ
    // ==================================================

    val roomSize: Size
        get() = Size(
            roomWidth,
            roomHeight
        )

    val roomCenter: Offset
        get() = Offset(
            roomWidth / 2f,
            roomHeight / 2f
        )

    // ==================================================
    // 2. DUVAR VE ZEMİN ÖLÇÜLERİ
    // ==================================================

    val wallHeight: Float = 560f

    val floorHeight: Float = 240f

    val floorTop: Float
        get() = roomHeight - floorHeight

    val floorBottom: Float
        get() = roomHeight

    val ceilingHeight: Float
        get() = 0f

    val leftWall: Float = 0f

    val rightWall: Float
        get() = roomWidth

    // ==================================================
    // 3. ODA ALANLARI
    // ==================================================

    val wallBounds: RoomBounds
        get() = RoomBounds(
            left = 0f,
            top = 0f,
            right = roomWidth,
            bottom = floorTop
        )

    val floorBounds: RoomBounds
        get() = RoomBounds(
            left = 0f,
            top = floorTop,
            right = roomWidth,
            bottom = roomHeight
        )

    val fullRoomBounds: RoomBounds
        get() = RoomBounds(
            left = 0f,
            top = 0f,
            right = roomWidth,
            bottom = roomHeight
        )

    // ==================================================
    // 4. ODA NESNELERİ
    // ==================================================

    val objects: List<RoomObject> = listOf(

        // ------------------------------
        // YATAK
        // ------------------------------

        RoomObject(
            id = "main_bed",
            name = "Yatak",
            type = RoomObjectType.BED,
            interaction = RoomInteractionType.SLEEP,
            position = Offset(80f, 390f),
            size = Size(250f, 150f),
            layer = 2,
            interactionRange = 115f,
            description = "Karakterin dinlenebileceği yatak."
        ),

        // ------------------------------
        // KOLTUK
        // ------------------------------

        RoomObject(
            id = "main_sofa",
            name = "Koltuk",
            type = RoomObjectType.SOFA,
            interaction = RoomInteractionType.SIT,
            position = Offset(390f, 440f),
            size = Size(230f, 110f),
            layer = 2,
            interactionRange = 110f,
            description = "Karakterin oturabileceği koltuk."
        ),

        // ------------------------------
        // BİLGİSAYAR MASASI
        // ------------------------------

        RoomObject(
            id = "computer_desk",
            name = "Bilgisayar Masası",
            type = RoomObjectType.COMPUTER_DESK,
            interaction = RoomInteractionType.USE_COMPUTER,
            position = Offset(690f, 430f),
            size = Size(220f, 120f),
            layer = 2,
            interactionRange = 120f,
            description = "Bilgisayarın bulunduğu çalışma masası."
        ),

        // ------------------------------
        // BİLGİSAYAR
        // ------------------------------

        RoomObject(
            id = "main_computer",
            name = "Bilgisayar",
            type = RoomObjectType.COMPUTER,
            interaction = RoomInteractionType.USE_COMPUTER,
            position = Offset(740f, 360f),
            size = Size(100f, 80f),
            layer = 3,
            interactionRange = 100f,
            description = "Karakterin kullanabileceği bilgisayar."
        ),

        // ------------------------------
        // TELEVİZYON
        // ------------------------------

        RoomObject(
            id = "main_tv",
            name = "Televizyon",
            type = RoomObjectType.TELEVISION,
            interaction = RoomInteractionType.WATCH_TV,
            position = Offset(430f, 180f),
            size = Size(190f, 120f),
            layer = 3,
            interactionRange = 150f,
            description = "Karakterin izleyebileceği televizyon."
        ),

        // ------------------------------
        // TELEVİZYON SEHPASI
        // ------------------------------

        RoomObject(
            id = "tv_stand",
            name = "Televizyon Sehpası",
            type = RoomObjectType.TV_STAND,
            interaction = RoomInteractionType.NONE,
            position = Offset(410f, 280f),
            size = Size(230f, 90f),
            layer = 2,
            isInteractable = false
        ),

        // ------------------------------
        // PENCERE
        // ------------------------------

        RoomObject(
            id = "main_window",
            name = "Büyük Pencere",
            type = RoomObjectType.WINDOW,
            interaction = RoomInteractionType.LOOK_OUTSIDE,
            position = Offset(700f, 60f),
            size = Size(330f, 230f),
            layer = 1,
            interactionRange = 130f,
            description = "Dışarıyı gösteren büyük pencere."
        ),

        // ------------------------------
        // NETHER PORTALI
        // ------------------------------

        RoomObject(
            id = "nether_portal",
            name = "Nether Portalı",
            type = RoomObjectType.NETHER_PORTAL,
            interaction = RoomInteractionType.ENTER_PORTAL,
            position = Offset(1050f, 290f),
            size = Size(110f, 240f),
            layer = 3,
            animation = RoomObjectAnimation.GLOW,
            interactionRange = 130f,
            description = "Minecraft Nether dünyasına açılan portal."
        ),

        // ------------------------------
        // KAPI
        // ------------------------------

        RoomObject(
            id = "main_door",
            name = "Oda Kapısı",
            type = RoomObjectType.DOOR,
            interaction = RoomInteractionType.OPEN_DOOR,
            position = Offset(40f, 170f),
            size = Size(100f, 220f),
            layer = 2,
            interactionRange = 100f
        ),

        // ------------------------------
        // LAMBA
        // ------------------------------

        RoomObject(
            id = "room_lamp",
            name = "Oda Lambası",
            type = RoomObjectType.LAMP,
            interaction = RoomInteractionType.TURN_ON_LIGHT,
            position = Offset(550f, 30f),
            size = Size(80f, 80f),
            layer = 3,
            animation = RoomObjectAnimation.GLOW,
            interactionRange = 100f
        ),

        // ------------------------------
        // KİTAPLIK
        // ------------------------------

        RoomObject(
            id = "bookshelf",
            name = "Kitaplık",
            type = RoomObjectType.BOOKSHELF,
            interaction = RoomInteractionType.READ_BOOK,
            position = Offset(180f, 130f),
            size = Size(150f, 230f),
            layer = 2,
            interactionRange = 100f
        ),

        // ------------------------------
        // BİTKİ
        // ------------------------------

        RoomObject(
            id = "room_plant",
            name = "Saksı Bitkisi",
            type = RoomObjectType.PLANT,
            interaction = RoomInteractionType.NONE,
            position = Offset(950f, 460f),
            size = Size(75f, 90f),
            layer = 2,
            isInteractable = false
        ),

        // ------------------------------
        // HALI
        // ------------------------------

        RoomObject(
            id = "room_carpet",
            name = "Oda Halısı",
            type = RoomObjectType.CARPET,
            interaction = RoomInteractionType.NONE,
            position = Offset(350f, 570f),
            size = Size(400f, 100f),
            layer = 0,
            isInteractable = false
        )
    )

    // ==================================================
    // 5. KARAKTERİN HAREKET ALANI
    // ==================================================

    val characterMovementBounds: RoomBounds
        get() = RoomBounds(
            left = 30f,
            top = floorTop - 100f,
            right = roomWidth - 30f,
            bottom = roomHeight - 60f
        )

    // ==================================================
    // 6. KARAKTERİN BAŞLANGIÇ KONUMU
    // ==================================================

    val characterStartPosition: Offset =
        Offset(
            x = 580f,
            y = floorTop - 90f
        )

    // ==================================================
    // 7. NESNE BULMA
    // ==================================================

    fun getObjectById(
        id: String
    ): RoomObject? {

        return objects.firstOrNull {
            it.id == id
        }
    }

    // ==================================================
    // 8. NESNE TÜRÜNE GÖRE BULMA
    // ==================================================

    fun getObjectsByType(
        type: RoomObjectType
    ): List<RoomObject> {

        return objects.filter {
            it.type == type
        }
    }

    // ==================================================
    // 9. NOKTANIN ALTINDAKİ NESNEYİ BULMA
    // ==================================================

    fun getObjectAt(
        point: Offset
    ): RoomObject? {

        return objects
            .asSequence()
            .filter {
                it.contains(point)
            }
            .sortedByDescending {
                it.layer
            }
            .firstOrNull()
    }

    // ==================================================
    // 10. ETKİLEŞİLEBİLİR NESNELER
    // ==================================================

    fun getInteractableObjects(): List<RoomObject> {

        return objects.filter {
            it.canInteract()
        }
    }

    // ==================================================
    // 11. EN YAKIN NESNEYİ BUL
    // ==================================================

    fun getNearestObject(
        position: Offset
    ): RoomObject? {

        return objects
            .filter {
                it.canInteract()
            }
            .minByOrNull {
                it.distanceTo(position)
            }
    }

    // ==================================================
    // 12. EN YAKIN ETKİLEŞİLEBİLİR NESNEYİ BUL
    // ==================================================

    fun getNearestInteractableObject(
        position: Offset
    ): RoomObject? {

        return objects
            .filter {
                it.canInteract() &&
                    it.isInRange(position)
            }
            .minByOrNull {
                it.distanceTo(position)
            }
    }

    // ==================================================
    // 13. KARAKTERİN HAREKET ALANINDA OLUP OLMADIĞI
    // ==================================================

    fun isInsideMovementArea(
        position: Offset
    ): Boolean {

        return characterMovementBounds.contains(position)
    }

    // ==================================================
    // 14. KONUMU HAREKET ALANINDA TUT
    // ==================================================

    fun clampCharacterPosition(
        position: Offset
    ): Offset {

        return characterMovementBounds.clamp(position)
    }

    // ==================================================
    // 15. ODA KOORDİNATLARINI EKRANA DÖNÜŞTÜR
    // ==================================================

    fun roomToScreen(
        position: Offset,
        scale: Float,
        screenOffset: Offset
    ): Offset {

        return Offset(
            x = position.x * scale + screenOffset.x,
            y = position.y * scale + screenOffset.y
        )
    }

    // ==================================================
    // 16. EKRAN KOORDİNATLARINI ODAYA DÖNÜŞTÜR
    // ==================================================

    fun screenToRoom(
        position: Offset,
        scale: Float,
        screenOffset: Offset
    ): Offset {

        require(scale > 0f) {
            "Ölçek sıfırdan büyük olmalıdır."
        }

        return Offset(
            x = (position.x - screenOffset.x) / scale,
            y = (position.y - screenOffset.y) / scale
        )
    }

    // ==================================================
    // 17. NESNELERİN ÇAKIŞMASINI KONTROL ET
    // ==================================================

    fun areObjectsOverlapping(
        first: RoomObject,
        second: RoomObject
    ): Boolean {

        return first.left < second.right &&
            first.right > second.left &&
            first.top < second.bottom &&
            first.bottom > second.top
    }

    // ==================================================
    // 18. BELİRLİ NESNENİN ÇAKIŞTIĞI NESNELER
    // ==================================================

    fun getOverlappingObjects(
        target: RoomObject
    ): List<RoomObject> {

        return objects.filter {

            it.id != target.id &&
                areObjectsOverlapping(target, it)
        }
    }

    // ==================================================
    // 19. ODA NESNESİ SAYISI
    // ==================================================

    fun getObjectCount(): Int {

        return objects.size
    }

    // ==================================================
    // 20. ODA BOYUTLARINI DOĞRULA
    // ==================================================

    fun isValidRoom(): Boolean {

        return roomWidth > 0f &&
            roomHeight > 0f &&
            wallHeight > 0f &&
            floorHeight > 0f &&
            floorHeight < roomHeight
    }
}

// ======================================================
// 21. ODA SINIRLARI VERİ SINIFI
// ======================================================

data class RoomBounds(

    val left: Float,

    val top: Float,

    val right: Float,

    val bottom: Float

) {

    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top

    val center: Offset
        get() = Offset(
            (left + right) / 2f,
            (top + bottom) / 2f
        )

    fun contains(
        position: Offset
    ): Boolean {

        return position.x >= left &&
            position.x <= right &&
            position.y >= top &&
            position.y <= bottom
    }

    fun clamp(
        position: Offset
    ): Offset {

        return Offset(

            x = position.x.coerceIn(
                left,
                right
            ),

            y = position.y.coerceIn(
                top,
                bottom
            )
        )
    }

    fun intersects(
        other: RoomBounds
    ): Boolean {

        return left < other.right &&
            right > other.left &&
            top < other.bottom &&
            bottom > other.top
    }
}
