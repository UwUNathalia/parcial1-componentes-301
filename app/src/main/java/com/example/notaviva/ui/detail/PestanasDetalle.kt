package com.example.notaviva.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notaviva.R
import com.example.notaviva.domain.model.Entrevista
import com.example.notaviva.domain.model.Evidencia
import com.example.notaviva.domain.model.TipoEvidencia
import com.example.notaviva.ui.components.MensajeVacio
import com.example.notaviva.ui.components.formatoCorto
import com.example.notaviva.viewmodel.EstadoDetalleCaso

/**
 * Contenido de las pestañas del detalle.
 *
 * Están en un archivo aparte para que [PantallaDetalleCaso] se ocupe solo de
 * la estructura (barra, pestañas, diálogos) y no crezca sin control.
 */

/** Lista de entrevistas del caso con sus hallazgos. */
@Composable
fun PestanaEntrevistas(
    estado: EstadoDetalleCaso,
    alEliminar: (Long) -> Unit
) {
    if (estado.entrevistas.isEmpty()) {
        MensajeVacio(
            titulo = stringResource(R.string.detail_tab_interviews),
            mensaje = stringResource(R.string.interview_empty)
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = estado.entrevistas, key = { it.id }) { entrevista ->
            TarjetaEntrevista(
                entrevista = entrevista,
                puedeEliminar = !estado.estaCerrado,
                alEliminar = { alEliminar(entrevista.id) }
            )
        }
    }
}

/** Tarjeta de una entrevista. */
@Composable
private fun TarjetaEntrevista(
    entrevista: Entrevista,
    puedeEliminar: Boolean,
    alEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        // Una fuente anónima nunca muestra el nombre, aunque
                        // se haya escrito algo antes de marcar la casilla.
                        text = if (entrevista.anonima) {
                            stringResource(R.string.interview_anonymous)
                        } else {
                            entrevista.nombreEntrevistado
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (entrevista.anonima && entrevista.rol.isBlank()) {
                            stringResource(R.string.interview_anonymous_role)
                        } else {
                            entrevista.rol
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = entrevista.fecha.formatoCorto(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (puedeEliminar) {
                    IconButton(onClick = alEliminar) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.interview_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(
                text = entrevista.hallazgos.ifBlank {
                    stringResource(R.string.interview_no_findings)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** Pestaña con la conclusión editable del caso. */
@Composable
fun PestanaConclusiones(
    estado: EstadoDetalleCaso,
    alCambiar: (String) -> Unit,
    alGuardar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.conclusion_title),
            style = MaterialTheme.typography.titleMedium
        )

        if (estado.estaCerrado) {
            // Con el caso cerrado la conclusión se lee pero no se edita.
            Text(
                text = estado.borradorConclusion.ifBlank {
                    stringResource(R.string.conclusion_empty)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            OutlinedTextField(
                value = estado.borradorConclusion,
                onValueChange = alCambiar,
                placeholder = { Text(stringResource(R.string.conclusion_hint)) },
                minLines = 6,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = alGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        }
    }
}

/** Pestaña con los archivos de respaldo del caso. */
@Composable
fun PestanaEvidencias(
    estado: EstadoDetalleCaso,
    alEliminar: (Long) -> Unit
) {
    if (estado.evidencias.isEmpty()) {
        MensajeVacio(
            titulo = stringResource(R.string.detail_tab_evidence),
            mensaje = stringResource(R.string.evidence_empty)
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = estado.evidencias, key = { it.id }) { evidencia ->
            FilaEvidencia(
                evidencia = evidencia,
                puedeEliminar = !estado.estaCerrado,
                alEliminar = { alEliminar(evidencia.id) }
            )
        }
    }
}

/** Fila de una evidencia, con el ícono según el tipo de archivo. */
@Composable
private fun FilaEvidencia(
    evidencia: Evidencia,
    puedeEliminar: Boolean,
    alEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = iconoDe(evidencia.tipo),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = evidencia.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = evidencia.tamanoLegible,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (puedeEliminar) {
                IconButton(onClick = alEliminar) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.evidence_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/** Ícono que corresponde a cada tipo de evidencia. */
private fun iconoDe(tipo: TipoEvidencia): ImageVector = when (tipo) {
    TipoEvidencia.DOCUMENTO -> Icons.Filled.Description
    TipoEvidencia.IMAGEN -> Icons.Filled.Image
    TipoEvidencia.AUDIO -> Icons.Filled.AudioFile
    TipoEvidencia.VIDEO -> Icons.Filled.VideoFile
    TipoEvidencia.OTRO -> Icons.Filled.InsertDriveFile
}
