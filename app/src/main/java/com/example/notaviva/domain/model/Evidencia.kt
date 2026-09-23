package com.example.notaviva.domain.model

import java.util.Locale

/** Archivo de respaldo asociado a un caso. */
data class Evidencia(
    val id: Long = 0,
    val casoId: Long,
    val nombre: String,
    val tipo: TipoEvidencia = TipoEvidencia.OTRO,
    val tamanoBytes: Long = 0,
    val rutaLocal: String = ""
) {

    val esValida: Boolean
        get() = casoId != 0L && nombre.isNotBlank()

    val tamanoLegible: String
        get() = when {
            tamanoBytes <= 0 -> "-"
            tamanoBytes < 1024 -> "$tamanoBytes B"
            tamanoBytes < 1024 * 1024 ->
                String.format(Locale.US, "%.0f KB", tamanoBytes / 1024.0)
            else ->
                String.format(Locale.US, "%.1f MB", tamanoBytes / (1024.0 * 1024.0))
        }

    companion object {

        /** Deduce el tipo de evidencia a partir de la extensión del archivo. */
        fun tipoSegunNombre(nombre: String): TipoEvidencia {
            val extension = nombre.substringAfterLast('.', "").lowercase()
            return when (extension) {
                "pdf", "doc", "docx", "txt" -> TipoEvidencia.DOCUMENTO
                "jpg", "jpeg", "png", "webp" -> TipoEvidencia.IMAGEN
                "mp3", "wav", "m4a", "ogg" -> TipoEvidencia.AUDIO
                "mp4", "mov", "avi", "mkv" -> TipoEvidencia.VIDEO
                else -> TipoEvidencia.OTRO
            }
        }
    }
}

/** Categorías de evidencia que maneja la aplicación. */
enum class TipoEvidencia {
    DOCUMENTO,
    IMAGEN,
    AUDIO,
    VIDEO,
    OTRO;

    companion object {

        fun desdeNombre(nombre: String?): TipoEvidencia =
            entries.firstOrNull { it.name == nombre } ?: OTRO
    }
}
