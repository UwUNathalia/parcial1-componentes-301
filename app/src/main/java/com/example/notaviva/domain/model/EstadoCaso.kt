package com.example.notaviva.domain.model

/** Estados por los que pasa una investigación periodística. */
enum class EstadoCaso {

    EN_INVESTIGACION,

    EN_EDICION,

    PUBLICADO,

    CERRADO;

    val estaCerrado: Boolean
        get() = this == CERRADO

    companion object {

        /** Convierte el nombre guardado en la base de datos al enum. */
        fun desdeNombre(nombre: String?): EstadoCaso =
            entries.firstOrNull { it.name == nombre } ?: EN_INVESTIGACION
    }
}
