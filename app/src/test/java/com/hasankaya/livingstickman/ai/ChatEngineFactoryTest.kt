package com.hasankaya.livingstickman.ai

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatEngineFactoryTest {

    @Test
    fun `blank url means backend url missing`() {
        assertEquals(
            ChatEngineState.NotConfigured(NotConfiguredReason.BACKEND_URL_MISSING),
            ChatEngineFactory.create("").state,
        )
        assertEquals(
            ChatEngineState.NotConfigured(NotConfiguredReason.BACKEND_URL_MISSING),
            ChatEngineFactory.create("   ").state,
        )
    }

    @Test
    fun `url without real client is still honest about not being connected`() = runTest {
        val engine = ChatEngineFactory.create("https://example.invalid")

        assertEquals(
            ChatEngineState.NotConfigured(NotConfiguredReason.CLIENT_NOT_IMPLEMENTED),
            engine.state,
        )
        assertTrue(engine.send(ChatRequest(message = "Merhaba")) is ChatResult.Failure)
    }
}
