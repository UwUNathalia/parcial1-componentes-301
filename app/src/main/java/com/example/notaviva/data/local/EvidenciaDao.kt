package com.example.notaviva.data.local

import android.content.ContentValues
import android.database.Cursor
import com.example.notaviva.data.local.ContratoNotaViva.TablaEvidencias
import com.example.notaviva.domain.model.Evidencia
import com.example.notaviva.domain.model.TipoEvidencia

/**
 * Acceso a la tabla de evidencias.
 *
 * Aquí solo se guarda la ficha del archivo (nombre, tipo, tamaño y ruta),
 * nunca el contenido del archivo.
 */
class EvidenciaDao(private val helper: NotaVivaDbHelper) {

    /** Guarda una evidencia nueva y devuelve su id. */
    fun insertar(evidencia: Evidencia): Long =
        helper.writableDatabase.insert(
            TablaEvidencias.NOMBRE,
            null,
            aContentValues(evidencia)
        )

    /** Borra una evidencia por su id. */
    fun eliminar(id: Long): Int =
        helper.writableDatabase.delete(
            TablaEvidencias.NOMBRE,
            "${TablaEvidencias.ID} = ?",
            arrayOf(id.toString())
        )

    /** Evidencias de un caso, en el orden en que se agregaron. */
    fun obtenerPorCaso(casoId: Long): List<Evidencia> {
        val sql = """
            SELECT * FROM ${TablaEvidencias.NOMBRE}
            WHERE ${TablaEvidencias.CASO_ID} = ?
            ORDER BY ${TablaEvidencias.ID} ASC
        """
        val evidencias = mutableListOf<Evidencia>()
        helper.readableDatabase.rawQuery(sql, arrayOf(casoId.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                evidencias += aEvidencia(cursor)
            }
        }
        return evidencias
    }

    /** Cuántas evidencias tiene un caso. */
    fun contarPorCaso(casoId: Long): Int {
        val sql = """
            SELECT COUNT(*) FROM ${TablaEvidencias.NOMBRE}
            WHERE ${TablaEvidencias.CASO_ID} = ?
        """
        helper.readableDatabase.rawQuery(sql, arrayOf(casoId.toString())).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    private fun aContentValues(evidencia: Evidencia) = ContentValues().apply {
        put(TablaEvidencias.CASO_ID, evidencia.casoId)
        put(TablaEvidencias.NOMBRE_ARCHIVO, evidencia.nombre.trim())
        put(TablaEvidencias.TIPO, evidencia.tipo.name)
        put(TablaEvidencias.TAMANO, evidencia.tamanoBytes)
        put(TablaEvidencias.RUTA, evidencia.rutaLocal)
    }

    private fun aEvidencia(cursor: Cursor) = Evidencia(
        id = cursor.getLong(cursor.getColumnIndexOrThrow(TablaEvidencias.ID)),
        casoId = cursor.getLong(cursor.getColumnIndexOrThrow(TablaEvidencias.CASO_ID)),
        nombre = cursor.getString(cursor.getColumnIndexOrThrow(TablaEvidencias.NOMBRE_ARCHIVO)),
        tipo = TipoEvidencia.desdeNombre(
            cursor.getString(cursor.getColumnIndexOrThrow(TablaEvidencias.TIPO))
        ),
        tamanoBytes = cursor.getLong(cursor.getColumnIndexOrThrow(TablaEvidencias.TAMANO)),
        rutaLocal = cursor.getString(cursor.getColumnIndexOrThrow(TablaEvidencias.RUTA))
    )
}
