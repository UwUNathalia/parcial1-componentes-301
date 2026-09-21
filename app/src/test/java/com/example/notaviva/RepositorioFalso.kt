package com.example.notaviva

import com.example.notaviva.data.repository.RepositorioCasos
import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.CasoConResumen
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.Entrevista
import com.example.notaviva.domain.model.Evidencia
import com.example.notaviva.domain.model.ResumenGeneral

/**
 * Repositorio de mentira que guarda todo en memoria.
 *
 * Gracias a que los ViewModels dependen de la interfaz [RepositorioCasos] y no
 * de la implementación con SQLite, las pruebas pueden correr en la JVM, sin
 * emulador y sin base de datos. Esa es la razón práctica de haber definido la
 * interfaz.
 */
class RepositorioFalso : RepositorioCasos {

    private val casos = mutableListOf<Caso>()
    private val entrevistas = mutableListOf<Entrevista>()
    private val evidencias = mutableListOf<Evidencia>()
    private var siguienteId = 1L

    override suspend fun listarCasos(
        consulta: String,
        estado: EstadoCaso?
    ): List<CasoConResumen> = casos
        .filter { it.coincideCon(consulta) }
        .filter { estado == null || it.estado == estado }
        .map { caso ->
            CasoConResumen(
                caso = caso,
                entrevistas = entrevistas.count { it.casoId == caso.id }
            )
        }

    override suspend fun obtenerCaso(id: Long): Caso? = casos.firstOrNull { it.id == id }

    override suspend fun guardarCaso(caso: Caso): Long {
        require(caso.esValido) { "El caso debe tener un título." }
        return if (caso.esNuevo) {
            val nuevo = caso.copy(id = siguienteId++)
            casos += nuevo
            nuevo.id
        } else {
            val indice = casos.indexOfFirst { it.id == caso.id }
            if (indice >= 0) casos[indice] = caso
            caso.id
        }
    }

    override suspend fun eliminarCaso(id: Long): Boolean {
        // Se imita el borrado en cascada del esquema de SQLite.
        entrevistas.removeAll { it.casoId == id }
        evidencias.removeAll { it.casoId == id }
        return casos.removeAll { it.id == id }
    }

    override suspend fun cambiarEstado(id: Long, estado: EstadoCaso): Boolean {
        val indice = casos.indexOfFirst { it.id == id }
        if (indice < 0) return false
        casos[indice] = casos[indice].copy(estado = estado)
        return true
    }

    override suspend fun guardarConclusion(id: Long, conclusion: String): Boolean {
        val indice = casos.indexOfFirst { it.id == id }
        if (indice < 0) return false
        casos[indice] = casos[indice].copy(conclusion = conclusion.trim())
        return true
    }

    override suspend fun listarEntrevistas(casoId: Long): List<Entrevista> =
        entrevistas.filter { it.casoId == casoId }

    override suspend fun guardarEntrevista(entrevista: Entrevista): Long {
        require(entrevista.esValida) {
            "La entrevista debe indicar la fuente o marcarse como anónima."
        }
        verificarAbierto(entrevista.casoId)
        val nueva = entrevista.copy(id = siguienteId++)
        entrevistas += nueva
        return nueva.id
    }

    override suspend fun eliminarEntrevista(id: Long): Boolean =
        entrevistas.removeAll { it.id == id }

    override suspend fun listarEvidencias(casoId: Long): List<Evidencia> =
        evidencias.filter { it.casoId == casoId }

    override suspend fun guardarEvidencia(evidencia: Evidencia): Long {
        require(evidencia.esValida) { "La evidencia debe tener un nombre de archivo." }
        verificarAbierto(evidencia.casoId)
        val nueva = evidencia.copy(id = siguienteId++)
        evidencias += nueva
        return nueva.id
    }

    override suspend fun eliminarEvidencia(id: Long): Boolean =
        evidencias.removeAll { it.id == id }

    override suspend fun obtenerResumen(): ResumenGeneral = ResumenGeneral(
        totalCasos = casos.size,
        totalEntrevistas = entrevistas.size,
        porEstado = casos.groupingBy { it.estado }.eachCount()
    )

    private fun verificarAbierto(casoId: Long) {
        val caso = casos.firstOrNull { it.id == casoId }
            ?: throw IllegalStateException("El caso no existe.")
        check(!caso.estado.estaCerrado) { "El caso está cerrado." }
    }
}
