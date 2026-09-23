package com.example.notaviva.data.repository

import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.CasoConResumen
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.Entrevista
import com.example.notaviva.domain.model.Evidencia
import com.example.notaviva.domain.model.ResumenGeneral

/** Contrato de acceso a datos de la aplicación. */
interface RepositorioCasos {

    /** Listado de casos con sus conteos, aplicando búsqueda y filtro. */
    suspend fun listarCasos(
        consulta: String = "",
        estado: EstadoCaso? = null
    ): List<CasoConResumen>

    /** Un caso por su id, o `null` si no existe. */
    suspend fun obtenerCaso(id: Long): Caso?

    /** Crea el caso si es nuevo, o lo actualiza si ya existe. */
    suspend fun guardarCaso(caso: Caso): Long

    /** Elimina el caso junto con sus entrevistas y evidencias. */
    suspend fun eliminarCaso(id: Long): Boolean

    /** Cambia el estado del caso. */
    suspend fun cambiarEstado(id: Long, estado: EstadoCaso): Boolean

    /** Guarda la conclusión del caso. */
    suspend fun guardarConclusion(id: Long, conclusion: String): Boolean

    /** Entrevistas de un caso. */
    suspend fun listarEntrevistas(casoId: Long): List<Entrevista>

    /** Crea o actualiza una entrevista. */
    suspend fun guardarEntrevista(entrevista: Entrevista): Long

    /** Elimina una entrevista. */
    suspend fun eliminarEntrevista(id: Long): Boolean

    /** Evidencias de un caso. */
    suspend fun listarEvidencias(casoId: Long): List<Evidencia>

    /** Registra una evidencia. */
    suspend fun guardarEvidencia(evidencia: Evidencia): Long

    /** Elimina una evidencia. */
    suspend fun eliminarEvidencia(id: Long): Boolean

    /** Cifras generales para la pantalla de inicio. */
    suspend fun obtenerResumen(): ResumenGeneral
}
