package com.example.notaviva.data.repository

import android.content.Context
import com.example.notaviva.data.local.CasoDao
import com.example.notaviva.data.local.EntrevistaDao
import com.example.notaviva.data.local.EvidenciaDao
import com.example.notaviva.data.local.NotaVivaDbHelper
import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.CasoConResumen
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.Entrevista
import com.example.notaviva.domain.model.Evidencia
import com.example.notaviva.domain.model.ResumenGeneral
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación de [RepositorioCasos] sobre SQLite.
 *
 * Es la única clase de la aplicación que conoce los DAOs. Su trabajo es:
 *
 * - sacar las consultas del hilo principal con [withContext],
 * - aplicar las reglas de negocio que involucran a más de una tabla,
 * - y combinar datos de varias consultas antes de entregarlos a la vista.
 *
 * @param dispatcher hilo en el que se ejecutan las consultas. Se recibe como
 *   parámetro para poder sustituirlo en las pruebas.
 */
class CasoRepository(
    private val casoDao: CasoDao,
    private val entrevistaDao: EntrevistaDao,
    private val evidenciaDao: EvidenciaDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RepositorioCasos {

    // ------------------------------------------------------------------
    // Casos
    // ------------------------------------------------------------------

    override suspend fun listarCasos(
        consulta: String,
        estado: EstadoCaso?
    ): List<CasoConResumen> = withContext(dispatcher) {
        val casos = casoDao.buscar(consulta, estado)
        // Un solo conteo agrupado para toda la lista, en vez de una consulta
        // por cada caso.
        val conteos = casoDao.contarEntrevistasPorCaso()
        casos.map { caso ->
            CasoConResumen(caso = caso, entrevistas = conteos[caso.id] ?: 0)
        }
    }

    override suspend fun obtenerCaso(id: Long): Caso? = withContext(dispatcher) {
        casoDao.obtenerPorId(id)
    }

    override suspend fun guardarCaso(caso: Caso): Long = withContext(dispatcher) {
        require(caso.esValido) { "El caso debe tener un título." }
        if (caso.esNuevo) {
            casoDao.insertar(caso)
        } else {
            casoDao.actualizar(caso)
            caso.id
        }
    }

    override suspend fun eliminarCaso(id: Long): Boolean = withContext(dispatcher) {
        // Las entrevistas y evidencias se borran solas por el ON DELETE CASCADE
        // declarado en el esquema.
        casoDao.eliminar(id) > 0
    }

    override suspend fun cambiarEstado(id: Long, estado: EstadoCaso): Boolean =
        withContext(dispatcher) {
            casoDao.actualizarEstado(id, estado) > 0
        }

    override suspend fun guardarConclusion(id: Long, conclusion: String): Boolean =
        withContext(dispatcher) {
            casoDao.actualizarConclusion(id, conclusion.trim()) > 0
        }

    // ------------------------------------------------------------------
    // Entrevistas
    // ------------------------------------------------------------------

    override suspend fun listarEntrevistas(casoId: Long): List<Entrevista> =
        withContext(dispatcher) {
            entrevistaDao.obtenerPorCaso(casoId)
        }

    override suspend fun guardarEntrevista(entrevista: Entrevista): Long =
        withContext(dispatcher) {
            require(entrevista.esValida) {
                "La entrevista debe indicar la fuente o marcarse como anónima."
            }
            verificarCasoAbierto(entrevista.casoId)
            if (entrevista.id == 0L) {
                entrevistaDao.insertar(entrevista)
            } else {
                entrevistaDao.actualizar(entrevista)
                entrevista.id
            }
        }

    override suspend fun eliminarEntrevista(id: Long): Boolean = withContext(dispatcher) {
        entrevistaDao.eliminar(id) > 0
    }

    // ------------------------------------------------------------------
    // Evidencias
    // ------------------------------------------------------------------

    override suspend fun listarEvidencias(casoId: Long): List<Evidencia> =
        withContext(dispatcher) {
            evidenciaDao.obtenerPorCaso(casoId)
        }

    override suspend fun guardarEvidencia(evidencia: Evidencia): Long =
        withContext(dispatcher) {
            require(evidencia.esValida) { "La evidencia debe tener un nombre de archivo." }
            verificarCasoAbierto(evidencia.casoId)
            evidenciaDao.insertar(evidencia)
        }

    override suspend fun eliminarEvidencia(id: Long): Boolean = withContext(dispatcher) {
        evidenciaDao.eliminar(id) > 0
    }

    // ------------------------------------------------------------------
    // Resumen
    // ------------------------------------------------------------------

    override suspend fun obtenerResumen(): ResumenGeneral = withContext(dispatcher) {
        ResumenGeneral(
            totalCasos = casoDao.contarTodos(),
            totalEntrevistas = entrevistaDao.contarTodas(),
            porEstado = casoDao.contarPorEstado()
        )
    }

    /**
     * Regla de negocio: un caso cerrado no admite nuevas entrevistas ni
     * evidencias. Para seguir trabajándolo hay que reabrirlo cambiando su
     * estado.
     */
    private fun verificarCasoAbierto(casoId: Long) {
        val caso = casoDao.obtenerPorId(casoId)
            ?: throw IllegalStateException("El caso no existe.")
        check(!caso.estado.estaCerrado) {
            "El caso está cerrado. Reábrelo para poder agregar información."
        }
    }

    companion object {

        @Volatile
        private var instancia: CasoRepository? = null

        /**
         * Devuelve el repositorio compartido por toda la aplicación.
         *
         * Se guarda una sola instancia para no abrir la base de datos una vez
         * por pantalla. Se usa `applicationContext` para no retener una
         * Activity y provocar una fuga de memoria.
         */
        fun obtener(context: Context): CasoRepository =
            instancia ?: synchronized(this) {
                instancia ?: crear(context.applicationContext).also { instancia = it }
            }

        private fun crear(context: Context): CasoRepository {
            val helper = NotaVivaDbHelper(context)
            return CasoRepository(
                casoDao = CasoDao(helper),
                entrevistaDao = EntrevistaDao(helper),
                evidenciaDao = EvidenciaDao(helper)
            )
        }
    }
}
