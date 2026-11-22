
package com.hasankaya.livingstickman

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.hasankaya.livingstickman.ai.ChatEngineFactory
import com.hasankaya.livingstickman.overlay.OverlayPermissionHelper
import com.hasankaya.livingstickman.overlay.StickmanOverlayService
import com.hasankaya.livingstickman.ui.chat.ChatScreen
import com.hasankaya.livingstickman.ui.chat.ChatViewModel
import com.hasankaya.livingstickman.ui.chat.ChatViewModelFactory
import com.hasankaya.livingstickman.ui.theme.LivingStickmanTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(
            ChatEngineFactory.createGemini()
        )
    }

    private var overlayServiceStarted = false
    private var permissionSettingsOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ana sohbet ekranı
        setContent {
            LivingStickmanTheme {
                val state by viewModel.state.collectAsState()

                ChatScreen(
                    state = state,
                    onInputChange = viewModel::onInputChange,
                    onSend = viewModel::send
                )
            }
        }

        // Stickman ekran izni kontrolü
        checkOverlayPermission()
    }

    override fun onResume() {
        super.onResume()

        // Ayarlardan geri dönüldüğünde izin kontrol edilir.
        if (OverlayPermissionHelper.hasPermission(this)) {
            startStickmanOverlay()
        }
    }

    private fun checkOverlayPermission() {

        if (OverlayPermissionHelper.hasPermission(this)) {
            startStickmanOverlay()
            return
        }

        if (!permissionSettingsOpened) {
            permissionSettingsOpened = true
            OverlayPermissionHelper.openSettings(this)
        }
    }

    private fun startStickmanOverlay() {

        if (overlayServiceStarted) return

        val intent = Intent(
            this,
            StickmanOverlayService::class.java
        ).apply {
            action = StickmanOverlayService.ACTION_START
        }

        try {
            startService(intent)
            overlayServiceStarted = true
        } catch (exception: Exception) {
            overlayServiceStarted = false
        }
    }
}
