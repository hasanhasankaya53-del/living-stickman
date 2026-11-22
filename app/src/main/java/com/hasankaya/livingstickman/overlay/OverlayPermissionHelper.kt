package com.hasankaya.livingstickman.overlay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log

object OverlayPermissionHelper {

    private const val TAG = "OverlayPermissionHelper"

    /**
     * Android'in diğer uygulamaların üzerinde
     * pencere gösterme iznini kontrol eder.
     */
    fun hasPermission(context: Context): Boolean {

        return try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(context)
            } else {
                true
            }

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Overlay izni kontrol edilemedi.",
                exception
            )

            false
        }
    }

    /**
     * Kullanıcıyı uygulamanın özel overlay
     * izin ekranına yönlendirir.
     */
    fun openSettings(context: Context): Boolean {

        if (hasPermission(context)) {
            return true
        }

        return try {

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {

                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

            context.startActivity(intent)

            true

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Overlay ayarları açılamadı.",
                exception
            )

            false
        }
    }

    /**
     * İzin durumunu tekrar kontrol eder.
     */
    fun checkPermissionStatus(context: Context): PermissionStatus {

        return if (hasPermission(context)) {

            PermissionStatus.GRANTED

        } else {

            PermissionStatus.DENIED
        }
    }

    enum class PermissionStatus {
        GRANTED,
        DENIED
    }
}
