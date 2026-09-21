package com.example.notaviva.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Administra el archivo de base de datos de la aplicación.
 *
 * [SQLiteOpenHelper] se encarga de crear la base de datos la primera vez que
 * se abre y de aplicar las migraciones cuando cambia [VERSION].
 *
 * @param context contexto de la aplicación. Se usa `applicationContext` para
 *   no retener una Activity y provocar una fuga de memoria.
 */
class NotaVivaDbHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    NOMBRE_BD,
    null,
    VERSION
) {

    /**
     * Se llama antes de cada apertura de la base de datos.
     *
     * SQLite trae las llaves foráneas desactivadas por omisión, así que hay que
     * encenderlas para que funcione el borrado en cascada de entrevistas y
     * evidencias cuando se elimina un caso.
     */
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    /** Crea el esquema la primera vez que se instala la aplicación. */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ContratoNotaViva.TablaCasos.CREAR)
        db.execSQL(ContratoNotaViva.TablaEntrevistas.CREAR)
        db.execSQL(ContratoNotaViva.TablaEvidencias.CREAR)
        db.execSQL(ContratoNotaViva.CREAR_INDICE_TITULO)
    }

    /**
     * Migración entre versiones del esquema.
     *
     * Esta aplicación guarda información que el usuario puede volver a
     * registrar, así que la estrategia es recrear las tablas. En una
     * aplicación de producción aquí irían sentencias `ALTER TABLE` para no
     * perder los datos del usuario.
     */
    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        db.execSQL(ContratoNotaViva.TablaEvidencias.ELIMINAR)
        db.execSQL(ContratoNotaViva.TablaEntrevistas.ELIMINAR)
        db.execSQL(ContratoNotaViva.TablaCasos.ELIMINAR)
        onCreate(db)
    }

    /** Si se baja de versión se trata igual que una subida: se recrea todo. */
    override fun onDowngrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        onUpgrade(db, versionAnterior, versionNueva)
    }

    companion object {
        /** Nombre del archivo de base de datos dentro del almacenamiento de la app. */
        const val NOMBRE_BD = "notaviva.db"

        /** Subir este número dispara [onUpgrade] en la siguiente apertura. */
        const val VERSION = 1
    }
}
