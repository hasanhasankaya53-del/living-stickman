package com.hasankaya.livingstickman.ai

import kotlinx.coroutines.CancellationException
import java.io.IOException

internal class GeminiChatEngine(
    private val gateway: GeminiGateway,
) : ChatEngine {

    override val state: ChatEngineState = ChatEngineState.Ready

    override suspend fun send(request: ChatRequest): ChatResult {
        if (request.message.isBlank()) {
            return ChatResult.Failure(
                ChatFailure.Rejected(
                    RejectCodes.EMPTY_MESSAGE,
                    "Mesaj boş olamaz.",
                ),
            )
        }

        return try {
            val conversation = ConversationPreparer.prepare(
                history = request.history,
                message = request.message,
            )

            val reply = gateway.generate(
                systemInstruction = PersonaPrompt.build(
                    personality = request.personality,
                    memories = request.memories,
                ),
                history = conversation.history,
                message = conversation.message,
            )

            if (reply.isBlank()) {
                ChatResult.Failure(ChatFailure.InvalidResponse)
            } else {
                ChatResult.Success(ChatReply(text = reply.trim()))
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: GeminiFailureException) {
            ChatResult.Failure(
                ChatFailure.Rejected(
                    code = exception.code,
                    message = exception.message ?: "Gemini isteği başarısız.",
                ),
            )
        } catch (exception: IOException) {
            ChatResult.Failure(ChatFailure.Offline)
        } catch (exception: Exception) {
            ChatResult.Failure(ChatFailure.InvalidResponse)
        }
    }
}
