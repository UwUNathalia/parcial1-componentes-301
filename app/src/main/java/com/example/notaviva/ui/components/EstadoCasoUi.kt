package com.example.notaviva.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.notaviva.R
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.ui.theme.EstadoCerradoFondo
import com.example.notaviva.ui.theme.EstadoCerradoFondoOscuro
import com.example.notaviva.ui.theme.EstadoCerradoTexto
import com.example.notaviva.ui.theme.EstadoCerradoTextoOscuro
import com.example.notaviva.ui.theme.EstadoEdicionFondo
import com.example.notaviva.ui.theme.EstadoEdicionFondoOscuro
import com.example.notaviva.ui.theme.EstadoEdicionTexto
import com.example.notaviva.ui.theme.EstadoEdicionTextoOscuro
import com.example.notaviva.ui.theme.EstadoInvestigacionFondo
import com.example.notaviva.ui.theme.EstadoInvestigacionFondoOscuro
import com.example.notaviva.ui.theme.EstadoInvestigacionTexto
import com.example.notaviva.ui.theme.EstadoInvestigacionTextoOscuro
import com.example.notaviva.ui.theme.EstadoPublicadoFondo
import com.example.notaviva.ui.theme.EstadoPublicadoFondoOscuro
import com.example.notaviva.ui.theme.EstadoPublicadoTexto
import com.example.notaviva.ui.theme.EstadoPublicadoTextoOscuro

/**
 * Traducciones visuales de [EstadoCaso].
 *
 * El enum vive en la capa de dominio y por eso no conoce recursos ni colores.
 * Ese vínculo se hace aquí, en la capa de interfaz, que es la única que debe
 * saber cómo se ve cada estado y cómo se llama en cada idioma.
 */

/** Texto que se muestra para el estado, traducido según el idioma del sistema. */
@get:StringRes
val EstadoCaso.etiquetaRes: Int
    get() = when (this) {
        EstadoCaso.EN_INVESTIGACION -> R.string.state_investigating
        EstadoCaso.EN_EDICION -> R.string.state_editing
        EstadoCaso.PUBLICADO -> R.string.state_published
        EstadoCaso.CERRADO -> R.string.state_closed
    }

/** Color de fondo de la etiqueta, adaptado al modo claro u oscuro. */
@Composable
fun EstadoCaso.colorFondo(): Color {
    val oscuro = isSystemInDarkTheme()
    return when (this) {
        EstadoCaso.EN_INVESTIGACION ->
            if (oscuro) EstadoInvestigacionFondoOscuro else EstadoInvestigacionFondo
        EstadoCaso.EN_EDICION ->
            if (oscuro) EstadoEdicionFondoOscuro else EstadoEdicionFondo
        EstadoCaso.PUBLICADO ->
            if (oscuro) EstadoPublicadoFondoOscuro else EstadoPublicadoFondo
        EstadoCaso.CERRADO ->
            if (oscuro) EstadoCerradoFondoOscuro else EstadoCerradoFondo
    }
}

/** Color del texto de la etiqueta, adaptado al modo claro u oscuro. */
@Composable
fun EstadoCaso.colorTexto(): Color {
    val oscuro = isSystemInDarkTheme()
    return when (this) {
        EstadoCaso.EN_INVESTIGACION ->
            if (oscuro) EstadoInvestigacionTextoOscuro else EstadoInvestigacionTexto
        EstadoCaso.EN_EDICION ->
            if (oscuro) EstadoEdicionTextoOscuro else EstadoEdicionTexto
        EstadoCaso.PUBLICADO ->
            if (oscuro) EstadoPublicadoTextoOscuro else EstadoPublicadoTexto
        EstadoCaso.CERRADO ->
            if (oscuro) EstadoCerradoTextoOscuro else EstadoCerradoTexto
    }
}
