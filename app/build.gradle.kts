import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.hasankaya.livingstickman"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hasankaya.livingstickman"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        // Cloud Run kullanılmıyor: backend adresi yok. Alan, ChatEngineFactory ve MainActivity
        // geçici olarak ona bağlı olduğu için boş değerle duruyor; FirebaseChatEngine
        // adımında Kotlin tarafıyla birlikte kaldırılacak. Gemini anahtarı uygulamada yoktur.
        buildConfigField("String", "BACKEND_BASE_URL", "\"\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

// Firebase AI Logic: Gemini anahtarı uygulamada değil, Firebase tarafında durur.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)

    // App Check: FirebaseAppCheck sınıfı doğrudan kullanıldığı için açıkça eklendi.
    // Yayın sürümünde Play Integrity, yalnızca debug derlemesinde Debug sağlayıcısı.
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
