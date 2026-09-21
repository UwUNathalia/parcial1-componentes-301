package com.example.notaviva.domain.model

/**
 * Estados por los que pasa una investigación periodística.
 *
 * El orden de las constantes es el del flujo natural del trabajo, y se usa
 * para ordenar los filtros en el listado de casos.
 */
enum class EstadoCaso {

    /** El periodista todavía está recogiendo entrevistas y evidencias. */
    EN_INVESTIGACION,

    /** La investigación terminó y la nota se está redactando. */
    EN_EDICION,

    /** La nota ya salió publicada. */
    PUBLICADO,

    /** El caso se dio por terminado y no admite más cambios de contenido. */
    CERRADO;

    /** Indica si el caso ya no debería recibir nuevas entrevistas o evidencias. */
    val estaCerrado: Boolean
        get() = this == CERRADO

    companion object {

        /**
         * Convierte el nombre guardado en la base de datos al enum.
         * Si el valor no existe (base de datos corrupta o de una versión vieja),
         * devuelve [EN_INVESTIGACION] en lugar de lanzar una excepción.
         */
        fun desdeNombre(nombre: String?): EstadoCaso =
            entries.firstOrNull { it.name == nombre } ?: EN_INVESTIGACION
    }
}
