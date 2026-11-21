package com.hasankaya.livingstickman.ui.main

import com.hasankaya.livingstickman.ai.ChatEngineState
import com.hasankaya.livingstickman.ai.NotConfiguredReason
import org.junit.Assert.assertEquals
import org.junit.Test

class ConnectionStatusTest {

    @Test
    fun `maps every engine state to a connection status`() {
        assertEquals(
            ConnectionStatus.NOT_CONFIGURED_URL,
            ChatEngineState.NotConfigured(NotConfiguredReason.BACKEND_URL_MISSING).toConnectionStatus(),
        )
        assertEquals(
            ConnectionStatus.NOT_CONFIGURED_CLIENT,
            ChatEngineState.NotConfigured(NotConfiguredReason.CLIENT_NOT_IMPLEMENTED).toConnectionStatus(),
        )
        assertEquals(ConnectionStatus.OFFLINE, ChatEngineState.Offline.toConnectionStatus())
        assertEquals(ConnectionStatus.ERROR, ChatEngineState.Error("x").toConnectionStatus())
        assertEquals(ConnectionStatus.READY, ChatEngineState.Ready.toConnectionStatus())
    }
}
