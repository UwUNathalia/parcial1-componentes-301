package com.example.notaviva.domain.model

import java.time.LocalDate

/**
 * Entrevista realizada dentro de un caso.
 *
 * El enunciado pide registrar la entrevista junto con sus **principales
 * hallazgos**, que es la información que después alimenta la conclusión.
 *
 * @property id identificador asignado por la base de datos.
 * @property casoId caso al que pertenece la entrevista.
 * @property nombreEntrevistado nombre de la fuente. Si la entrevista es
 *   anónima se guarda vacío y la interfaz muestra "Entrevista anónima".
 * @property rol cargo o relación de la fuente con el caso, por ejemplo
 *   "Exfuncionaria" o "Contratista".
 * @property fecha día en que se hizo la entrevista.
 * @property hallazgos principales hallazgos obtenidos.
 * @property anonima indica si la fuente pidió reserva de identidad.
 */
data class Entrevista(
    val id: Long = 0,
    val casoId: Long,
    val nombreEntrevistado: String = "",
    val rol: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val hallazgos: String = "",
    val anonima: Boolean = false
) {

    /**
     * Una entrevista es válida si pertenece a un caso guardado y tiene
     * identificada la fuente, salvo que sea anónima.
     */
    val esValida: Boolean
        get() = casoId != 0L && (anonima || nombreEntrevistado.isNotBlank())

    /** True si la entrevista ya tiene hallazgos registrados. */
    val tieneHallazgos: Boolean
        get() = hallazgos.isNotBlank()
}
