package com.hasankaya.livingstickman.ai

/**
 * Gerçek bağlantı kurulamadığında kullanılan dürüst motor.
 * Hiçbir koşulda cevap metni üretmez; yalnızca neden bağlanılamadığını bildirir.
 */
class NotConfiguredChatEngine(
    private val reason: NotConfiguredReason,
) : ChatEngine {

    override val state: ChatEngineState = ChatEngineState.NotConfigured(reason)

    override suspend fun send(request: ChatRequest): ChatResult =
        ChatResult.Failure(ChatFailure.NotConfigured(reason))
}
