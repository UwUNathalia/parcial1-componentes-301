package com.example.notaviva.data.repository

import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.CasoConResumen
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.Entrevista
import com.example.notaviva.domain.model.Evidencia
import com.example.notaviva.domain.model.ResumenGeneral

/**
 * Contrato de acceso a datos de la aplicación.
 *
 * Los ViewModels dependen de esta interfaz y no de la implementación, por dos
 * razones:
 *
 * 1. Se puede cambiar SQLite por otra fuente de datos sin tocar la lógica.
 * 2. En las pruebas unitarias se usa una implementación falsa en memoria, sin
 *    necesidad de un dispositivo ni de un emulador.
 *
 * Todas las operaciones son `suspend` porque tocan disco y no deben correr en
 * el hilo principal.
 */
interface RepositorioCasos {

    // --- Casos ---

    /**
     * Listado de casos con sus conteos, aplicando búsqueda y filtro.
     *
     * @param consulta texto a buscar. En blanco no filtra.
     * @param estado estado por el que filtrar, o `null` para todos.
     */
    suspend fun listarCasos(
        consulta: String = "",
        estado: EstadoCaso? = null
    ): List<CasoConResumen>

    /** Un caso por su id, o `null` si no existe. */
    suspend fun obtenerCaso(id: Long): Caso?

    /**
     * Crea el caso si es nuevo, o lo actualiza si ya existe.
     *
     * @return el id del caso guardado.
     * @throws IllegalArgumentException si el caso no tiene título.
     */
    suspend fun guardarCaso(caso: Caso): Long

    /** Elimina el caso junto con sus entrevistas y evidencias. */
    suspend fun eliminarCaso(id: Long): Boolean

    /** Cambia el estado del caso (incluido el cierre). */
    suspend fun cambiarEstado(id: Long, estado: EstadoCaso): Boolean

    /** Guarda la conclusión del caso. */
    suspend fun guardarConclusion(id: Long, conclusion: String): Boolean

    // --- Entrevistas ---

    /** Entrevistas de un caso. */
    suspend fun listarEntrevistas(casoId: Long): List<Entrevista>

    /**
     * Crea o actualiza una entrevista.
     *
     * @throws IllegalArgumentException si la entrevista no identifica la fuente
     *   y tampoco está marcada como anónima.
     * @throws IllegalStateException si el caso ya está cerrado.
     */
    suspend fun guardarEntrevista(entrevista: Entrevista): Long

    /** Elimina una entrevista. */
    suspend fun eliminarEntrevista(id: Long): Boolean

    // --- Evidencias ---

    /** Evidencias de un caso. */
    suspend fun listarEvidencias(casoId: Long): List<Evidencia>

    /**
     * Registra una evidencia.
     *
     * @throws IllegalArgumentException si la evidencia no tiene nombre.
     * @throws IllegalStateException si el caso ya está cerrado.
     */
    suspend fun guardarEvidencia(evidencia: Evidencia): Long

    /** Elimina una evidencia. */
    suspend fun eliminarEvidencia(id: Long): Boolean

    // --- Resumen ---

    /** Cifras generales para la pantalla de inicio. */
    suspend fun obtenerResumen(): ResumenGeneral
}
