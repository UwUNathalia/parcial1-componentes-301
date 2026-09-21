package com.example.notaviva.data.local

import android.content.ContentValues
import android.database.Cursor
import com.example.notaviva.data.local.ContratoNotaViva.TablaCasos
import com.example.notaviva.data.local.ContratoNotaViva.TablaEntrevistas
import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.EstadoCaso
import java.time.LocalDate

/**
 * Acceso a la tabla de casos.
 *
 * Un DAO (Data Access Object) concentra todo el SQL de una tabla. Las capas de
 * arriba (repositorio, ViewModel, interfaz) nunca ven una consulta: solo piden
 * y reciben objetos [Caso].
 *
 * Todos los métodos son síncronos y bloquean el hilo desde el que se llaman.
 * El repositorio se encarga de sacarlos del hilo principal.
 */
class CasoDao(private val helper: NotaVivaDbHelper) {

    /**
     * Guarda un caso nuevo.
     *
     * @return el id que le asignó la base de datos, o -1 si falló la inserción.
     */
    fun insertar(caso: Caso): Long =
        helper.writableDatabase.insert(TablaCasos.NOMBRE, null, aContentValues(caso))

    /**
     * Actualiza un caso existente.
     *
     * @return cuántas filas se modificaron. Debe ser 1; un 0 significa que el
     *   id no existe.
     */
    fun actualizar(caso: Caso): Int =
        helper.writableDatabase.update(
            TablaCasos.NOMBRE,
            aContentValues(caso),
            "${TablaCasos.ID} = ?",
            arrayOf(caso.id.toString())
        )

    /**
     * Borra un caso. Las entrevistas y evidencias asociadas se eliminan solas
     * gracias al `ON DELETE CASCADE` del esquema.
     *
     * @return cuántas filas se borraron.
     */
    fun eliminar(id: Long): Int =
        helper.writableDatabase.delete(
            TablaCasos.NOMBRE,
            "${TablaCasos.ID} = ?",
            arrayOf(id.toString())
        )

    /** Cambia solo el estado de un caso, sin tocar el resto de los campos. */
    fun actualizarEstado(id: Long, estado: EstadoCaso): Int {
        val valores = ContentValues().apply { put(TablaCasos.ESTADO, estado.name) }
        return helper.writableDatabase.update(
            TablaCasos.NOMBRE,
            valores,
            "${TablaCasos.ID} = ?",
            arrayOf(id.toString())
        )
    }

    /** Guarda la conclusión del caso. */
    fun actualizarConclusion(id: Long, conclusion: String): Int {
        val valores = ContentValues().apply { put(TablaCasos.CONCLUSION, conclusion) }
        return helper.writableDatabase.update(
            TablaCasos.NOMBRE,
            valores,
            "${TablaCasos.ID} = ?",
            arrayOf(id.toString())
        )
    }

    /** Busca un caso por su id. Devuelve null si no existe. */
    fun obtenerPorId(id: Long): Caso? {
        val sql = "SELECT * FROM ${TablaCasos.NOMBRE} WHERE ${TablaCasos.ID} = ?"
        helper.readableDatabase.rawQuery(sql, arrayOf(id.toString())).use { cursor ->
            return if (cursor.moveToFirst()) aCaso(cursor) else null
        }
    }

    /** Todos los casos, del más reciente al más antiguo. */
    fun obtenerTodos(): List<Caso> {
        val sql = "SELECT * FROM ${TablaCasos.NOMBRE} ORDER BY ${TablaCasos.FECHA} DESC"
        return consultarLista(sql, emptyArray())
    }

    /**
     * Listado filtrado, que es lo que alimenta la pantalla de casos.
     *
     * @param consulta texto a buscar en título, descripción y tema. En blanco
     *   no filtra nada.
     * @param estado estado por el que filtrar. `null` significa "todos".
     */
    fun buscar(consulta: String = "", estado: EstadoCaso? = null): List<Caso> {
        val condiciones = mutableListOf<String>()
        val argumentos = mutableListOf<String>()

        if (consulta.isNotBlank()) {
            // LIKE con comodines a lado y lado busca el texto en cualquier parte.
            condiciones += "(${TablaCasos.TITULO} LIKE ? OR " +
                "${TablaCasos.DESCRIPCION} LIKE ? OR " +
                "${TablaCasos.TEMA} LIKE ?)"
            val patron = "%${consulta.trim()}%"
            repeat(3) { argumentos += patron }
        }

        if (estado != null) {
            condiciones += "${TablaCasos.ESTADO} = ?"
            argumentos += estado.name
        }

        val where = if (condiciones.isEmpty()) "" else "WHERE ${condiciones.joinToString(" AND ")}"
        val sql = "SELECT * FROM ${TablaCasos.NOMBRE} $where ORDER BY ${TablaCasos.FECHA} DESC"
        return consultarLista(sql, argumentos.toTypedArray())
    }

    /** Cuántos casos hay en total. Se usa en el resumen de la pantalla de inicio. */
    fun contarTodos(): Int {
        val sql = "SELECT COUNT(*) FROM ${TablaCasos.NOMBRE}"
        helper.readableDatabase.rawQuery(sql, null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    /**
     * Cuántos casos hay en cada estado, para los chips de filtro y el resumen.
     * Los estados sin casos no aparecen en el mapa.
     */
    fun contarPorEstado(): Map<EstadoCaso, Int> {
        val sql = """
            SELECT ${TablaCasos.ESTADO}, COUNT(*)
            FROM ${TablaCasos.NOMBRE}
            GROUP BY ${TablaCasos.ESTADO}
        """
        val conteos = mutableMapOf<EstadoCaso, Int>()
        helper.readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                conteos[EstadoCaso.desdeNombre(cursor.getString(0))] = cursor.getInt(1)
            }
        }
        return conteos
    }

    /**
     * Cuántas entrevistas tiene cada caso, en una sola consulta.
     *
     * Se hace así en vez de preguntar caso por caso para no disparar una
     * consulta por cada fila de la lista.
     */
    fun contarEntrevistasPorCaso(): Map<Long, Int> {
        val sql = """
            SELECT ${TablaEntrevistas.CASO_ID}, COUNT(*)
            FROM ${TablaEntrevistas.NOMBRE}
            GROUP BY ${TablaEntrevistas.CASO_ID}
        """
        val conteos = mutableMapOf<Long, Int>()
        helper.readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                conteos[cursor.getLong(0)] = cursor.getInt(1)
            }
        }
        return conteos
    }

    // ---------------------------------------------------------------------
    // Conversiones entre el modelo de dominio y las filas de la base de datos
    // ---------------------------------------------------------------------

    /** Ejecuta una consulta y convierte todas sus filas en objetos [Caso]. */
    private fun consultarLista(sql: String, argumentos: Array<String>): List<Caso> {
        val casos = mutableListOf<Caso>()
        helper.readableDatabase.rawQuery(sql, argumentos).use { cursor ->
            while (cursor.moveToNext()) {
                casos += aCaso(cursor)
            }
        }
        return casos
    }

    /** Convierte un [Caso] en el conjunto de columnas que entiende SQLite. */
    private fun aContentValues(caso: Caso) = ContentValues().apply {
        put(TablaCasos.TITULO, caso.titulo.trim())
        put(TablaCasos.DESCRIPCION, caso.descripcion.trim())
        put(TablaCasos.TEMA, caso.tema.trim())
        // La fecha se guarda en formato ISO (2026-09-19) porque ordenarlo como
        // texto da el mismo resultado que ordenarlo como fecha.
        put(TablaCasos.FECHA, caso.fecha.toString())
        put(TablaCasos.ESTADO, caso.estado.name)
        put(TablaCasos.CONCLUSION, caso.conclusion.trim())
    }

    /** Convierte la fila actual del cursor en un [Caso]. */
    private fun aCaso(cursor: Cursor) = Caso(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(TablaCasos.ID)),
        titulo = cursor.getString(cursor.getColumnIndexOrThrow(TablaCasos.TITULO)),
        descripcion = cursor.getString(cursor.getColumnIndexOrThrow(TablaCasos.DESCRIPCION)),
        tema = cursor.getString(cursor.getColumnIndexOrThrow(TablaCasos.TEMA)),
        fecha = aFecha(cursor.getString(cursor.getColumnIndexOrThrow(TablaCasos.FECHA))),
        estado = EstadoCaso.desdeNombre(
            cursor.getString(cursor.getColumnIndexOrThrow(TablaCasos.ESTADO))
        ),
        conclusion = cursor.getString(cursor.getColumnIndexOrThrow(TablaCasos.CONCLUSION))
    )

    /**
     * Lee una fecha guardada como texto. Si el dato está dañado se devuelve la
     * fecha de hoy en lugar de dejar que la aplicación se cierre.
     */
    private fun aFecha(texto: String?): LocalDate = try {
        LocalDate.parse(texto)
    } catch (e: Exception) {
        LocalDate.now()
    }
}
