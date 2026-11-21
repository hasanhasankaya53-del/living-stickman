# API Sözleşmesi v1 (taslak)

Android uygulaması ile Google Cloud Run üzerindeki backend arasındaki sözleşmedir.
Uygulama Gemini'yi doğrudan çağırmaz. Gemini API anahtarı yalnızca backend'in ortam
değişkeninde tutulur; Android uygulamasında ve GitHub deposunda ASLA bulunmaz.

## Genel kurallar

- Taşıma: yalnızca HTTPS, JSON, UTF-8. Başlık: `Content-Type: application/json`.
- Sürüm: yol öneki `/v1`. Kırıcı değişiklik `/v2` olarak yayımlanır.
- Dil: sohbet Türkçedir. Backend sistem talimatında Türkçe cevap ister.
- Yalnızca yazılı cevap: cevap metin olarak döner, ses üretilmez.
- Sahte cevap yok: Gemini'ye ulaşılamıyorsa ya da backend'de anahtar tanımlı değilse backend
  hata döner. Hata cevaplarında `reply` alanı ASLA bulunmaz.
- Backend durumsuzdur (stateless): mesaj içeriğini loglamaz ve saklamaz.

## `GET /health`

Uygulama bu uç noktayı bağlantı durumunu belirlemek için kullanır.

200 cevap:

```json
{
  "status": "ok",
  "apiVersion": "1",
  "geminiConfigured": true
}
```

- `geminiConfigured: false` ise uygulama "Yapay zekâ bağlantısı kurulmadı" durumunu gösterir
  ve mesaj göndermeye izin vermez. Yalnızca `status = ok` ve `geminiConfigured = true` iken
  durum `Ready` olur.
- Cevap anahtarın kendisini ya da bir ipucunu asla içermez.

## `POST /v1/chat`

### İstek

```json
{
  "message": "Bugün hava çok güzel!",
  "history": [
    { "role": "user", "text": "Merhaba" },
    { "role": "stickman", "text": "Merhaba! Bugün neler yaptın merak ediyorum." }
  ],
  "memories": [
    { "id": "m_01", "text": "Kullanıcı kedileri seviyor." }
  ],
  "personality": {
    "version": 1,
    "baseTrait": "curious",
    "notes": []
  },
  "client": {
    "appVersion": "0.1.0",
    "installId": "b1c2d3e4-0000-4000-8000-000000000000"
  }
}
```

| Alan | Tür | Sınır | Açıklama |
|---|---|---|---|
| `message` | string | 1–2000 karakter | Kullanıcının mesajı (yazılı ya da mikrofondan yazıya dönüştürülmüş). |
| `history` | dizi | en çok 20 tur | Son konuşma. `role`: `user` veya `stickman`. |
| `memories` | dizi | en çok 30 öğe | YALNIZCA kullanıcının onayladığı hafıza parçaları. |
| `personality` | nesne | | Kişiliğin kompakt anlık görüntüsü. Başlangıç: `baseTrait = "curious"` (meraklı). |
| `client.installId` | string (UUID) | | Rate
