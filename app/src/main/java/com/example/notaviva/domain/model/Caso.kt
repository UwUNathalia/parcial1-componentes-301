package com.example.notaviva.domain.model

import java.time.LocalDate

/** Caso de investigación periodística. */
data class Caso(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String = "",
    val tema: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val estado: EstadoCaso = EstadoCaso.EN_INVESTIGACION,
    val conclusion: String = ""
) {
    val esValido: Boolean
        get() = titulo.isNotBlank()

    val esNuevo: Boolean
        get() = id == 0L

    val tieneConclusion: Boolean
        get() = conclusion.isNotBlank()

    /** Comprueba si el caso coincide con un texto de búsqueda. */
    fun coincideCon(consulta: String): Boolean {
        if (consulta.isBlank()) return true
        val termino = consulta.trim()
        return titulo.contains(termino, ignoreCase = true) ||
            descripcion.contains(termino, ignoreCase = true) ||
            tema.contains(termino, ignoreCase = true)
    }
}
