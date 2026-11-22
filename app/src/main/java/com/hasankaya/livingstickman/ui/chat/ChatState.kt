package com.hasankaya.livingstickman.ui.chat

import com.hasankaya.livingstickman.ai.ChatFailure
import com.hasankaya.livingstickman.ai.ChatRole

data class ChatMessage(
    val id: Long,
    val role: ChatRole,
    val text: String,
)

enum class ChatError {
    NOT_CONFIGURED,
    OFFLINE,
    BLOCKED,
    QUOTA,
    SERVICE,
    INVALID_RESPONSE,
    UNKNOWN,
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val error: ChatError? = null,
)

internal fun ChatFailure.toChatError(): ChatError =
    when (this) {
        is ChatFailure.NotConfigured -> ChatError.NOT_CONFIGURED
        ChatFailure.Offline -> ChatError.OFFLINE
        is ChatFailure.Rejected -> when (code) {
            "BLOCKED" -> ChatError.BLOCKED
            "QUOTA" -> ChatError.QUOTA
            "SERVICE_UNAVAILABLE" -> ChatError.SERVICE
            else -> ChatError.UNKNOWN
        }
        ChatFailure.InvalidResponse -> ChatError.INVALID_RESPONSE
    }
