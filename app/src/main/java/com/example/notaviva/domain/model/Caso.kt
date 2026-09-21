package com.example.notaviva.domain.model

import java.time.LocalDate

/**
 * Caso de investigación periodística.
 *
 * Es el modelo central de la aplicación: agrupa las entrevistas, las evidencias
 * y la conclusión del periodista.
 *
 * Esta clase pertenece a la capa de dominio, así que no conoce la base de datos
 * ni Compose. La capa de datos la traduce a filas de SQLite y la de interfaz
 * solo la lee.
 *
 * @property id identificador asignado por la base de datos. Vale 0 mientras el
 *   caso no se ha guardado todavía.
 * @property titulo título de la investigación. Es el único campo obligatorio.
 * @property descripcion resumen de qué se está investigando.
 * @property tema sección o tema periodístico, por ejemplo "Política" o "Ambiente".
 * @property fecha fecha de apertura del caso.
 * @property estado etapa actual del caso.
 * @property conclusion cierre que escribe el periodista al terminar.
 */
data class Caso(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String = "",
    val tema: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val estado: EstadoCaso = EstadoCaso.EN_INVESTIGACION,
    val conclusion: String = ""
) {

    /** Un caso solo se puede guardar si tiene título. */
    val esValido: Boolean
        get() = titulo.isNotBlank()

    /** True si el caso todavía no se ha guardado en la base de datos. */
    val esNuevo: Boolean
        get() = id == 0L

    /** True si el periodista ya escribió la conclusión. */
    val tieneConclusion: Boolean
        get() = conclusion.isNotBlank()

    /**
     * Comprueba si el caso coincide con un texto de búsqueda.
     * Se busca en el título, la descripción y el tema, sin distinguir mayúsculas.
     *
     * @param consulta texto escrito por el usuario. En blanco significa "todos".
     */
    fun coincideCon(consulta: String): Boolean {
        if (consulta.isBlank()) return true
        val termino = consulta.trim()
        return titulo.contains(termino, ignoreCase = true) ||
            descripcion.contains(termino, ignoreCase = true) ||
            tema.contains(termino, ignoreCase = true)
    }
}
