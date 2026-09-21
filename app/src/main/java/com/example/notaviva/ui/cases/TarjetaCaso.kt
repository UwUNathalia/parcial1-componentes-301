@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.notaviva.ui.cases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notaviva.R
import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.CasoConResumen
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.ui.components.EtiquetaEstado
import com.example.notaviva.ui.components.formatoCorto
import com.example.notaviva.ui.theme.NotaVivaTheme
import java.time.LocalDate

/**
 * Tarjeta de un caso dentro del listado.
 *
 * Muestra lo que el periodista necesita para reconocer el caso de un vistazo:
 * título, estado, fecha y cuántas entrevistas lleva.
 */
@Composable
fun TarjetaCaso(
    casoConResumen: CasoConResumen,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val caso = casoConResumen.caso

    Card(
        onClick = alPulsar,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = caso.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaEstado(estado = caso.estado)
            }

            if (caso.tema.isNotBlank()) {
                Text(
                    text = caso.tema,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DatoConIcono(
                    icono = Icons.Filled.CalendarMonth,
                    texto = caso.fecha.formatoCorto()
                )
                DatoConIcono(
                    icono = Icons.Filled.RecordVoiceOver,
                    texto = if (casoConResumen.entrevistas == 1) {
                        stringResource(R.string.cases_interviews_count_one)
                    } else {
                        stringResource(
                            R.string.cases_interviews_count,
                            casoConResumen.entrevistas
                        )
                    }
                )
            }
        }
    }
}

/** Ícono pequeño con un texto al lado, para los metadatos de la tarjeta. */
@Composable
private fun DatoConIcono(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaCasoPreview() {
    NotaVivaTheme {
        TarjetaCaso(
            casoConResumen = CasoConResumen(
                caso = Caso(
                    id = 1,
                    titulo = "Corrupción en la obra pública",
                    tema = "Política",
                    fecha = LocalDate.of(2026, 3, 12),
                    estado = EstadoCaso.EN_INVESTIGACION
                ),
                entrevistas = 4
            ),
            alPulsar = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
