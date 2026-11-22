package com.hasankaya.livingstickman.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasankaya.livingstickman.ai.ChatEngine
import kotlinx.coroutines.flow.StateFlow

internal class ChatViewModel(
    engine: ChatEngine,
) : ViewModel() {

    private val controller = ChatController(
        engine = engine,
        scope = viewModelScope,
    )

    val state: StateFlow<ChatUiState> = controller.state

    fun onInputChange(value: String) {
        controller.onInputChange(value)
    }

    fun send() {
        controller.send()
    }
}
