package com.example.notaviva.data.local

import android.provider.BaseColumns

/**Nombres de tablas y columnas de la base de datos. */
object ContratoNotaViva {

    object TablaCasos : BaseColumns {
        const val NOMBRE = "casos"
        const val ID = BaseColumns._ID
        const val TITULO = "titulo"
        const val DESCRIPCION = "descripcion"
        const val TEMA = "tema"
        const val FECHA = "fecha"
        const val ESTADO = "estado"
        const val CONCLUSION = "conclusion"

        const val CREAR = """
            CREATE TABLE $NOMBRE (
                $ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TITULO TEXT NOT NULL,
                $DESCRIPCION TEXT NOT NULL DEFAULT '',
                $TEMA TEXT NOT NULL DEFAULT '',
                $FECHA TEXT NOT NULL,
                $ESTADO TEXT NOT NULL DEFAULT 'EN_INVESTIGACION',
                $CONCLUSION TEXT NOT NULL DEFAULT ''
            )
        """

        const val ELIMINAR = "DROP TABLE IF EXISTS $NOMBRE"
    }

    object TablaEntrevistas : BaseColumns {
        const val NOMBRE = "entrevistas"
        const val ID = BaseColumns._ID
        const val CASO_ID = "caso_id"
        const val ENTREVISTADO = "entrevistado"
        const val ROL = "rol"
        const val FECHA = "fecha"
        const val HALLAZGOS = "hallazgos"
        const val ANONIMA = "anonima"

        const val CREAR = """
            CREATE TABLE $NOMBRE (
                $ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CASO_ID INTEGER NOT NULL,
                $ENTREVISTADO TEXT NOT NULL DEFAULT '',
                $ROL TEXT NOT NULL DEFAULT '',
                $FECHA TEXT NOT NULL,
                $HALLAZGOS TEXT NOT NULL DEFAULT '',
                $ANONIMA INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY ($CASO_ID) REFERENCES ${TablaCasos.NOMBRE}(${TablaCasos.ID})
                    ON DELETE CASCADE
            )
        """

        const val ELIMINAR = "DROP TABLE IF EXISTS $NOMBRE"
    }

    object TablaEvidencias : BaseColumns {
        const val NOMBRE = "evidencias"
        const val ID = BaseColumns._ID
        const val CASO_ID = "caso_id"
        const val NOMBRE_ARCHIVO = "nombre_archivo"
        const val TIPO = "tipo"
        const val TAMANO = "tamano"
        const val RUTA = "ruta"

        const val CREAR = """
            CREATE TABLE $NOMBRE (
                $ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CASO_ID INTEGER NOT NULL,
                $NOMBRE_ARCHIVO TEXT NOT NULL,
                $TIPO TEXT NOT NULL DEFAULT 'OTRO',
                $TAMANO INTEGER NOT NULL DEFAULT 0,
                $RUTA TEXT NOT NULL DEFAULT '',
                FOREIGN KEY ($CASO_ID) REFERENCES ${TablaCasos.NOMBRE}(${TablaCasos.ID})
                    ON DELETE CASCADE
            )
        """

        const val ELIMINAR = "DROP TABLE IF EXISTS $NOMBRE"
    }

    /** Índice sobre el título para que la búsqueda no recorra toda la tabla cuando haya muchos casos. */
    const val CREAR_INDICE_TITULO =
        "CREATE INDEX idx_casos_titulo ON ${TablaCasos.NOMBRE}(${TablaCasos.TITULO})"
}
