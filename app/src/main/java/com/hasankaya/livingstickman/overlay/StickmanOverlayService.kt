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
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.app.NotificationCompat
import kotlin.math.abs

class StickmanOverlayService : Service() {

    companion object {

        const val ACTION_START =
            "com.hasankaya.livingstickman.overlay.START"

        const val ACTION_STOP =
            "com.hasankaya.livingstickman.overlay.STOP"

        private const val CHANNEL_ID =
            "living_stickman_overlay"

        private const val NOTIFICATION_ID = 2001

        private const val VIEW_SIZE_DP = 180

        private const val INITIAL_X_DP = 30

        private const val INITIAL_Y_DP = 120

        private const val TOUCH_THRESHOLD_DP = 8

        private const val TAG = "StickmanOverlayService"
    }

    private lateinit var windowManager: WindowManager

    private var overlayContainer: FrameLayout? = null

    private var stickmanView: StickmanOverlayView? = null

    private var animation: StickmanAnimation? = null

    private var windowParams: WindowManager.LayoutParams? = null

    private var foregroundStarted = false

    private var isDragging = false

    private var initialTouchX = 0f

    private var initialTouchY = 0f

    private var initialWindowX = 0

    private var initialWindowY = 0

    private var touchThreshold = 8f

    // --------------------------------------------------
    // SERVİS OLUŞTURMA
    // --------------------------------------------------

    override fun onCreate() {
        super.onCreate()

        try {

            windowManager =
                getSystemService(WINDOW_SERVICE) as WindowManager

            val density =
                resources.displayMetrics.density

            touchThreshold =
                TOUCH_THRESHOLD_DP * density

            createNotificationChannel()

            Log.d(TAG, "Servis oluşturuldu.")

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Servis başlatılırken hata oluştu.",
                exception
            )

            stopSelf()
        }
    }

    // --------------------------------------------------
    // BİLDİRİM KANALI
    // --------------------------------------------------

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Living Stickman",
                NotificationManager.IMPORTANCE_LOW
            ).apply {

                description =
                    "Living Stickman karakterinin çalışma bildirimi"

                setShowBadge(false)

                setSound(null, null)

                enableVibration(false)
            }

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    // --------------------------------------------------
    // ÖN PLAN SERVİSİ
    // --------------------------------------------------

    private fun startAsForegroundService() {

        if (foregroundStarted) return

        val stopIntent = Intent(
            this,
            StickmanOverlayService::class.java
        ).apply {

            action = ACTION_STOP
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            2002,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )

                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )

                .setContentTitle(
                    "Living Stickman"
                )

                .setContentText(
                    "Stickman ekran üzerinde aktif."
                )

                .setCategory(
                    NotificationCompat.CATEGORY_SERVICE
                )

                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )

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

            startForeground(
                NOTIFICATION_ID,
                notification
            )
        }

        foregroundStarted = true

        Log.d(TAG, "Ön plan servisi başlatıldı.")
    }

    // --------------------------------------------------
    // STICKMAN OLUŞTURMA
    // --------------------------------------------------

    private fun createStickman() {

        if (overlayContainer != null) return

        if (!Settings.canDrawOverlays(this)) {

            Log.e(TAG, "Overlay izni bulunamadı.")

            stopSelf()

            return
        }

        try {

            val density =
                resources.displayMetrics.density

            val viewSize =
                (VIEW_SIZE_DP * density).toInt()

            val character =
                StickmanOverlayView(this)

            val container =
                FrameLayout(this).apply {

                    isClickable = true

                    isFocusable = false

                    setBackgroundColor(
                        android.graphics.Color.TRANSPARENT
                    )

                    addView(
                        character,
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    )
                }

            val params =
                WindowManager.LayoutParams(

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

                    gravity =
                        Gravity.TOP or Gravity.START

                    x =
                        (INITIAL_X_DP * density).toInt()

                    y =
                        (INITIAL_Y_DP * density).toInt()
                }

            container.setOnClickListener {

                onCharacterTapped()
            }

            container.setOnTouchListener { _, event ->

                handleTouch(
                    event,
                    params,
                    container
                )
            }

            windowManager.addView(
                container,
                params
            )

            overlayContainer = container

            stickmanView = character

            windowParams = params

            animation = StickmanAnimation(character).also {

                it.play(
                    StickmanAnimation.Motion.IDLE
                )
            }

            Log.d(TAG, "Stickman ekrana eklendi.")

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Stickman oluşturulamadı.",
                exception
            )

            removeOverlay()

            stopSelf()
        }
    }

    // --------------------------------------------------
    // DOKUNMA VE SÜRÜKLEME
    // --------------------------------------------------

    private fun handleTouch(
        event: MotionEvent,
        params: WindowManager.LayoutParams,
        container: FrameLayout
    ): Boolean {

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {

                initialTouchX = event.rawX

                initialTouchY = event.rawY

                initialWindowX = params.x

                initialWindowY = params.y

                isDragging = false

                return true
            }

            MotionEvent.ACTION_MOVE -> {

                val deltaX =
                    event.rawX - initialTouchX

                val deltaY =
                    event.rawY - initialTouchY

                if (
                    abs(deltaX) > touchThreshold ||
                    abs(deltaY) > touchThreshold
                ) {

                    isDragging = true
                }

                if (isDragging) {

                    params.x =
                        initialWindowX + deltaX.toInt()

                    params.y =
                        initialWindowY + deltaY.toInt()

                    keepInsideScreen(params)

                    updateOverlayPosition(
                        container,
                        params
                    )
                }

                return true
            }

            MotionEvent.ACTION_UP -> {

                if (!isDragging) {

                    container.performClick()
                }

                isDragging = false

                return true
            }

            MotionEvent.ACTION_CANCEL -> {

                isDragging = false

                return true
            }
        }

        return true
    }

    // --------------------------------------------------
    // EKRAN SINIRLARINI KONTROL ET
    // --------------------------------------------------

    private fun keepInsideScreen(
        params: WindowManager.LayoutParams
    ) {

        val displayMetrics =
            resources.displayMetrics

        val screenWidth =
            displayMetrics.widthPixels

        val screenHeight =
            displayMetrics.heightPixels

        val maxX =
            (screenWidth - params.width).coerceAtLeast(0)

        val maxY =
            (screenHeight - params.height).coerceAtLeast(0)

        params.x =
            params.x.coerceIn(0, maxX)

        params.y =
            params.y.coerceIn(0, maxY)
    }

    // --------------------------------------------------
    // PENCERE KONUMUNU GÜNCELLE
    // --------------------------------------------------

    private fun updateOverlayPosition(
        container: FrameLayout,
        params: WindowManager.LayoutParams
    ) {

        try {

            windowManager.updateViewLayout(
                container,
                params
            )

        } catch (exception: IllegalArgumentException) {

            Log.w(
                TAG,
                "Overlay penceresi artık mevcut değil.",
                exception
            )

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Overlay konumu güncellenemedi.",
                exception
            )
        }
    }

    // --------------------------------------------------
    // KARAKTERE DOKUNULDU
    // --------------------------------------------------

    private fun onCharacterTapped() {

        animation?.play(
            StickmanAnimation.Motion.WAVE
        )

        Log.d(TAG, "Karaktere dokunuldu.")
    }

    // --------------------------------------------------
    // DIŞARIDAN ANİMASYON DEĞİŞTİRME
    // --------------------------------------------------

    fun changeAnimation(
        motion: StickmanAnimation.Motion
    ) {

        animation?.play(motion)
    }

    // --------------------------------------------------
    // SERVİS KOMUTLARI
    // --------------------------------------------------

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_STOP -> {

                Log.d(TAG, "Durdurma komutu alındı.")

                stopSelf()

                return START_NOT_STICKY
            }

            ACTION_START, null -> {

                if (!Settings.canDrawOverlays(this)) {

                    Log.e(TAG, "Overlay izni verilmemiş.")

                    stopSelf()

                    return START_NOT_STICKY
                }

                try {

                    startAsForegroundService()

                    createStickman()

                } catch (exception: Exception) {

                    Log.e(
                        TAG,
                        "Servis çalıştırılamadı.",
                        exception
                    )

                    stopSelf()

                    return START_NOT_STICKY
                }
            }

            else -> {

                Log.w(
                    TAG,
                    "Bilinmeyen servis komutu: ${intent.action}"
                )

                return START_NOT_STICKY
            }
        }

        return START_STICKY
    }

    // --------------------------------------------------
    // OVERLAY TEMİZLEME
    // --------------------------------------------------

    private fun removeOverlay() {

        animation?.stop()

        animation = null

        val container = overlayContainer

        if (container != null) {

            try {

                if (::windowManager.isInitialized) {

                    windowManager.removeView(container)
                }

            } catch (exception: Exception) {

                Log.w(
                    TAG,
                    "Overlay kaldırılırken hata oluştu.",
                    exception
                )
            }
        }

        overlayContainer = null

        stickmanView = null

        windowParams = null

        isDragging = false
    }

    // --------------------------------------------------
    // SERVİS KAPANIŞI
    // --------------------------------------------------

    override fun onDestroy() {

        Log.d(TAG, "Servis kapatılıyor.")

        removeOverlay()

        if (foregroundStarted) {

            stopForeground(
                STOP_FOREGROUND_REMOVE
            )

            foregroundStarted = false
        }

        super.onDestroy()
    }

    // --------------------------------------------------
    // BAĞLANTI
    // --------------------------------------------------

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }
}
