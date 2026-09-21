package com.example.notaviva.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.notaviva.R
import com.example.notaviva.domain.model.EstadoCaso
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Componentes visuales que se repiten en varias pantallas.
 *
 * Tenerlos en un solo archivo evita copiar y pegar el mismo bloque de Compose
 * en cada pantalla, que es la principal fuente de inconsistencias visuales.
 */

/**
 * Etiqueta de color con el estado del caso, como en los mockups.
 */
@Composable
fun EtiquetaEstado(
    estado: EstadoCaso,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(estado.etiquetaRes),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = estado.colorTexto(),
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(estado.colorFondo())
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/**
 * Mensaje centrado para cuando una lista está vacía.
 *
 * Una lista en blanco deja al usuario sin saber si la aplicación falló o si
 * simplemente no hay nada, por eso siempre se muestra una explicación.
 */
@Composable
fun MensajeVacio(
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Campo de búsqueda con ícono de lupa y botón para limpiar.
 *
 * @param texto lo que hay escrito ahora mismo.
 * @param alCambiar se llama en cada tecla; el ViewModel decide qué hacer.
 */
@Composable
fun BarraBusqueda(
    texto: String,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = texto,
        onValueChange = alCambiar,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        placeholder = { Text(stringResource(R.string.cases_search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { alCambiar("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.dialog_cancel)
                    )
                }
            }
        }
    )
}

/**
 * Convierte una fecha a un texto corto y legible, del estilo "12 mar 2026".
 *
 * Usa el idioma del dispositivo, así que en inglés queda "12 Mar 2026".
 */
fun LocalDate.formatoCorto(): String =
    format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))
