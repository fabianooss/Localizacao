package br.org.oss.localizacao

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen(
    paddingValues: PaddingValues,
    viewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Gerencia permissão de forma declarativa
    val locationPermission = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    ) { granted ->
        if (granted) viewModel.onPermissionGranted()
    }

    LaunchedEffect(Unit) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        } else {
            viewModel.onPermissionGranted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            locationPermission.status.shouldShowRationale -> {
                // Usuário negou antes — explique o motivo
                Text("A localização é necessária para rastrear sua viagem.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { locationPermission.launchPermissionRequest() }) {
                    Text("Conceder permissão")
                }
            }

            uiState.isLoading -> {
                CircularProgressIndicator()
                Text("Obtendo localização...")
            }

            uiState.latitude != null -> {
                Text(
                    text = "City: ${uiState.city}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Latitude: %.6f".format(uiState.latitude),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Longitude: %.6f".format(uiState.longitude),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Precisão: ${uiState.accuracy?.toInt()} m",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}