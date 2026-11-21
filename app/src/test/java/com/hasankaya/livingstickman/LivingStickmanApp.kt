package com.hasankaya.livingstickman

import android.app.Application
import com.google.firebase.FirebaseApp
import com.hasankaya.livingstickman.firebase.AppCheckInstaller

/**
 * Uygulama açılırken Firebase'i hazırlar. Sıra önemlidir:
 * 1) Firebase başlatılır, 2) App Check sağlayıcısı kurulur,
 * 3) Firebase AI Logic ancak bundan sonra kullanılabilir.
 *
 * Bu sınıf Activity'lerden önce çalıştığı için Firebase AI Logic'i çağıran kod
 * (ör. gelecekteki FirebaseChatEngine) her zaman App Check kurulduktan sonra çalışır.
 */
class LivingStickmanApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppCheckInstaller.install()
    }
}
