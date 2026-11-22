package com.hasankaya.livingstickman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hasankaya.livingstickman.ai.ChatEngineFactory
import com.hasankaya.livingstickman.ui.chat.ChatScreen
import com.hasankaya.livingstickman.ui.chat.ChatViewModel
import com.hasankaya.livingstickman.ui.chat.ChatViewModelFactory
import com.hasankaya.livingstickman.ui.theme.LivingStickmanTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(ChatEngineFactory.createGemini())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LivingStickmanTheme {
                val state by viewModel.state.collectAsState()

                ChatScreen(
                    state = state,
                    onInputChange = viewModel::onInputChange,
                    onSend = viewModel::send,
                )
            }
        }
    }
}
