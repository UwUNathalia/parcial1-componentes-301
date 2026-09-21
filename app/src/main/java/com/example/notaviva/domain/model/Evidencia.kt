package com.example.notaviva.domain.model

import java.util.Locale

/**
 * Archivo de respaldo asociado a un caso: un contrato, una foto, el audio de
 * una entrevista o un video.
 *
 * Solo se guarda la referencia al archivo, nunca el archivo en sí, para no
 * llenar la base de datos.
 *
 * @property id identificador asignado por la base de datos.
 * @property casoId caso al que pertenece la evidencia.
 * @property nombre nombre visible del archivo, por ejemplo "contrato.pdf".
 * @property tipo categoría del archivo, usada para escoger el ícono.
 * @property tamanoBytes tamaño del archivo en bytes.
 * @property rutaLocal ubicación del archivo en el dispositivo.
 */
data class Evidencia(
    val id: Long = 0,
    val casoId: Long,
    val nombre: String,
    val tipo: TipoEvidencia = TipoEvidencia.OTRO,
    val tamanoBytes: Long = 0,
    val rutaLocal: String = ""
) {

    /** Una evidencia es válida si pertenece a un caso guardado y tiene nombre. */
    val esValida: Boolean
        get() = casoId != 0L && nombre.isNotBlank()

    /**
     * Tamaño legible para mostrar en la interfaz: "2.4 MB", "980 KB".
     * Se calcula aquí para que la vista no tenga que hacer cuentas.
     */
    val tamanoLegible: String
        get() = when {
            tamanoBytes <= 0 -> "-"
            tamanoBytes < 1024 -> "$tamanoBytes B"
            // Se fija Locale.US para que el separador decimal sea siempre un
            // punto. Con el idioma del sistema, en español saldría "1,5 MB" y
            // el mismo dato cambiaría de forma según el teléfono.
            tamanoBytes < 1024 * 1024 ->
                String.format(Locale.US, "%.0f KB", tamanoBytes / 1024.0)
            else ->
                String.format(Locale.US, "%.1f MB", tamanoBytes / (1024.0 * 1024.0))
        }

    companion object {

        /**
         * Deduce el tipo de evidencia a partir de la extensión del archivo.
         * Si la extensión no se reconoce, se clasifica como [TipoEvidencia.OTRO].
         */
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

        /** Conversión segura desde el valor guardado en la base de datos. */
        fun desdeNombre(nombre: String?): TipoEvidencia =
            entries.firstOrNull { it.name == nombre } ?: OTRO
    }
}
