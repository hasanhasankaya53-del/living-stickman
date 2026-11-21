package com.hasankaya.livingstickman.ai

/**
 * Yapay zekâ katmanının sözleşmesi. Uygulama Gemini'yi doğrudan ÇAĞIRMAZ;
 * tüm istekler kendi backend'i üzerinden geçer (bkz. docs/api-contract.md).
 *
 * Kural: bağlantı yoksa cevap metni üretilmez. Sahte cevap yoktur.
 */
interface ChatEngine {
    /** Motorun şu anki bağlantı durumu. Arayüz bunu dürüstçe gösterir. */
    val state: ChatEngineState

    /** Mesajı gönderir. Bağlantı yoksa [ChatResult.Failure] döner; hiçbir zaman uydurma cevap dönmez. */
    suspend fun send(request: ChatRequest): ChatResult
}

enum class ChatRole { USER, STICKMAN }

data class ChatTurn(val role: ChatRole, val text: String)

/** Kullanıcının onayladığı ve backend'e bağlam olarak gönderilebilecek hafıza parçası. */
data class ApprovedMemory(val id: String, val text: String)

/** Kişiliğin kompakt anlık görüntüsü. Başlangıç kişiliği yalnızca "curious" (meraklı). */
data class PersonalitySnapshot(
    val version: Int = 1,
    val baseTrait: String = BASE_TRAIT_CURIOUS,
    val notes: List<String> = emptyList(),
) {
    companion object {
        const val BASE_TRAIT_CURIOUS = "curious"
    }
}

data class ChatRequest(
    val message: String,
    val history: List<ChatTurn> = emptyList(),
    val memories: List<ApprovedMemory> = emptyList(),
    val personality: PersonalitySnapshot = PersonalitySnapshot(),
)

/** Gemini'nin önerdiği hafıza adayı. Kullanıcı onaylamadan ASLA kaydedilmez. */
data class MemoryCandidate(
    val text: String,
    val category: String,
    val containsPersonalData: Boolean,
)

/** Kişilik gelişimi önerisi. Uygulama sınırlar içinde uygular ve kayıt altına alır. */
data class PersonalityNote(val trait: String, val note: String)

/** Yalnızca yazılı cevap: ses çıkışı yoktur. */
data class ChatReply(
    val text: String,
    val memoryCandidates: List<MemoryCandidate> = emptyList(),
    val personalityNotes: List<PersonalityNote> = emptyList(),
)

enum class NotConfiguredReason {
    /** Backend adresi (BACKEND_BASE_URL) ayarlanmamış. */
    BACKEND_URL_MISSING,

    /** Adres var, fakat gerçek istemci (RemoteChatEngine) bu sürümde henüz yok. */
    CLIENT_NOT_IMPLEMENTED,
}

sealed interface ChatEngineState {
    data class NotConfigured(val reason: NotConfiguredReason) : ChatEngineState
    data object Offline : ChatEngineState
    data object Ready : ChatEngineState
    data class Error(val message: String) : ChatEngineState
}

sealed interface ChatFailure {
    data class NotConfigured(val reason: NotConfiguredReason) : ChatFailure
    data object Offline : ChatFailure
    data class Rejected(val code: String, val message: String) : ChatFailure
    data object InvalidResponse : ChatFailure
}

sealed interface ChatResult {
    data class Success(val reply: ChatReply) : ChatResult
    data class Failure(val failure: ChatFailure) : ChatResult
}
