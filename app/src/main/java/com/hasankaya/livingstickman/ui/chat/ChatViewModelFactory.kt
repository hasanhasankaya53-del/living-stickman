package com.hasankaya.livingstickman.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hasankaya.livingstickman.ai.ChatEngine

internal class ChatViewModelFactory(
    private val engine: ChatEngine,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(engine) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
