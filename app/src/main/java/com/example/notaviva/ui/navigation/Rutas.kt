package com.example.notaviva.ui.navigation

/** Rutas de navegación de la aplicación. */
object Rutas {

    const val INICIO = "inicio"

    const val CASOS = "casos"

    const val DETALLE = "caso/{casoId}"

    const val FORMULARIO = "formulario/{casoId}"

    const val ARG_CASO_ID = "casoId"

    fun detalleDe(casoId: Long) = "caso/$casoId"

    fun formularioDe(casoId: Long = 0) = "formulario/$casoId"
}
