package com.hasankaya.livingstickman.ai

internal interface GeminiGateway {

    suspend fun generate(
        systemInstruction: String,
        history: List<ChatTurn>,
        message: String,
    ): String
}

internal class GeminiFailureException(
    val code: String,
    cause: Throwable? = null,
) : Exception(code, cause)
