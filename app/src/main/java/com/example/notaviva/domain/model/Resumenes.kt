package com.example.notaviva.domain.model

/** Un caso acompañado de sus conteos. */
data class CasoConResumen(
    val caso: Caso,
    val entrevistas: Int = 0
)

data class ResumenGeneral(
    val totalCasos: Int = 0,
    val totalEntrevistas: Int = 0,
    val porEstado: Map<EstadoCaso, Int> = emptyMap()
) {

    fun cantidadEn(estado: EstadoCaso): Int = porEstado[estado] ?: 0

    val casosAbiertos: Int
        get() = totalCasos - cantidadEn(EstadoCaso.CERRADO)

    val casosCerrados: Int
        get() = cantidadEn(EstadoCaso.CERRADO)
}
