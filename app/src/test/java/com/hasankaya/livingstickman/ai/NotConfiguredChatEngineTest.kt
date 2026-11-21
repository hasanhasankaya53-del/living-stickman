package com.hasankaya.livingstickman.ai

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotConfiguredChatEngineTest {

    @Test
    fun `state reports not configured with the given reason`() {
        val engine = NotConfiguredChatEngine(NotConfiguredReason.BACKEND_URL_MISSING)

        assertEquals(
            ChatEngineState.NotConfigured(NotConfiguredReason.BACKEND_URL_MISSING),
            engine.state,
        )
    }

    @Test
    fun `send never produces a reply`() = runTest {
        val engine = NotConfiguredChatEngine(NotConfiguredReason.BACKEND_URL_MISSING)

        val result = engine.send(ChatRequest(message = "Merhaba"))

        assertFalse("Bağlantı yokken cevap üretilmemeli", result is ChatResult.Success)
        assertEquals(
            ChatResult.Failure(ChatFailure.NotConfigured(NotConfiguredReason.BACKEND_URL_MISSING)),
            result,
        )
    }

    @Test
    fun `send fails with client-not-implemented reason too`() = runTest {
        val engine = NotConfiguredChatEngine(NotConfiguredReason.CLIENT_NOT_IMPLEMENTED)

        val result = engine.send(ChatRequest(message = "Selam"))

        assertTrue(result is ChatResult.Failure)
    }

    @Test
    fun `default personality is curious only`() {
        val snapshot = PersonalitySnapshot()

        assertEquals("curious", snapshot.baseTrait)
        assertTrue(snapshot.notes.isEmpty())
    }
}
