package com.example.notaviva.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = AzulProfundo,
    onPrimary = GrisSuperficie,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulProfundo,
    secondary = AzulAccion,
    onSecondary = GrisSuperficie,
    background = GrisFondo,
    onBackground = GrisTexto,
    surface = GrisSuperficie,
    onSurface = GrisTexto,
    onSurfaceVariant = GrisTextoSuave,
    error = RojoError
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulProfundoOscuro,
    onPrimary = FondoOscuro,
    primaryContainer = AzulProfundo,
    onPrimaryContainer = TextoOscuro,
    secondary = AzulAccionOscuro,
    onSecondary = FondoOscuro,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = SuperficieOscura,
    onSurface = TextoOscuro,
    error = RojoError
)

/** Tema de la aplicación. */
@Composable
fun NotaVivaTheme(
    modoOscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (modoOscuro) EsquemaOscuro else EsquemaClaro,
        typography = Tipografia,
        content = content
    )
}
