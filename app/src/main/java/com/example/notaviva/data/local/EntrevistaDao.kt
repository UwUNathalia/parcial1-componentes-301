package com.example.notaviva.data.local

import android.content.ContentValues
import android.database.Cursor
import com.example.notaviva.data.local.ContratoNotaViva.TablaEntrevistas
import com.example.notaviva.domain.model.Entrevista
import java.time.LocalDate

/**
 * Acceso a la tabla de entrevistas.
 *
 * Las entrevistas siempre se consultan dentro de un caso, por eso casi todos
 * los métodos reciben un `casoId`.
 */
class EntrevistaDao(private val helper: NotaVivaDbHelper) {

    /** Guarda una entrevista nueva y devuelve su id. */
    fun insertar(entrevista: Entrevista): Long =
        helper.writableDatabase.insert(
            TablaEntrevistas.NOMBRE,
            null,
            aContentValues(entrevista)
        )

    /** Actualiza una entrevista existente. */
    fun actualizar(entrevista: Entrevista): Int =
        helper.writableDatabase.update(
            TablaEntrevistas.NOMBRE,
            aContentValues(entrevista),
            "${TablaEntrevistas.ID} = ?",
            arrayOf(entrevista.id.toString())
        )

    /** Borra una entrevista por su id. */
    fun eliminar(id: Long): Int =
        helper.writableDatabase.delete(
            TablaEntrevistas.NOMBRE,
            "${TablaEntrevistas.ID} = ?",
            arrayOf(id.toString())
        )

    /** Entrevistas de un caso, de la más reciente a la más antigua. */
    fun obtenerPorCaso(casoId: Long): List<Entrevista> {
        val sql = """
            SELECT * FROM ${TablaEntrevistas.NOMBRE}
            WHERE ${TablaEntrevistas.CASO_ID} = ?
            ORDER BY ${TablaEntrevistas.FECHA} DESC
        """
        val entrevistas = mutableListOf<Entrevista>()
        helper.readableDatabase.rawQuery(sql, arrayOf(casoId.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                entrevistas += aEntrevista(cursor)
            }
        }
        return entrevistas
    }

    /** Busca una entrevista concreta. Devuelve null si no existe. */
    fun obtenerPorId(id: Long): Entrevista? {
        val sql = "SELECT * FROM ${TablaEntrevistas.NOMBRE} WHERE ${TablaEntrevistas.ID} = ?"
        helper.readableDatabase.rawQuery(sql, arrayOf(id.toString())).use { cursor ->
            return if (cursor.moveToFirst()) aEntrevista(cursor) else null
        }
    }

    /** Cuántas entrevistas tiene un caso. */
    fun contarPorCaso(casoId: Long): Int {
        val sql = """
            SELECT COUNT(*) FROM ${TablaEntrevistas.NOMBRE}
            WHERE ${TablaEntrevistas.CASO_ID} = ?
        """
        helper.readableDatabase.rawQuery(sql, arrayOf(casoId.toString())).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    /** Total de entrevistas registradas en la aplicación. */
    fun contarTodas(): Int {
        val sql = "SELECT COUNT(*) FROM ${TablaEntrevistas.NOMBRE}"
        helper.readableDatabase.rawQuery(sql, null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    private fun aContentValues(entrevista: Entrevista) = ContentValues().apply {
        put(TablaEntrevistas.CASO_ID, entrevista.casoId)
        put(TablaEntrevistas.ENTREVISTADO, entrevista.nombreEntrevistado.trim())
        put(TablaEntrevistas.ROL, entrevista.rol.trim())
        put(TablaEntrevistas.FECHA, entrevista.fecha.toString())
        put(TablaEntrevistas.HALLAZGOS, entrevista.hallazgos.trim())
        // SQLite no tiene tipo booleano: se guarda como 1 o 0.
        put(TablaEntrevistas.ANONIMA, if (entrevista.anonima) 1 else 0)
    }

    private fun aEntrevista(cursor: Cursor) = Entrevista(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(TablaEntrevistas.ID)),
        casoId = cursor.getLong(cursor.getColumnIndexOrThrow(TablaEntrevistas.CASO_ID)),
        nombreEntrevistado = cursor.getString(
            cursor.getColumnIndexOrThrow(TablaEntrevistas.ENTREVISTADO)
        ),
        rol = cursor.getString(cursor.getColumnIndexOrThrow(TablaEntrevistas.ROL)),
        fecha = aFecha(cursor.getString(cursor.getColumnIndexOrThrow(TablaEntrevistas.FECHA))),
        hallazgos = cursor.getString(cursor.getColumnIndexOrThrow(TablaEntrevistas.HALLAZGOS)),
        anonima = cursor.getInt(cursor.getColumnIndexOrThrow(TablaEntrevistas.ANONIMA)) == 1
    )

    private fun aFecha(texto: String?): LocalDate = try {
        LocalDate.parse(texto)
    } catch (e: Exception) {
        LocalDate.now()
    }
}
