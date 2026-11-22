package com.hasankaya.livingstickman.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hasankaya.livingstickman.ai.ChatRole

@Composable
fun ChatScreen(
    state: ChatUiState,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Living Stickman", style = MaterialTheme.typography.headlineSmall)

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.messages, key = { it.id }) { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.role == ChatRole.USER) {
                            Arrangement.End
                        } else {
                            Arrangement.Start
                        },
                    ) {
                        Card {
                            Text(
                                text = message.text,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    }
                }
            }

            state.error?.let {
                Text(
                    text = "Mesaj gönderilemedi: $it",
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = state.input,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mesajını yaz...") },
                    enabled = !state.isSending,
                )

                Button(
                    onClick = onSend,
                    enabled = state.input.isNotBlank() && !state.isSending,
                ) {
                    Text(if (state.isSending) "..." else "Gönder")
                }
            }
        }
    }
}
