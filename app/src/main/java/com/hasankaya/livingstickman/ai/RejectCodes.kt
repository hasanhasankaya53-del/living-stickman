package com.hasankaya.livingstickman.ai

/**
 * Living Stickman - Reject Codes
 *
 * Yapay zekâ sisteminde oluşabilecek hataların
 * standart kodlarını merkezi olarak tutar.
 *
 * Amaç:
 *
 * 1. Hataları birbirinden ayırmak.
 * 2. UNKNOWN hatalarının azaltılmasına yardımcı olmak.
 * 3. ChatEngine ve Gemini Gateway arasında
 *    ortak hata kodları kullanmak.
 * 4. Kullanıcıya daha anlaşılır hata mesajları
 *    gösterilmesini sağlamak.
 * 5. Gelecekte yeni hata türleri eklenmesini
 *    kolaylaştırmak.
 *
 * ÖNEMLİ:
 * Bu sınıf hata kodlarını tanımlar.
 * Hataları otomatik olarak algılamaz.
 * Hata algılama işlemi Gateway ve ChatEngine
 * katmanlarında gerçekleştirilir.
 */

internal object RejectCodes {

    // -------------------------------------------------
    // 1. MESAJ HATALARI
    // -------------------------------------------------

    /**
     * Kullanıcı boş mesaj göndermeye çalıştı.
     */
    const val EMPTY_MESSAGE = "EMPTY_MESSAGE"

    /**
     * Mesaj izin verilen uzunluğu aştı.
     */
    const val MESSAGE_TOO_LONG = "MESSAGE_TOO_LONG"

    /**
     * Mesaj biçimi geçersiz.
     */
    const val INVALID_MESSAGE = "INVALID_MESSAGE"

    /**
     * Mesaj işlenemedi.
     */
    const val MESSAGE_PROCESSING_FAILED =
        "MESSAGE_PROCESSING_FAILED"


    // -------------------------------------------------
    // 2. GÜVENLİK VE İÇERİK HATALARI
    // -------------------------------------------------

    /**
     * İstek güvenlik kontrolü tarafından engellendi.
     */
    const val BLOCKED = "BLOCKED"

    /**
     * İçerik güvenlik denetiminden geçemedi.
     */
    const val SAFETY_BLOCKED = "SAFETY_BLOCKED"

    /**
     * İstek geçersiz veya izin verilmeyen içerik içeriyor.
     */
    const val INVALID_CONTENT = "INVALID_CONTENT"


    // -------------------------------------------------
    // 3. KOTA VE KULLANIM SINIRI HATALARI
    // -------------------------------------------------

    /**
     * API kullanım kotası doldu.
     */
    const val QUOTA = "QUOTA"

    /**
     * İstek sınırına ulaşıldı.
     */
    const val RATE_LIMIT = "RATE_LIMIT"

    /**
     * Çok fazla istek gönderildi.
     */
    const val TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS"


    // -------------------------------------------------
    // 4. İNTERNET VE BAĞLANTI HATALARI
    // -------------------------------------------------

    /**
     * İnternet bağlantısı bulunamadı
     * veya bağlantı başarısız oldu.
     */
    const val NETWORK_ERROR = "NETWORK_ERROR"

    /**
     * Bağlantı zaman aşımına uğradı.
     */
    const val TIMEOUT = "TIMEOUT"

    /**
     * Sunucuya bağlantı kurulamadı.
     */
    const val CONNECTION_FAILED = "CONNECTION_FAILED"

    /**
     * Sunucu bağlantısı beklenmedik şekilde kesildi.
     */
    const val CONNECTION_LOST = "CONNECTION_LOST"

    /**
     * Sunucu yanıtı geçersiz.
     */
    const val INVALID_SERVER_RESPONSE =
        "INVALID_SERVER_RESPONSE"


    // -------------------------------------------------
    // 5. FIREBASE VE KİMLİK DOĞRULAMA HATALARI
    // -------------------------------------------------

    /**
     * Firebase servisi kullanılamıyor.
     */
    const val FIREBASE_UNAVAILABLE =
        "FIREBASE_UNAVAILABLE"

    /**
     * Kimlik doğrulama başarısız.
     */
    const val AUTHENTICATION_FAILED =
        "AUTHENTICATION_FAILED"

    /**
     * API anahtarı veya yapılandırma sorunu.
     */
    const val API_CONFIGURATION_ERROR =
        "API_CONFIGURATION_ERROR"

    /**
     * Kullanıcının isteği için gerekli izin bulunamadı.
     */
    const val PERMISSION_DENIED = "PERMISSION_DENIED"


    // -------------------------------------------------
    // 6. GEMINI MODEL HATALARI
    // -------------------------------------------------

    /**
     * İstenen Gemini modeli bulunamadı.
     */
    const val MODEL_NOT_FOUND = "MODEL_NOT_FOUND"

    /**
     * Gemini modeli kullanılamıyor.
     */
    const val MODEL_UNAVAILABLE = "MODEL_UNAVAILABLE"

    /**
     * Gemini boş cevap döndürdü.
     */
    const val EMPTY_RESPONSE = "EMPTY_RESPONSE"

    /**
     * Gemini cevabı işlenemedi.
     */
    const val RESPONSE_PROCESSING_FAILED =
        "RESPONSE_PROCESSING_FAILED"

    /**
     * Gemini isteği başarısız oldu.
     */
    const val GENERATION_FAILED = "GENERATION_FAILED"


    // -------------------------------------------------
    // 7. SOHBET VE OTURUM HATALARI
    // -------------------------------------------------

    /**
     * Sohbet oturumu oluşturulamadı.
     */
    const val CHAT_SESSION_FAILED = "CHAT_SESSION_FAILED"

    /**
     * Konuşma geçmişi geçersiz.
     */
    const val INVALID_CHAT_HISTORY = "INVALID_CHAT_HISTORY"

    /**
     * Konuşma geçmişi yüklenemedi.
     */
    const val CHAT_HISTORY_LOAD_FAILED =
        "CHAT_HISTORY_LOAD_FAILED"

    /**
     * Konuşma geçmişi kaydedilemedi.
     */
    const val CHAT_HISTORY_SAVE_FAILED =
        "CHAT_HISTORY_SAVE_FAILED"

    /**
     * İstek zaten devam ediyor.
     */
    const val REQUEST_ALREADY_RUNNING =
        "REQUEST_ALREADY_RUNNING"

    /**
     * İstek kullanıcı veya sistem tarafından iptal edildi.
     */
    const val REQUEST_CANCELLED = "REQUEST_CANCELLED"


    // -------------------------------------------------
    // 8. KARAKTER HAFIZASI HATALARI
    // -------------------------------------------------

    /**
     * Karakter hafızası yüklenemedi.
     */
    const val MEMORY_LOAD_FAILED = "MEMORY_LOAD_FAILED"

    /**
     * Karakter hafızası kaydedilemedi.
     */
    const val MEMORY_SAVE_FAILED = "MEMORY_SAVE_FAILED"

    /**
     * Hafıza verisi geçersiz.
     */
    const val INVALID_MEMORY_DATA = "INVALID_MEMORY_DATA"

    /**
     * Hafıza onayı bulunamadı.
     */
    const val MEMORY_APPROVAL_REQUIRED =
        "MEMORY_APPROVAL_REQUIRED"


    // -------------------------------------------------
    // 9. SERVİS HATALARI
    // -------------------------------------------------

    /**
     * Yapay zekâ servisi geçici olarak kullanılamıyor.
     *
     * Eski kodla uyumluluk için korunmuştur.
     */
    const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"

    /**
     * Sunucu beklenmeyen bir hata döndürdü.
     */
    const val SERVER_ERROR = "SERVER_ERROR"

    /**
     * İstek tamamlanamadı.
     */
    const val REQUEST_FAILED = "REQUEST_FAILED"


    // -------------------------------------------------
    // 10. GENEL HATALAR
    // -------------------------------------------------

    /**
     * Hatanın gerçek türü belirlenemedi.
     *
     * Eski sistemle uyumluluk için korunmuştur.
     */
    const val UNKNOWN = "UNKNOWN"
}
