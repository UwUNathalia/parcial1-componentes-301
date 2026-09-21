package com.example.notaviva.domain.model

/**
 * Un caso acompañado de sus conteos, que es lo que necesita la tarjeta del
 * listado: además del caso hay que mostrar cuántas entrevistas lleva.
 *
 * Se arma en el repositorio para que la vista no tenga que pedir los conteos
 * por separado.
 */
data class CasoConResumen(
    val caso: Caso,
    val entrevistas: Int = 0
)

/**
 * Cifras generales de la aplicación, para la pantalla de inicio y la de
 * estadísticas.
 *
 * @property totalCasos casos registrados.
 * @property totalEntrevistas entrevistas registradas en todos los casos.
 * @property porEstado cuántos casos hay en cada estado.
 */
data class ResumenGeneral(
    val totalCasos: Int = 0,
    val totalEntrevistas: Int = 0,
    val porEstado: Map<EstadoCaso, Int> = emptyMap()
) {

    /** Cuántos casos hay en un estado concreto, 0 si no hay ninguno. */
    fun cantidadEn(estado: EstadoCaso): Int = porEstado[estado] ?: 0

    /** Casos que siguen abiertos, es decir, todos los que no están cerrados. */
    val casosAbiertos: Int
        get() = totalCasos - cantidadEn(EstadoCaso.CERRADO)
}
