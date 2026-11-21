package com.hasankaya.livingstickman.ai

object ChatEngineFactory {

    /**
     * Aşama 1: gerçek istemci (RemoteChatEngine) henüz yazılmadı.
     * Bu yüzden adres dolu olsa bile "bağlı" gibi davranılmaz; durum dürüstçe bildirilir.
     * Aşama 7'de adres doluysa RemoteChatEngine döndürülecek.
     */
    fun create(backendBaseUrl: String): ChatEngine {
        val reason = if (backendBaseUrl.isBlank()) {
            NotConfiguredReason.BACKEND_URL_MISSING
        } else {
            NotConfiguredReason.CLIENT_NOT_IMPLEMENTED
        }
        return NotConfiguredChatEngine(reason)
    }
}
