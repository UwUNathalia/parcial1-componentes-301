package com.example.notaviva.data.local

import android.content.ContentValues
import android.database.Cursor
import com.example.notaviva.data.local.ContratoNotaViva.TablaCasos
import com.example.notaviva.data.local.ContratoNotaViva.TablaEntrevistas
import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.EstadoCaso
import java.time.LocalDate

/** Acceso a la tabla de casos. */
class CasoDao(private val helper: NotaVivaDbHelper) {


    fun insertar(caso: Caso): Long =
        helper.writableDatabase.insert(TablaCasos.NOMBRE, null, aContentValues(caso))


    fun actualizar(caso: Caso): Int =
        helper.writableDatabase.update(
            TablaCasos.NOMBRE,
            aContentValues(caso),
            "${TablaCasos.ID} = ?",
            arrayOf(caso.id.toString())
        )

    fun eliminar(id: Long): Int =
        helper.writableDatabase.delete(
            TablaCasos.NOMBRE,
            "${TablaCasos.ID} = ?",
            arrayOf(id.toString())
        )

    fun actualizarEstado(id: Long, estado: EstadoCaso): Int {
        val valores = ContentValues().apply { put(TablaCasos.ESTADO, estado.name) }
        return helper.writableDatabase.update(
            TablaCasos.NOMBRE,
            valores,
            "${TablaCasos.ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun actualizarConclusion(id: Long, conclusion: String): Int {
        val valores = ContentValues().apply { put(TablaCasos.CONCLUSION, conclusion) }
        return helper.writableDatabase.update(
            TablaCasos.NOMBRE,
            valores,
            "${TablaCasos.ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun obtenerPorId(id: Long): Caso? {
        val sql = "SELECT * FROM ${TablaCasos.NOMBRE} WHERE ${TablaCasos.ID} = ?"
        helper.readableDatabase.rawQuery(sql, arrayOf(id.toString())).use { cursor ->
            return if (cursor.moveToFirst()) aCaso(cursor) else null
        }
    }

    fun obtenerTodos(): List<Caso> {
        val sql = "SELECT * FROM ${TablaCasos.NOMBRE} ORDER BY ${TablaCasos.FECHA} DESC"
        return consultarLista(sql, emptyArray())
    }

    fun buscar(consulta: String = "", estado: EstadoCaso? = null): List<Caso> {
        val condiciones = mutableListOf<String>()
        val argumentos = mutableListOf<String>()

        if (consulta.isNotBlank()) {
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

    fun contarTodos(): Int {
        val sql = "SELECT COUNT(*) FROM ${TablaCasos.NOMBRE}"
        helper.readableDatabase.rawQuery(sql, null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    /** Cuántos casos hay en cada estado, para los chips de filtro y el resumen. */
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

    private fun consultarLista(sql: String, argumentos: Array<String>): List<Caso> {
        val casos = mutableListOf<Caso>()
        helper.readableDatabase.rawQuery(sql, argumentos).use { cursor ->
            while (cursor.moveToNext()) {
                casos += aCaso(cursor)
            }
        }
        return casos
    }

    private fun aContentValues(caso: Caso) = ContentValues().apply {
        put(TablaCasos.TITULO, caso.titulo.trim())
        put(TablaCasos.DESCRIPCION, caso.descripcion.trim())
        put(TablaCasos.TEMA, caso.tema.trim())
        put(TablaCasos.FECHA, caso.fecha.toString())
        put(TablaCasos.ESTADO, caso.estado.name)
        put(TablaCasos.CONCLUSION, caso.conclusion.trim())
    }

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

    private fun aFecha(texto: String?): LocalDate = try {
        LocalDate.parse(texto)
    } catch (e: Exception) {
        LocalDate.now()
    }
}
