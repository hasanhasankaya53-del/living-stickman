
package com.hasankaya.livingstickman.overlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class StickmanOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var stickmanView: TextView? = null

    override fun onCreate() {
        super.onCreate()

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val view = TextView(this).apply {
            text = "👑"
            textSize = 48f
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        windowManager.addView(view, params)
        stickmanView = view
    }

    override fun onDestroy() {
        stickmanView?.let {
            if (it.isAttachedToWindow) {
                windowManager.removeView(it)
            }
        }
        stickmanView = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
