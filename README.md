# living-stickman

Android için yapay zekâ destekli, canlı bir stickman. (Kotlin + Jetpack Compose)

Durum: **Aşama 1 / 10**, yani Android iskeleti, CI ve API sözleşmesi. Henüz oda, stickman
ve sohbet yok. Uygulama şu an yalnızca dürüst bir bağlantı durumu gösterir.

## Önemli ilkeler

- Yapay zekâ: Google Gemini, yalnızca güvenli backend (Google Cloud Run) üzerinden.
- API anahtarı Android uygulamasına ve bu depoya ASLA konmaz.
- Gerçek bağlantı yoksa sahte cevap üretilmez; uygulama "bağlantı kurulmadı" der.
- Stickman yalnızca yazılı cevap verir. Kullanıcı yazabilir ya da mikrofonla konuşabilir.
- Hafıza ve kişilik gelişim kaydı Room ile telefonda tutulur. Onay olmadan özel bilgi kaydedilmez.

Ayrıntılar: [`docs/architecture.md`](docs/architecture.md), [`docs/api-contract.md`](docs/api-contract.md)

## Derleme

Gereksinimler: JDK 17, Android SDK (API 36), Gradle 8.13.

```bash
gradle testDebugUnitTest assembleDebug
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

Backend adresini vermek için (gizli değildir, anahtar DEĞİLDİR):

```bash
gradle assembleDebug -PbackendBaseUrl=https://ornek-backend.a.run.app
```

Gradle wrapper dosyaları bu aşamada eklenmedi. İstersen yerelde `gradle wrapper --gradle-version 8.13`
çalıştırıp `gradlew`, `gradlew.bat` ve `gradle/wrapper/` dosyalarını commit edebilirsin.
CI, wrapper kullanmaz; Gradle 8.13'ü kendisi kurar.

## CI

`.github/workflows/android.yml` her push ve pull request'te birim testlerini çalıştırır,
debug APK'yi derler ve APK ile test raporlarını artifact olarak yükler. Ayrıca depoda
API anahtarı benzeri bir değer varsa derlemeyi durdurur.
