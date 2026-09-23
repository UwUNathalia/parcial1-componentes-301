package com.example.notaviva.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.notaviva.R
import com.example.notaviva.ui.components.EtiquetaEstado
import com.example.notaviva.ui.components.formatoCorto
import com.example.notaviva.viewmodel.EstadoDetalleCaso

/**
 * Pantalla de detalle de un caso.
 *
 * Organiza la información en 4 pestañas estilizadas:
 * Resumen, Entrevistas, Conclusiones y Evidencias.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleCaso(
    estado: EstadoDetalleCaso,
    alVolver: () -> Unit,
    alEditar: () -> Unit,
    alEliminarCaso: () -> Unit,
    alAlternarCierre: () -> Unit,
    alAgregarEntrevista: (String, String, String, Boolean) -> Unit,
    alEliminarEntrevista: (Long) -> Unit,
    alAgregarEvidencia: (String, Long) -> Unit,
    alEliminarEvidencia: (Long) -> Unit,
    alCambiarConclusion: (String) -> Unit,
    alGuardarConclusion: () -> Unit,
    alMensajeMostrado: () -> Unit
) {
    var pestana by remember { mutableIntStateOf(0) }
    var mostrarDialogoEntrevista by remember { mutableStateOf(false) }
    var mostrarDialogoEvidencia by remember { mutableStateOf(false) }
    var mostrarConfirmacionBorrado by remember { mutableStateOf(false) }
    val anfitrionMensajes = remember { SnackbarHostState() }

    val titulos = listOf(
        stringResource(R.string.detail_tab_summary),
        stringResource(R.string.detail_tab_interviews),
        stringResource(R.string.detail_tab_conclusions),
        stringResource(R.string.detail_tab_evidence)
    )

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            anfitrionMensajes.showSnackbar(it)
            alMensajeMostrado()
        }
    }

    LaunchedEffect(estado.casoEliminado) {
        if (estado.casoEliminado) alVolver()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = estado.caso?.titulo ?: stringResource(R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = alAlternarCierre) {
                        Icon(
                            imageVector = if (estado.estaCerrado) Icons.Filled.LockOpen
                            else Icons.Filled.Lock,
                            contentDescription = stringResource(
                                if (estado.estaCerrado) R.string.detail_reopen_case
                                else R.string.detail_close_case
                            )
                        )
                    }
                    IconButton(onClick = alEditar) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.detail_edit)
                        )
                    }
                    IconButton(onClick = { mostrarConfirmacionBorrado = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.detail_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(anfitrionMensajes) },
        floatingActionButton = {
            if (!estado.estaCerrado && (pestana == 1 || pestana == 3)) {
                FloatingActionButton(
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        if (pestana == 1) mostrarDialogoEntrevista = true
                        else mostrarDialogoEvidencia = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(
                            if (pestana == 1) R.string.interview_new
                            else R.string.evidence_new
                        )
                    )
                }
            }
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
        ) {
            Cabecera(estado)

            TabRow(
                selectedTabIndex = pestana,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                titulos.forEachIndexed { indice, titulo ->
                    Tab(
                        selected = pestana == indice,
                        onClick = { pestana = indice },
                        text = {
                            Text(
                                text = titulo,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (pestana == indice) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            when (pestana) {
                0 -> PestanaResumen(estado)
                1 -> PestanaEntrevistas(estado, alEliminarEntrevista)
                2 -> PestanaConclusiones(estado, alCambiarConclusion, alGuardarConclusion)
                else -> PestanaEvidencias(estado, alEliminarEvidencia)
            }
        }
    }

    if (mostrarDialogoEntrevista) {
        DialogoNuevaEntrevista(
            alConfirmar = alAgregarEntrevista,
            alCerrar = { mostrarDialogoEntrevista = false }
        )
    }

    if (mostrarDialogoEvidencia) {
        DialogoNuevaEvidencia(
            alConfirmar = alAgregarEvidencia,
            alCerrar = { mostrarDialogoEvidencia = false }
        )
    }

    if (mostrarConfirmacionBorrado) {
        DialogoConfirmarBorrado(
            alConfirmar = alEliminarCaso,
            alCerrar = { mostrarConfirmacionBorrado = false }
        )
    }
}

/** Tarjeta superior con la metadata del caso y el aviso si está cerrado. */
@Composable
private fun Cabecera(estado: EstadoDetalleCaso) {
    val caso = estado.caso ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EtiquetaEstado(estado = caso.estado)

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = caso.fecha.formatoCorto(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (caso.tema.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = caso.tema,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (estado.estaCerrado) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = stringResource(R.string.detail_case_closed_notice),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

/** Pestaña con la descripción del caso. */
@Composable
private fun PestanaResumen(estado: EstadoDetalleCaso) {
    val caso = estado.caso ?: return
    Card(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.detail_description),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = caso.descripcion.ifBlank {
                    stringResource(R.string.detail_no_description)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
