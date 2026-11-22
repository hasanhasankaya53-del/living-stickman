package com.hasankaya.livingstickman.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException

internal class FirebaseGeminiGateway : GeminiGateway {

    override suspend fun generate(
        systemInstruction: String,
        history: List<ChatTurn>,
        message: String,
    ): String {
        try {
            val model = Firebase.ai(
                backend = GenerativeBackend.googleAI(),
            ).generativeModel(
                modelName = GeminiConfig.MODEL_NAME,
                systemInstruction = content {
                    text(systemInstruction)
                },
            )

            val chat = model.startChat(
                history = history.map { turn ->
                    content(
                        role = when (turn.role) {
                            ChatRole.USER -> "user"
                            ChatRole.STICKMAN -> "model"
                        },
                    ) {
                        text(turn.text)
                    }
                },
            )

            return chat.sendMessage(message).text.orEmpty()

        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw GeminiFailureException(
                code = RejectCodes.UNKNOWN,
                cause = exception,
            )
        }
    }
}
