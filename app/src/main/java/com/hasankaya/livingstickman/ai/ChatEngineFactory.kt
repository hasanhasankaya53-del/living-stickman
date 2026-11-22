package com.hasankaya.livingstickman.ai

object ChatEngineFactory {

    fun createGemini(): ChatEngine {
        return GeminiChatEngine(
            gateway = FirebaseGeminiGateway(),
        )
    }

    fun create(backendBaseUrl: String): ChatEngine {
        val reason = if (backendBaseUrl.isBlank()) {
            NotConfiguredReason.BACKEND_URL_MISSING
        } else {
            NotConfiguredReason.CLIENT_NOT_IMPLEMENTED
        }

        return NotConfiguredChatEngine(reason)
    }
}
