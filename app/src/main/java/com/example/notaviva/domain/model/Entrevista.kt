package com.example.notaviva.domain.model

import java.time.LocalDate

/** Entrevista realizada dentro de un caso. */
data class Entrevista(
    val id: Long = 0,
    val casoId: Long,
    val nombreEntrevistado: String = "",
    val rol: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val hallazgos: String = "",
    val anonima: Boolean = false
) {

    /** Una entrevista es válida si pertenece a un caso guardado y tiene identificada la fuente, salvo que sea anónima. */
    val esValida: Boolean
        get() = casoId != 0L && (anonima || nombreEntrevistado.isNotBlank())

    val tieneHallazgos: Boolean
        get() = hallazgos.isNotBlank()
}
