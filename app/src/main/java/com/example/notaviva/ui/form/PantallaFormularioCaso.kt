package com.example.notaviva.ui.form

import android.app.DatePickerDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notaviva.R
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.ui.components.etiquetaRes
import com.example.notaviva.ui.components.formatoCorto
import com.example.notaviva.viewmodel.EstadoFormularioCaso
import java.time.LocalDate

/** Formulario para crear y editar un caso. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormularioCaso(
    estado: EstadoFormularioCaso,
    alCambiarTitulo: (String) -> Unit,
    alCambiarDescripcion: (String) -> Unit,
    alCambiarTema: (String) -> Unit,
    alCambiarFecha: (LocalDate) -> Unit,
    alCambiarEstado: (EstadoCaso) -> Unit,
    alGuardar: () -> Unit,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (estado.esEdicion) R.string.form_edit_title
                            else R.string.form_new_title
                        ),
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
                }
            )
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = estado.titulo,
                onValueChange = alCambiarTitulo,
                label = { Text(stringResource(R.string.form_field_title)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                isError = estado.tituloInvalido,
                supportingText = {
                    if (estado.tituloInvalido) {
                        Text(stringResource(R.string.form_error_title_required))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.tema,
                onValueChange = alCambiarTema,
                label = { Text(stringResource(R.string.form_field_topic)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = alCambiarDescripcion,
                label = { Text(stringResource(R.string.form_field_description)) },
                minLines = 4,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = {
                    DatePickerDialog(
                        contexto,
                        { _, anio, mes, dia ->
                            alCambiarFecha(LocalDate.of(anio, mes + 1, dia))
                        },
                        estado.fecha.year,
                        estado.fecha.monthValue - 1,
                        estado.fecha.dayOfMonth
                    ).show()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "  ${stringResource(R.string.form_field_date)}: " +
                        estado.fecha.formatoCorto(),
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = stringResource(R.string.form_field_state),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadoCaso.entries.forEach { opcion ->
                    FilterChip(
                        selected = estado.estado == opcion,
                        onClick = { alCambiarEstado(opcion) },
                        label = { Text(stringResource(opcion.etiquetaRes)) },
                        shape = RoundedCornerShape(50)
                    )
                }
            }

            Button(
                onClick = alGuardar,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.form_save),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
