package com.hasankaya.livingstickman.ui.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hasankaya.livingstickman.R

@Composable
fun MainScreen(status: ConnectionStatus) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ConnectionBanner(status)
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun ConnectionBanner(status: ConnectionStatus) {
    val (title, detail) = status.texts()
    val colors = if (status == ConnectionStatus.READY) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    }
    Card(modifier = Modifier.fillMaxWidth(), colors = colors) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = stringResource(title), style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(detail), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private data class StatusTexts(@StringRes val title: Int, @StringRes val detail: Int)

private fun ConnectionStatus.texts(): StatusTexts = when (this) {
    ConnectionStatus.NOT_CONFIGURED_URL ->
        StatusTexts(R.string.status_not_configured_url_title, R.string.status_not_configured_url_detail)
    ConnectionStatus.NOT_CONFIGURED_CLIENT ->
        StatusTexts(R.string.status_not_configured_client_title, R.string.status_not_configured_client_detail)
    ConnectionStatus.OFFLINE ->
        StatusTexts(R.string.status_offline_title, R.string.status_offline_detail)
    ConnectionStatus.ERROR ->
        StatusTexts(R.string.status_error_title, R.string.status_error_detail)
    ConnectionStatus.READY ->
        StatusTexts(R.string.status_ready_title, R.string.status_ready_detail)
}
