package com.hasankaya.livingstickman

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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

    companion object {
        private const val TAG = "LivingStickman"
    }

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(
            ChatEngineFactory.createGemini()
        )
    }

    private var overlayServiceStarted = false
    private var permissionSettingsOpened = false

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // Kullanıcı izin verse de vermese de
            // overlay kontrolüne devam edilir.
            checkOverlayPermission()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        checkNotificationPermission()
    }

    override fun onResume() {
        super.onResume()

        if (OverlayPermissionHelper.hasPermission(this)) {
            startStickmanOverlay()
        }
    }

    private fun checkNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33) {

            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
                return
            }
        }

        checkOverlayPermission()
    }

    private fun checkOverlayPermission() {

        if (OverlayPermissionHelper.hasPermission(this)) {
            startStickmanOverlay()
            return
        }

        if (!permissionSettingsOpened) {
            permissionSettingsOpened = true

            try {
                OverlayPermissionHelper.openSettings(this)
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Overlay ayarları açılamadı.",
                    exception
                )
            }
        }
    }

    private fun startStickmanOverlay() {

        if (overlayServiceStarted) return

        if (!OverlayPermissionHelper.hasPermission(this)) {
            return
        }

        val intent = Intent(
            this,
            StickmanOverlayService::class.java
        ).apply {
            action = StickmanOverlayService.ACTION_START
        }

        try {
            ContextCompat.startForegroundService(
                this,
                intent
            )

            overlayServiceStarted = true

        } catch (exception: Exception) {

            overlayServiceStarted = false

            Log.e(
                TAG,
                "Stickman servisi başlatılamadı.",
                exception
            )
        }
    }
}
