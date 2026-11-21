# Mimari

```
Android uygulaması  --HTTPS-->  Backend (Google Cloud Run)  -->  Google Gemini API
(anahtar yok)                   (anahtar yalnızca burada)
```

## Kararlar

| Konu | Karar |
|---|---|
| Dil / arayüz | Kotlin + Jetpack Compose (Material 3) |
| Paket adı | `com.hasankaya.livingstickman` |
| Yapay zekâ | Google Gemini API, yalnızca güvenli backend üzerinden |
| Backend | Google Cloud Run (6. aşamada eklenecek, `/backend`) |
| API anahtarı | Yalnızca backend ortam değişkeni. Uygulamada ve depoda ASLA |
| Hafıza | Android üzerinde Room. Yalnızca kullanıcının onayladığı bilgiler |
| Kişilik gelişim kaydı | Android üzerinde Room. Kullanıcı görüntüleyebilir ve sıfırlayabilir |
| Başlangıç kişiliği | Yalnızca MERAKLI. Zamanla öğrenerek gelişir |
| Giriş | Yazılı ya da mikrofonla (yazıya dönüştürme) |
| Çıkış | Stickman yalnızca yazılı cevap verir (ses çıkışı yok) |
| Teslim | Her aşama ayrı Pull Request |

## Dürüstlük kuralı

Gerçek Gemini bağlantısı yoksa sahte cevap üretilmez. `ChatEngine` durumları:
`NotConfigured`, `Offline`, `Ready`, `Error`. Aşama 1'de yalnızca `NotConfiguredChatEngine`
vardır; her gönderme isteği `Failure` döner.

## Paket yapısı (Android)

```
com.hasankaya.livingstickman
├── MainActivity
├── ai/        ChatEngine arayüzü, durumlar, istek/cevap modelleri (Aşama 1)
├── ui/main/   Ana ekran ve bağlantı durumu bandı (Aşama 1)
├── ui/theme/  Tema
├── ui/room/   Sanal oda (Aşama 2)
├── ui/stickman/ Stickman çizimi ve animasyonlar (Aşama 2-3)
├── ui/chat/   Sohbet arayüzü (Aşama 4)
├── speech/    Mikrofon -> yazı (Aşama 5)
├── memory/    Onaylı hafıza, Room (Aşama 8)
├── personality/ Kişilik gelişim kaydı, Room (Aşama 8-9)
└── activity/  Sanal etkinlik kataloğu (Aşama 10)
```

## Aşama planı

1. Android iskeleti, GitHub Actions APK derleme, API sözleşmesi (bu aşama)
2. Sanal oda ve stickman çizimi
3. Bekleme ve yürüme animasyonları
4. Sohbet arayüzü ve dürüst bağlantı durumu
5. Mikrofonla yazıya dönüştürme (tr-TR)
6. Backend iskeleti (`/health`, `/v1/chat`, kişilik, şema doğrulama, rate limit) ve testleri
7. `RemoteChatEngine` ile uygulamayı
