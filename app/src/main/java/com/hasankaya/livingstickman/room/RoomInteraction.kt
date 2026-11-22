
package com.hasankaya.livingstickman.room

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

/**
 * Living Stickman - Room Interaction System
 *
 * Karakterin oda nesneleriyle etkileşimlerini yönetir.
 *
 * Bu sınıf:
 * - Etkileşime uygun nesneyi bulur.
 * - Karakterin nesneye yaklaşacağı konumu hesaplar.
 * - Mesafe kontrolü yapar.
 * - Etkileşimi başlatır ve bitirir.
 * - Nesneye göre karakter eylemini belirler.
 */

enum class InteractionPhase {
    IDLE,
    SEARCHING,
    APPROACHING,
    READY,
    INTERACTING,
    COMPLETED,
    FAILED
}

data class InteractionSession(
    val objectId: String? = null,
    val phase: InteractionPhase = InteractionPhase.IDLE,
    val targetPosition: Offset? = null,
    val interaction: RoomInteractionType? = null,
    val message: String? = null
)

class RoomInteraction(
    private val layout: RoomLayout,
    private val roomState: RoomState
) {

    private var session = InteractionSession()

    fun getSession(): InteractionSession = session

    /**
     * Bir nesneye etkileşim isteği gönderir.
     */
    fun requestInteraction(
        objectId: String
    ): InteractionSession {

        val roomObject =
            layout.getObjectById(objectId)

        if (roomObject == null) {
            return fail("Bu nesne odada bulunamadı.")
        }

        if (!roomObject.isEnabled) {
            return fail("Bu nesne şu anda kullanılamıyor.")
        }

        if (!roomObject.isVisible) {
            return fail("Bu nesne görünür değil.")
        }

        if (!roomObject.isInteractable) {
            return fail("Bu nesneyle etkileşim kurulamıyor.")
        }

        val currentPosition =
            roomState.getCharacterPosition()

        if (roomObject.isInRange(currentPosition)) {
            return beginInteraction(roomObject)
        }

        val target =
            calculateApproachPosition(roomObject)

        session = InteractionSession(
            objectId = objectId,
            phase = InteractionPhase.APPROACHING,
            targetPosition = target,
            interaction = roomObject.interaction,
            message = "${roomObject.name} nesnesine yaklaşılıyor."
        )

        roomState.setTargetObject(objectId)
        roomState.setCharacterAction(
            CharacterRoomAction.WALKING
        )

        return session
    }

    /**
     * Nesneye yaklaşılacak güvenli konumu hesaplar.
     */
    private fun calculateApproachPosition(
        roomObject: RoomObject
    ): Offset {

        val characterPosition =
            roomState.getCharacterPosition()

        val center = roomObject.center

        val dx = characterPosition.x - center.x
        val dy = characterPosition.y - center.y

        val distance =
            sqrt(dx * dx + dy * dy)

        val safeDistance =
            roomObject.interactionRange * 0.65f

        if (distance <= 0.001f) {
            return layout.clampCharacterPosition(
                Offset(
                    center.x - safeDistance,
                    center.y
                )
            )
        }

        val directionX = dx / distance
        val directionY = dy / distance

        val target = Offset(
            center.x + directionX * safeDistance,
            center.y + directionY * safeDistance
        )

        return layout.clampCharacterPosition(target)
    }

    /**
     * Karakterin hedefe ulaştığını bildirir.
     *
     * Hareket sistemi daha sonra bu fonksiyonu çağıracak.
     */
    fun onCharacterArrived(): InteractionSession {

        val objectId =
            session.objectId
                ?: return fail("Hedef nesne bulunamadı.")

        val roomObject =
            layout.getObjectById(objectId)
                ?: return fail("Hedef nesne artık mevcut değil.")

        val characterPosition =
            roomState.getCharacterPosition()

        if (!roomObject.isInRange(characterPosition)) {
            return fail("Nesneye yeterince yaklaşılamadı.")
        }

        return beginInteraction(roomObject)
    }

    /**
     * Etkileşimi başlatır.
     */
    private fun beginInteraction(
        roomObject: RoomObject
    ): InteractionSession {

        val result =
            roomState.interactWithObject(roomObject.id)

        if (result != InteractionResult.SUCCESS) {
            return fail(
                when (result) {
                    InteractionResult.OUT_OF_RANGE ->
                        "Nesneye daha fazla yaklaşmalısın."

                    InteractionResult.ALREADY_IN_USE ->
                        "Bu nesne şu anda kullanılıyor."

                    InteractionResult.DISABLED ->
                        "Bu nesne devre dışı."

                    InteractionResult.NOT_VISIBLE ->
                        "Nesne görünür değil."

                    InteractionResult.NOT_INTERACTABLE ->
                        "Bu nesne kullanılamıyor."

                    else ->
                        "Etkileşim başlatılamadı."
                }
            )
        }

        val action =
            getCharacterAction(roomObject.interaction)

        roomState.setCurrentObject(roomObject.id)
        roomState.setCharacterAction(action)

        session = InteractionSession(
            objectId = roomObject.id,
            phase = InteractionPhase.INTERACTING,
            targetPosition = roomObject.center,
            interaction = roomObject.interaction,
            message = "${roomObject.name} kullanılıyor."
        )

        return session
    }

    /**
     * Nesne türüne göre karakter eylemini belirler.
     */
    private fun getCharacterAction(
        interaction: RoomInteractionType
    ): CharacterRoomAction {

        return when (interaction) {

            RoomInteractionType.SLEEP ->
                CharacterRoomAction.SLEEPING

            RoomInteractionType.SIT ->
                CharacterRoomAction.SITTING

            RoomInteractionType.USE_COMPUTER ->
                CharacterRoomAction.USING_COMPUTER

            RoomInteractionType.WATCH_TV ->
                CharacterRoomAction.WATCHING_TV

            RoomInteractionType.LOOK_OUTSIDE ->
                CharacterRoomAction.LOOKING_OUTSIDE

            RoomInteractionType.ENTER_PORTAL ->
                CharacterRoomAction.ENTERING_PORTAL

            RoomInteractionType.READ_BOOK ->
                CharacterRoomAction.READING

            RoomInteractionType.OPEN_DOOR ->
                CharacterRoomAction.INTERACTING

            RoomInteractionType.TURN_ON_LIGHT,
            RoomInteractionType.TURN_OFF_LIGHT ->
                CharacterRoomAction.INTERACTING

            RoomInteractionType.WALK_TO ->
                CharacterRoomAction.WALKING

            RoomInteractionType.NONE ->
                CharacterRoomAction.IDLE
        }
    }

    /**
     * Etkileşimi tamamlar.
     */
    fun completeInteraction(): InteractionSession {

        if (session.phase != InteractionPhase.INTERACTING) {
            return fail("Devam eden bir etkileşim yok.")
        }

        val objectId = session.objectId

        roomState.finishInteraction()

        session = InteractionSession(
            objectId = objectId,
            phase = InteractionPhase.COMPLETED,
            message = "Etkileşim tamamlandı."
        )

        return session
    }

    /**
     * Etkileşimi iptal eder.
     */
    fun cancelInteraction(): InteractionSession {

        roomState.finishInteraction()

        session = InteractionSession(
            phase = InteractionPhase.IDLE,
            message = "Etkileşim iptal edildi."
        )

        return session
    }

    /**
     * Hedef nesneyi otomatik bulur.
     */
    fun requestNearestInteraction(): InteractionSession {

        val position =
            roomState.getCharacterPosition()

        val nearest =
            layout.getNearestInteractableObject(position)

        if (nearest == null) {
            return fail("Yakında kullanılabilir nesne yok.")
        }

        return requestInteraction(nearest.id)
    }

    /**
     * Nesnenin etkileşim mesafesini kontrol eder.
     */
    fun isNearObject(
        objectId: String
    ): Boolean {

        val roomObject =
            layout.getObjectById(objectId)
                ?: return false

        return roomObject.isInRange(
            roomState.getCharacterPosition()
        )
    }

    /**
     * Mevcut etkileşimi kontrol eder.
     */
    fun isInteracting(): Boolean {

        return session.phase ==
            InteractionPhase.INTERACTING
    }

    /**
     * Karakterin yaklaşma aşamasında olup olmadığını kontrol eder.
     */
    fun isApproaching(): Boolean {

        return session.phase ==
            InteractionPhase.APPROACHING
    }

    /**
     * Hata durumunu oluşturur.
     */
    private fun fail(
        message: String
    ): InteractionSession {

        session = InteractionSession(
            phase = InteractionPhase.FAILED,
            message = message
        )

        roomState.setInteractionMessage(message)

        return session
    }

    /**
     * Etkileşim sistemini sıfırlar.
     */
    fun reset() {

        session = InteractionSession()
    }
}
