
package com.hasankaya.livingstickman.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.app.NotificationCompat
import kotlin.math.abs

class StickmanOverlayService : Service() {

    private lateinit var windowManager: WindowManager

    private var overlayContainer: FrameLayout? = null
    private var stickmanView: StickmanOverlayView? = null
    private var animation: StickmanAnimation? = null
    private var windowParams: WindowManager.LayoutParams? = null

    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0

    private var isDragging = false

    companion object {
        const val ACTION_START =
            "com.hasankaya.livingstickman.overlay.START"

        const val ACTION_STOP =
            "com.hasankaya.livingstickman.overlay.STOP"

        private const val CHANNEL_ID = "living_stickman_overlay"
        private const val NOTIFICATION_ID = 2001
        private const val VIEW_SIZE_DP = 180
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startAsForegroundService()

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager

        createStickman()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Living Stickman",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ekrandaki Stickman karakterinin çalışması"
                setShowBadge(false)
            }

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    private fun startAsForegroundService() {
        val stopIntent = Intent(this, StickmanOverlayService::class.java).apply {
            action = ACTION_STOP
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Living Stickman")
                .setContentText("Stickman ekran üzerinde aktif.")
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setSilent(true)
                .addAction(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Durdur",
                    stopPendingIntent
                )
                .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createStickman() {
        if (overlayContainer != null) return

        val density = resources.displayMetrics.density
        val viewSize = (VIEW_SIZE_DP * density).toInt()

        val character = StickmanOverlayView(this)

        val container = FrameLayout(this).apply {
            addView(
                character,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }

        val params = WindowManager.LayoutParams(
            viewSize,
            viewSize,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 80
            y = 180
        }

        container.setOnTouchListener { _, event ->
            handleTouch(event, params)
        }

        try {
            windowManager.addView(container, params)

            overlayContainer = container
            stickmanView = character
            windowParams = params

            animation = StickmanAnimation(character).also {
                it.play(StickmanAnimation.Motion.IDLE)
            }
        } catch (_: Exception) {
            stopSelf()
        }
    }

    private fun handleTouch(
        event: MotionEvent,
        params: WindowManager.LayoutParams
    ): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.rawX
                initialTouchY = event.rawY

                initialWindowX = params.x
                initialWindowY = params.y

                isDragging = false
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - initialTouchX
                val deltaY = event.rawY - initialTouchY

                if (abs(deltaX) > 8 || abs(deltaY) > 8) {
                    isDragging = true
                }

                if (isDragging) {
                    params.x = initialWindowX + deltaX.toInt()
                    params.y = initialWindowY + deltaY.toInt()

                    overlayContainer?.let { container ->
                        try {
                            windowManager.updateViewLayout(
                                container,
                                params
                            )
                        } catch (_: IllegalArgumentException) {
                            // Pencere artık ekranda değil.
                        }
                    }
                }

                return true
            }

            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    onCharacterTapped()
                }
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                return true
            }
        }

        return true
    }

    private fun onCharacterTapped() {
        animation?.play(StickmanAnimation.Motion.WAVE)
    }

    fun changeAnimation(motion: StickmanAnimation.Motion) {
        animation?.play(motion)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }

            ACTION_START, null -> {
                if (overlayContainer == null &&
                    Settings.canDrawOverlays(this)
                ) {
                    createStickman()
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        animation?.stop()
        animation = null

        overlayContainer?.let { container ->
            try {
                if (::windowManager.isInitialized) {
                    windowManager.removeView(container)
                }
            } catch (_: Exception) {
                // Pencere zaten kaldırılmış olabilir.
            }
        }

        overlayContainer = null
        stickmanView = null
        windowParams = null

        stopForeground(STOP_FOREGROUND_REMOVE)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
