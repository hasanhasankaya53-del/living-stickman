package com.hasankaya.livingstickman.ui.main

import com.hasankaya.livingstickman.ai.ChatEngineState
import com.hasankaya.livingstickman.ai.NotConfiguredReason

/** Arayüzde gösterilen bağlantı durumu. Android kaynaklarından bağımsızdır, bu yüzden birim testlenebilir. */
enum class ConnectionStatus {
    NOT_CONFIGURED_URL,
    NOT_CONFIGURED_CLIENT,
    OFFLINE,
    ERROR,
    READY,
}

fun ChatEngineState.toConnectionStatus(): ConnectionStatus = when (this) {
    is ChatEngineState.NotConfigured -> when (reason) {
        NotConfiguredReason.BACKEND_URL_MISSING -> ConnectionStatus.NOT_CONFIGURED_URL
        NotConfiguredReason.CLIENT_NOT_IMPLEMENTED -> ConnectionStatus.NOT_CONFIGURED_CLIENT
    }
    ChatEngineState.Offline -> ConnectionStatus.OFFLINE
    is ChatEngineState.Error -> ConnectionStatus.ERROR
    ChatEngineState.Ready -> ConnectionStatus.READY
}
