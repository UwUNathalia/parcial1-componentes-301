@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.notaviva.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notaviva.R
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.ResumenGeneral
import com.example.notaviva.ui.theme.NotaVivaTheme

/**
 * Pantalla de inicio, equivalente al primer mockup del enunciado.
 *
 * Es una pantalla "tonta": recibe los datos ya calculados y las acciones como
 * parámetros, sin conocer el ViewModel. Eso permite verla en el Preview de
 * Android Studio sin base de datos.
 */
@Composable
fun PantallaInicio(
    resumen: ResumenGeneral,
    alCrearCaso: () -> Unit,
    alVerCasos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Encabezado()

        FilaResumen(resumen)

        TarjetaAccion(
            icono = Icons.Filled.NoteAdd,
            titulo = stringResource(R.string.home_new_case),
            descripcion = stringResource(R.string.home_new_case_hint),
            alPulsar = alCrearCaso
        )

        TarjetaAccion(
            icono = Icons.AutoMirrored.Filled.ListAlt,
            titulo = stringResource(R.string.home_my_cases),
            descripcion = stringResource(R.string.home_my_cases_hint),
            alPulsar = alVerCasos
        )
    }
}

/** Saludo y nombre de la aplicación. */
@Composable
private fun Encabezado() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.greeting_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.greeting_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Las tres cifras del resumen, en una sola fila. */
@Composable
private fun FilaResumen(resumen: ResumenGeneral) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Cifra(
            valor = resumen.totalCasos,
            etiqueta = stringResource(R.string.home_total_cases),
            modifier = Modifier.weight(1f)
        )
        Cifra(
            valor = resumen.casosAbiertos,
            etiqueta = stringResource(R.string.home_open_cases),
            modifier = Modifier.weight(1f)
        )
        Cifra(
            valor = resumen.totalEntrevistas,
            etiqueta = stringResource(R.string.home_total_interviews),
            modifier = Modifier.weight(1f)
        )
    }
}

/** Una cifra con su etiqueta debajo. */
@Composable
private fun Cifra(
    valor: Int,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Tarjeta grande con ícono, título y descripción, como en el mockup. */
@Composable
private fun TarjetaAccion(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    alPulsar: () -> Unit
) {
    Card(
        onClick = alPulsar,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PantallaInicioPreview() {
    NotaVivaTheme {
        PantallaInicio(
            resumen = ResumenGeneral(
                totalCasos = 6,
                totalEntrevistas = 18,
                porEstado = mapOf(EstadoCaso.CERRADO to 1)
            ),
            alCrearCaso = {},
            alVerCasos = {}
        )
    }
}
