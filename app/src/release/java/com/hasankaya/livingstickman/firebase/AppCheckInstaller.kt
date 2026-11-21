package com.hasankaya.livingstickman.firebase

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

/**
 * Yalnızca release derlemesi. Cihaz ve uygulama doğrulaması Play Integrity ile yapılır.
 * Kodda gizli anahtar ya da token yoktur.
 */
internal object AppCheckInstaller {

    fun install() {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
    }
}
