package com.hasankaya.livingstickman.firebase

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

/**
 * Yalnızca debug derlemesi. SDK, ilk çalıştırmada kendi debug token'ını üretir ve
 * Logcat'e ("DebugAppCheckProvider" etiketi) yazar. Token kodda ya da depoda tutulmaz;
 * Firebase Console > App Check > Apps > Manage debug tokens altına elle eklenir.
 */
internal object AppCheckInstaller {

    fun install() {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
    }
}
