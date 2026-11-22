package com.hasankaya.livingstickman.ui.chat

import com.hasankaya.livingstickman.ai.ChatEngine
import com.hasankaya.livingstickman.ai.ChatRequest
import com.hasankaya.livingstickman.ai.ChatRole
import com.hasankaya.livingstickman.ai.ChatTurn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ChatController(
    private val engine: ChatEngine,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    fun onInputChange(value: String) {
        _state.value = _state.value.copy(
            input = value,
            error = null,
        )
    }

    fun send() {
        val current = _state.value
        if (current.isSending || current.input.isBlank()) return

        val message = current.input.trim()
        val history = current.messages.map {
            ChatTurn(role = it.role, text = it.text)
        }

        _state.value = current.copy(
            input = "",
            isSending = true,
            error = null,
        )

        scope.launch {
            when (val result = engine.send(
                ChatRequest(
                    message = message,
                    history = history,
                ),
            )) {
                is com.hasankaya.livingstickman.ai.ChatResult.Success -> {
                    val reply = result.reply.text
                    _state.value = _state.value.copy(
                        messages = _state.value.messages + listOf(
                            ChatMessage(
                                id = nextId(),
                                role = ChatRole.USER,
                                text = message,
                            ),
                            ChatMessage(
                                id = nextId(),
                                role = ChatRole.STICKMAN,
                                text = reply,
                            ),
                        ),
                        isSending = false,
                    )
                }

                is com.hasankaya.livingstickman.ai.ChatResult.Failure -> {
                    _state.value = _state.value.copy(
                        input = message,
                        isSending = false,
                        error = result.failure.toChatError(),
                    )
                }
            }
        }
    }

    private fun nextId(): Long = System.nanoTime()
}
