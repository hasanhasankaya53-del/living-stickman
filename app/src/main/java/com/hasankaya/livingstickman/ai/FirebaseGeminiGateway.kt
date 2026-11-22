
package com.hasankaya.livingstickman.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException

/**
 * Living Stickman - Firebase Gemini Gateway
 *
 * Firebase AI Logic ile Gemini arasındaki bağlantıyı yönetir.
 *
 * Sorumlulukları:
 *
 * 1. Firebase Google AI servisine bağlanmak.
 * 2. Gemini modelini yapılandırmak.
 * 3. Karakterin sistem talimatını modele aktarmak.
 * 4. Önceki konuşmaları Gemini'ye iletmek.
 * 5. Kullanıcının yeni mesajını göndermek.
 * 6. Gemini cevabını almak.
 * 7. Boş ve geçersiz cevapları kontrol etmek.
 * 8. Gerçek hata ayrıntılarını Logcat'e kaydetmek.
 * 9. İptal edilen coroutine işlemlerini doğru yönetmek.
 *
 * Önemli:
 * Bu sınıf konuşma geçmişini kalıcı olarak saklamaz.
 * Geçmiş bilgileri ChatEngine tarafından gönderilir.
 */

internal class FirebaseGeminiGateway : GeminiGateway {

    companion object {

        private const val TAG = "LivingStickmanGemini"

        private const val LOG_REQUEST_STARTED =
            "Gemini isteği başlatılıyor."

        private const val LOG_REQUEST_SUCCESS =
            "Gemini cevabı başarıyla alındı."

        private const val LOG_REQUEST_FAILED =
            "Gemini isteği başarısız oldu."

        private const val LOG_EMPTY_RESPONSE =
            "Gemini boş cevap döndürdü."
    }

    /**
     * Gemini'ye istek gönderir.
     *
     * @param systemInstruction Karakterin kişiliğini ve
     * davranış kurallarını belirleyen sistem talimatı.
     *
     * @param history Önceki kullanıcı ve Stickman mesajları.
     *
     * @param message Kullanıcının yeni mesajı.
     *
     * @return Gemini tarafından oluşturulan cevap.
     *
     * Başarısız olursa GeminiFailureException fırlatır.
     */
    override suspend fun generate(
        systemInstruction: String,
        history: List<ChatTurn>,
        message: String,
    ): String {

        try {

            // -------------------------------------------------
            // 1. GİRDİ KONTROLLERİ
            // -------------------------------------------------

            val cleanedMessage = message.trim()

            require(cleanedMessage.isNotEmpty()) {
                "Gönderilen mesaj boş olamaz."
            }

            require(systemInstruction.isNotBlank()) {
                "Gemini sistem talimatı boş olamaz."
            }

            Log.d(
                TAG,
                "$LOG_REQUEST_STARTED " +
                    "Geçmiş mesaj sayısı: ${history.size}"
            )

            // -------------------------------------------------
            // 2. FIREBASE AI BAĞLANTISI
            // -------------------------------------------------

            val firebaseAI = Firebase.ai(
                backend = GenerativeBackend.googleAI(),
            )

            // -------------------------------------------------
            // 3. GEMINI MODELİNİ OLUŞTUR
            // -------------------------------------------------

            val model = firebaseAI.generativeModel(

                modelName = GeminiConfig.MODEL_NAME,

                systemInstruction = content {
                    text(systemInstruction)
                },
            )

            Log.d(
                TAG,
                "Gemini modeli hazırlandı: ${GeminiConfig.MODEL_NAME}"
            )

            // -------------------------------------------------
            // 4. KONUŞMA GEÇMİŞİNİ HAZIRLA
            // -------------------------------------------------

            val preparedHistory = history.mapIndexed { index, turn ->

                val role = when (turn.role) {

                    ChatRole.USER -> "user"

                    ChatRole.STICKMAN -> "model"
                }

                Log.d(
                    TAG,
                    "Geçmiş mesaj hazırlandı. " +
                        "Sıra: $index, rol: $role"
                )

                content(
                    role = role,
                ) {
                    text(turn.text)
                }
            }

            // -------------------------------------------------
            // 5. SOHBET OTURUMUNU BAŞLAT
            // -------------------------------------------------

            val chat = model.startChat(
                history = preparedHistory,
            )

            Log.d(
                TAG,
                "Gemini sohbet oturumu oluşturuldu."
            )

            // -------------------------------------------------
            // 6. KULLANICI MESAJINI GÖNDER
            // -------------------------------------------------

            Log.d(
                TAG,
                "Kullanıcı mesajı Gemini'ye gönderiliyor."
            )

            val response = chat.sendMessage(
                cleanedMessage
            )

            // -------------------------------------------------
            // 7. GEMINI CEVABINI AL
            // -------------------------------------------------

            val rawAnswer = response.text

            if (rawAnswer.isNullOrBlank()) {

                Log.e(
                    TAG,
                    LOG_EMPTY_RESPONSE
                )

                throw IllegalStateException(
                    "Gemini sunucusu boş cevap döndürdü."
                )
            }

            val finalAnswer = rawAnswer.trim()

            // -------------------------------------------------
            // 8. CEVABI DOĞRULA
            // -------------------------------------------------

            if (finalAnswer.isEmpty()) {

                throw IllegalStateException(
                    "Gemini cevabı temizlendikten sonra boş kaldı."
                )
            }

            // -------------------------------------------------
            // 9. BAŞARILI İŞLEM KAYDI
            // -------------------------------------------------

            Log.i(
                TAG,
                "$LOG_REQUEST_SUCCESS " +
                    "Cevap uzunluğu: ${finalAnswer.length}"
            )

            return finalAnswer

        } catch (exception: CancellationException) {

            // Coroutine iptallerini normal hata gibi
            // göstermiyoruz. İptal işlemi üst katmana aktarılır.

            Log.d(
                TAG,
                "Gemini isteği iptal edildi."
            )

            throw exception

        } catch (exception: Exception) {

            // -------------------------------------------------
            // 10. GERÇEK HATA AYRINTILARINI KAYDET
            // -------------------------------------------------

            Log.e(
                TAG,
                "$LOG_REQUEST_FAILED " +
                    "Hata sınıfı: ${exception.javaClass.name} " +
                    "Hata mesajı: ${exception.message}",
                exception
            )

            // -------------------------------------------------
            // 11. MEVCUT HATA SİSTEMİNE AKTAR
            // -------------------------------------------------

            throw GeminiFailureException(
                code = RejectCodes.UNKNOWN,
                cause = exception,
            )
        }
    }
}
