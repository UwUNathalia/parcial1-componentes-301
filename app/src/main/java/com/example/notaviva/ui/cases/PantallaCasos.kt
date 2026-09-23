@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.notaviva.ui.cases

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notaviva.R
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.ui.components.BarraBusqueda
import com.example.notaviva.ui.components.MensajeVacio
import com.example.notaviva.ui.components.etiquetaRes
import com.example.notaviva.viewmodel.EstadoListaCasos

/**
 * Listado de casos con búsqueda y filtros por estado.
 *
 * Mantiene la misma estética moderna que la pantalla de inicio con chips redondeados,
 * barra de búsqueda curva e ítems de tarjetas espaciados.
 */
@Composable
fun PantallaCasos(
    estado: EstadoListaCasos,
    alBuscar: (String) -> Unit,
    alFiltrar: (EstadoCaso?) -> Unit,
    alAbrirCaso: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BarraBusqueda(texto = estado.consulta, alCambiar = alBuscar)
            FilaFiltros(seleccionado = estado.filtroEstado, alFiltrar = alFiltrar)
        }

        when {
            estado.cargando -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            estado.sinCasos -> MensajeVacio(
                titulo = stringResource(R.string.cases_empty_title),
                mensaje = stringResource(R.string.cases_empty_message)
            )

            estado.sinResultados -> MensajeVacio(
                titulo = stringResource(R.string.cases_no_results_title),
                mensaje = stringResource(R.string.cases_no_results_message)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = estado.casos, key = { it.caso.id }) { resumen ->
                    TarjetaCaso(
                        casoConResumen = resumen,
                        alPulsar = { alAbrirCaso(resumen.caso.id) }
                    )
                }
            }
        }
    }
}

/** Chips de filtro por estado con bordes suavemente redondeados (pill shape). */
@Composable
private fun FilaFiltros(
    seleccionado: EstadoCaso?,
    alFiltrar: (EstadoCaso?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = seleccionado == null,
            onClick = { alFiltrar(null) },
            label = { Text(stringResource(R.string.cases_filter_all)) },
            shape = RoundedCornerShape(50)
        )
        EstadoCaso.entries.forEach { estado ->
            FilterChip(
                selected = seleccionado == estado,
                onClick = { alFiltrar(estado) },
                label = { Text(stringResource(estado.etiquetaRes)) },
                shape = RoundedCornerShape(50)
            )
        }
    }
}
