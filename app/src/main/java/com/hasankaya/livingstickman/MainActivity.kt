package com.hasankaya.livingstickman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hasankaya.livingstickman.ai.ChatEngine
import com.hasankaya.livingstickman.ai.ChatEngineFactory
import com.hasankaya.livingstickman.ui.main.MainScreen
import com.hasankaya.livingstickman.ui.main.toConnectionStatus
import com.hasankaya.livingstickman.ui.theme.LivingStickmanTheme

class MainActivity : ComponentActivity() {

    // Backend adresi derleme zamanında gelir (gizli değil). API anahtarı uygulamada yoktur.
    private val chatEngine: ChatEngine = ChatEngineFactory.create(BuildConfig.BACKEND_BASE_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LivingStickmanTheme {
                MainScreen(status = chatEngine.state.toConnectionStatus())
            }
        }
    }
}
