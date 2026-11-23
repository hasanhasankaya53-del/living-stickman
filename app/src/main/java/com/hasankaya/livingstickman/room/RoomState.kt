
package com.hasankaya.livingstickman.room

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ======================================================
// 1. KARAKTERİN ODA İÇİNDEKİ EYLEMLERİ
// ======================================================

enum class CharacterRoomAction {

    IDLE,
    WALKING,
    RUNNING,
    SITTING,
    SLEEPING,
    USING_COMPUTER,
    WATCHING_TV,
    LOOKING_OUTSIDE,
    APPROACHING_PORTAL,
    ENTERING_PORTAL,
    READING,
    INTERACTING,
    LEAVING
}

// ======================================================
// 2. ODA IŞIK DURUMU
// ======================================================

enum class RoomLightState {

    DAYLIGHT,
    EVENING,
    NIGHT,
    DARK,
    CUSTOM
}

// ======================================================
// 3. BİLGİSAYAR DURUMU
// ======================================================

enum class ComputerState {

    OFF,
    STARTING,
    ON,
    SLEEP_MODE
}

// ======================================================
// 4. TELEVİZYON DURUMU
// ======================================================

enum class TelevisionState {

    OFF,
    TURNING_ON,
    ON,
    TURNING_OFF
}

// ======================================================
// 5. PORTAL DURUMU
// ======================================================

enum class PortalState {

    INACTIVE,
    ACTIVATING,
    ACTIVE,
    DESTINATION_READY,
    ENTERING,
    TRANSITIONING,
    COOLDOWN,
    ERROR
}

// ======================================================
// 6. ODA KAPISI DURUMU
// ======================================================

enum class DoorState {

    CLOSED,
    OPENING,
    OPEN,
    CLOSING
}

// ======================================================
// 7. ODA SES DURUMU
// ======================================================

enum class RoomAudioState {

    MUTED,
    AMBIENT,
    INTERACTION,
    PORTAL,
    CUSTOM
}

// ======================================================
// 8. KARAKTERİN ODA İÇİNDEKİ BİLGİLERİ
// ======================================================

data class CharacterRoomState(

    val position: Offset,

    val action: CharacterRoomAction =
        CharacterRoomAction.IDLE,

    val facingRight: Boolean = true,

    val isMoving: Boolean = false,

    val isInteracting: Boolean = false,

    val targetObjectId: String? = null,

    val currentObjectId: String? = null,

    val movementSpeed: Float = 180f,

    val isVisible: Boolean = true
)

// ======================================================
// 9. ODA GENEL DURUMU
// ======================================================

data class RoomSnapshot(

    val character: CharacterRoomState,

    val lightState: RoomLightState =
        RoomLightState.DAYLIGHT,

    val lightIntensity: Float = 1f,

    val computerState: ComputerState =
        ComputerState.OFF,

    val televisionState: TelevisionState =
        TelevisionState.OFF,

    val portalState: PortalState =
        PortalState.INACTIVE,

    val doorState: DoorState =
        DoorState.CLOSED,

    val audioState: RoomAudioState =
        RoomAudioState.AMBIENT,

    val isRoomActive: Boolean = true,

    val isPaused: Boolean = false,

    val isPortalAnimationEnabled: Boolean = true,

    val isParticlesEnabled: Boolean = true,

    val isLightingEnabled: Boolean = true,

    val interactionMessage: String? = null,

    val lastInteractedObjectId: String? = null,

    val revision: Long = 0L
)

// ======================================================
// 10. ODA DURUM YÖNETİCİSİ
// ======================================================

class RoomState(

    private val layout: RoomLayout

) {

    // --------------------------------------------------
    // BAŞLANGIÇ DURUMU
    // --------------------------------------------------

    private val initialSnapshot = RoomSnapshot(

        character = CharacterRoomState(

            position = layout.characterStartPosition
        )
    )

    // --------------------------------------------------
    // DEĞİŞTİRİLEBİLİR İÇ DURUM
    // --------------------------------------------------

    private val mutableState =
        MutableStateFlow(initialSnapshot)

    // --------------------------------------------------
    // DIŞARIDAN SADECE OKUNABİLEN DURUM
    // --------------------------------------------------

    val state: StateFlow<RoomSnapshot> =
        mutableState.asStateFlow()

    // --------------------------------------------------
    // MEVCUT DURUMU AL
    // --------------------------------------------------

    fun getCurrentState(): RoomSnapshot {

        return mutableState.value
    }

    // ==================================================
    // 11. KARAKTER KONUMU
    // ==================================================

    fun updateCharacterPosition(
        newPosition: Offset
    ) {

        val safePosition =
            layout.clampCharacterPosition(newPosition)

        mutableState.update { current ->

            current.copy(

                character = current.character.copy(
                    position = safePosition
                ),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 12. KARAKTER HAREKETİ
    // ==================================================

    fun moveCharacterBy(
        deltaX: Float,
        deltaY: Float
    ) {

        mutableState.update { current ->

            val oldPosition =
                current.character.position

            val newPosition =
                layout.clampCharacterPosition(
                    Offset(
                        oldPosition.x + deltaX,
                        oldPosition.y + deltaY
                    )
                )

            current.copy(

                character = current.character.copy(

                    position = newPosition,

                    isMoving = true,

                    facingRight =
                        if (deltaX > 0f) {
                            true
                        } else if (deltaX < 0f) {
                            false
                        } else {
                            current.character.facingRight
                        },

                    action =
                        CharacterRoomAction.WALKING
                ),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 13. KARAKTER HAREKETİNİ DURDUR
    // ==================================================

    fun stopCharacter() {

        mutableState.update { current ->

            current.copy(

                character = current.character.copy(

                    isMoving = false,

                    action = CharacterRoomAction.IDLE,

                    targetObjectId = null
                ),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 14. KARAKTER EYLEMİNİ DEĞİŞTİR
    // ==================================================

    fun setCharacterAction(
        action: CharacterRoomAction
    ) {

        mutableState.update { current ->

            current.copy(

                character = current.character.copy(

                    action = action,

                    isMoving =
                        action == CharacterRoomAction.WALKING ||
                        action == CharacterRoomAction.RUNNING,

                    isInteracting =
                        action != CharacterRoomAction.IDLE &&
                        action != CharacterRoomAction.WALKING &&
                        action != CharacterRoomAction.RUNNING
                ),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 15. KARAKTERİN HEDEF NESNESİ
    // ==================================================

    fun setTargetObject(
        objectId: String?
    ) {

        mutableState.update { current ->

            current.copy(

                character = current.character.copy(
                    targetObjectId = objectId
                ),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 16. KARAKTERİN KULLANDIĞI NESNE
    // ==================================================

    fun setCurrentObject(
        objectId: String?
    ) {

        mutableState.update { current ->

            current.copy(

                character = current.character.copy(
                    currentObjectId = objectId
                ),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 17. KARAKTERİN YÖNÜ
    // ==================================================

    fun setFacingDirection(
        facingRight: Boolean
    ) {

        mutableState.update { current ->

            current.copy(

                character = current.character.copy(
                    facingRight = facingRight
                ),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 18. KARAKTER GÖRÜNÜRLÜĞÜ
    // ==================================================

    fun setCharacterVisible(
        visible: Boolean
    ) {

        mutableState.update { current ->

            current.copy(

                character = current.character.copy(
                    isVisible = visible
                ),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 19. ODA IŞIKLARI
    // ==================================================

    fun setLightState(
        lightState: RoomLightState
    ) {

        mutableState.update { current ->

            current.copy(

                lightState = lightState,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 20. IŞIK YOĞUNLUĞU
    // ==================================================

    fun setLightIntensity(
        intensity: Float
    ) {

        mutableState.update { current ->

            current.copy(

                lightIntensity =
                    intensity.coerceIn(0f, 1f),

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 21. IŞIKLANDIRMAYI AÇ / KAPAT
    // ==================================================

    fun setLightingEnabled(
        enabled: Boolean
    ) {

        mutableState.update { current ->

            current.copy(

                isLightingEnabled = enabled,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 22. BİLGİSAYAR DURUMU
    // ==================================================

    fun setComputerState(
        computerState: ComputerState
    ) {

        mutableState.update { current ->

            current.copy(

                computerState = computerState,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 23. BİLGİSAYARI AÇ
    // ==================================================

    fun turnOnComputer() {

        setComputerState(
            ComputerState.ON
        )
    }

    // ==================================================
    // 24. BİLGİSAYARI KAPAT
    // ==================================================

    fun turnOffComputer() {

        setComputerState(
            ComputerState.OFF
        )
    }

    // ==================================================
    // 25. TELEVİZYON DURUMU
    // ==================================================

    fun setTelevisionState(
        televisionState: TelevisionState
    ) {

        mutableState.update { current ->

            current.copy(

                televisionState = televisionState,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 26. TELEVİZYONU AÇ
    // ==================================================

    fun turnOnTelevision() {

        setTelevisionState(
            TelevisionState.ON
        )
    }

    // ==================================================
    // 27. TELEVİZYONU KAPAT
    // ==================================================

    fun turnOffTelevision() {

        setTelevisionState(
            TelevisionState.OFF
        )
    }

    // ==================================================
    // 28. PORTAL DURUMU
    // ==================================================

    fun setPortalState(
        portalState: PortalState
    ) {

        mutableState.update { current ->

            current.copy(

                portalState = portalState,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 29. PORTALI AKTİF ET
    // ==================================================

    fun activatePortal() {

        setPortalState(
            PortalState.ACTIVATING
        )
    }

    // ==================================================
    // 30. PORTALI HAZIR HALE GETİR
    // ==================================================

    fun completePortalActivation() {

        setPortalState(
            PortalState.ACTIVE
        )
    }

    // ==================================================
    // 31. PORTALI KAPAT
    // ==================================================

    fun deactivatePortal() {

        setPortalState(
            PortalState.INACTIVE
        )
    }

    // ==================================================
    // 32. PORTAL EFEKTLERİ
    // ==================================================

    fun setPortalEffectsEnabled(
        animationEnabled: Boolean,
        particlesEnabled: Boolean
    ) {

        mutableState.update { current ->

            current.copy(

                isPortalAnimationEnabled =
                    animationEnabled,

                isParticlesEnabled =
                    particlesEnabled,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 33. KAPI DURUMU
    // ==================================================

    fun setDoorState(
        doorState: DoorState
    ) {

        mutableState.update { current ->

            current.copy(

                doorState = doorState,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 34. ODA SESİ
    // ==================================================

    fun setAudioState(
        audioState: RoomAudioState
    ) {

        mutableState.update { current ->

            current.copy(

                audioState = audioState,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 35. ETKİLEŞİM MESAJI
    // ==================================================

    fun setInteractionMessage(
        message: String?
    ) {

        mutableState.update { current ->

            current.copy(

                interactionMessage = message,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 36. SON ETKİLEŞİLEN NESNE
    // ==================================================

    fun setLastInteractedObject(
        objectId: String?
    ) {

        mutableState.update { current ->

            current.copy(

                lastInteractedObjectId = objectId,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 37. ODAYI DURAKLAT
    // ==================================================

    fun pauseRoom() {

        mutableState.update { current ->

            current.copy(

                isPaused = true,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 38. ODAYI DEVAM ETTİR
    // ==================================================

    fun resumeRoom() {

        mutableState.update { current ->

            current.copy(

                isPaused = false,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 39. ODAYI AKTİF ET
    // ==================================================

    fun activateRoom() {

        mutableState.update { current ->

            current.copy(

                isRoomActive = true,

                isPaused = false,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 40. ODAYI DEVRE DIŞI BIRAK
    // ==================================================

    fun deactivateRoom() {

        mutableState.update { current ->

            current.copy(

                isRoomActive = false,

                isPaused = true,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 41. NESNE ETKİLEŞİMİNİ KONTROL ET
    // ==================================================

    fun interactWithObject(
        objectId: String
    ): InteractionResult {

        val roomObject =
            layout.getObjectById(objectId)
                ?: return InteractionResult.FAILED

        val current =
            mutableState.value

        if (!current.isRoomActive || current.isPaused) {
            return InteractionResult.FAILED
        }

        if (!roomObject.isInRange(
                current.character.position
            )
        ) {
            return InteractionResult.OUT_OF_RANGE
        }

        val result =
            roomObject.interact()

        if (result == InteractionResult.SUCCESS) {

            mutableState.update { state ->

                state.copy(

                    lastInteractedObjectId = objectId,

                    interactionMessage =
                        "${roomObject.name} ile etkileşim başladı.",

                    character = state.character.copy(
                        targetObjectId = objectId,
                        isInteracting = true
                    ),

                    revision = state.revision + 1
                )
            }
        }

        return result
    }

    // ==================================================
    // 42. ETKİLEŞİMİ BİTİR
    // ==================================================

    fun finishInteraction() {

        mutableState.update { current ->

            current.copy(

                character = current.character.copy(

                    isInteracting = false,

                    currentObjectId = null,

                    targetObjectId = null,

                    action = CharacterRoomAction.IDLE,

                    isMoving = false
                ),

                interactionMessage = null,

                revision = current.revision + 1
            )
        }
    }

    // ==================================================
    // 43. ODA DURUMUNU SIFIRLA
    // ==================================================

    fun resetRoom() {
    mutableState.update { current ->
        initialSnapshot.copy(
            revision = current.revision + 1L
        )
    }
}
        
fun getCharacterPosition(): Offset {
    return mutableState.value.character.position
}
