package com.example.notaviva.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Administra el archivo de base de datos de la aplicación. */
class NotaVivaDbHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    NOMBRE_BD,
    null,
    VERSION
) {


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

    /** Migración entre versiones del esquema. */
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
