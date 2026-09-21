package com.example.notaviva.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.notaviva.R

/**
 * Diálogo para registrar una entrevista con sus hallazgos.
 *
 * El estado del formulario vive dentro del diálogo y no en el ViewModel,
 * porque se descarta al cerrarlo: no tiene sentido conservarlo.
 */
@Composable
fun DialogoNuevaEntrevista(
    alConfirmar: (nombre: String, rol: String, hallazgos: String, anonima: Boolean) -> Unit,
    alCerrar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var hallazgos by remember { mutableStateOf("") }
    var anonima by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text(stringResource(R.string.interview_new)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        error = false
                    },
                    label = { Text(stringResource(R.string.interview_field_name)) },
                    singleLine = true,
                    // Una fuente anónima no lleva nombre, así que el campo se
                    // desactiva en lugar de quedar pidiendo un dato inútil.
                    enabled = !anonima,
                    isError = error,
                    supportingText = {
                        if (error) Text(stringResource(R.string.interview_error_name))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rol,
                    onValueChange = { rol = it },
                    label = { Text(stringResource(R.string.interview_field_role)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hallazgos,
                    onValueChange = { hallazgos = it },
                    label = { Text(stringResource(R.string.interview_field_findings)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = anonima,
                        onCheckedChange = {
                            anonima = it
                            error = false
                        }
                    )
                    Text(
                        text = stringResource(R.string.interview_field_anonymous),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!anonima && nombre.isBlank()) {
                        error = true
                    } else {
                        alConfirmar(nombre, rol, hallazgos, anonima)
                        alCerrar()
                    }
                }
            ) {
                Text(stringResource(R.string.dialog_add))
            }
        },
        dismissButton = {
            TextButton(onClick = alCerrar) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

/**
 * Diálogo para registrar una evidencia.
 *
 * El tipo (documento, imagen, audio, video) no se pregunta: se deduce de la
 * extensión del nombre del archivo.
 */
@Composable
fun DialogoNuevaEvidencia(
    alConfirmar: (nombre: String, tamanoKb: Long) -> Unit,
    alCerrar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var tamano by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text(stringResource(R.string.evidence_new)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        error = false
                    },
                    label = { Text(stringResource(R.string.evidence_field_name)) },
                    placeholder = { Text("contrato.pdf") },
                    singleLine = true,
                    isError = error,
                    supportingText = {
                        if (error) Text(stringResource(R.string.evidence_error_name))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tamano,
                    // Se filtran las letras para no tener que validar después.
                    onValueChange = { nuevo -> tamano = nuevo.filter { it.isDigit() } },
                    label = { Text(stringResource(R.string.evidence_field_size)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombre.isBlank()) {
                        error = true
                    } else {
                        alConfirmar(nombre, tamano.toLongOrNull() ?: 0L)
                        alCerrar()
                    }
                }
            ) {
                Text(stringResource(R.string.dialog_add))
            }
        },
        dismissButton = {
            TextButton(onClick = alCerrar) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}

/** Confirmación antes de borrar un caso, porque la acción no se puede deshacer. */
@Composable
fun DialogoConfirmarBorrado(
    alConfirmar: () -> Unit,
    alCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text(stringResource(R.string.dialog_delete_case_title)) },
        text = { Text(stringResource(R.string.dialog_delete_case_message)) },
        confirmButton = {
            TextButton(
                onClick = {
                    alConfirmar()
                    alCerrar()
                }
            ) {
                Text(
                    text = stringResource(R.string.dialog_confirm),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = alCerrar) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}
