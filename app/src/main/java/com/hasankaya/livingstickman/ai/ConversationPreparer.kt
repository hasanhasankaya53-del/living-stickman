package com.hasankaya.livingstickman.ai

internal object ConversationPreparer {

    data class PreparedConversation(
        val history: List<ChatTurn>,
        val message: String,
    )

    fun prepare(
        history: List<ChatTurn>,
        message: String,
        maxHistoryTurns: Int = GeminiConfig.MAX_HISTORY_TURNS,
    ): PreparedConversation {
        val cleanMessage = message.trim()
        require(cleanMessage.isNotBlank()) { "Message cannot be blank." }

        val normalized = mutableListOf<ChatTurn>()

        for (turn in history) {
            val text = turn.text.trim()
            if (text.isBlank()) continue

            val previous = normalized.lastOrNull()
            if (previous != null && previous.role == turn.role) {
                normalized[normalized.lastIndex] =
                    previous.copy(text = "${previous.text}\n$text")
            } else {
                normalized += turn.copy(text = text)
            }
        }

        while (normalized.firstOrNull()?.role == ChatRole.STICKMAN) {
            normalized.removeAt(0)
        }

        while (normalized.lastOrNull()?.role == ChatRole.USER) {
            normalized.removeAt(normalized.lastIndex)
        }

        val safeLimit = maxHistoryTurns.coerceAtLeast(0)
        val limited = normalized.takeLast(safeLimit * 2)

        return PreparedConversation(
            history = limited,
            message = cleanMessage,
        )
    }
}
