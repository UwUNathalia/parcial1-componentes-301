package com.example.notaviva.ui.navigation

/**
 * Rutas de navegación de la aplicación.
 *
 * Están centralizadas para no escribir cadenas sueltas por todo el código: un
 * error de dedo en una ruta no lo detecta el compilador, solo se ve cuando la
 * navegación falla en tiempo de ejecución.
 */
object Rutas {

    /** Pantalla de inicio. */
    const val INICIO = "inicio"

    /** Listado de casos con búsqueda y filtros. */
    const val CASOS = "casos"

    /** Detalle de un caso. Recibe el id como argumento. */
    const val DETALLE = "caso/{casoId}"

    /**
     * Formulario de caso. Recibe el id como argumento: 0 significa "caso
     * nuevo" y cualquier otro valor significa "editar ese caso".
     */
    const val FORMULARIO = "formulario/{casoId}"

    /** Nombre del argumento que viaja en las rutas anteriores. */
    const val ARG_CASO_ID = "casoId"

    /** Construye la ruta concreta del detalle de un caso. */
    fun detalleDe(casoId: Long) = "caso/$casoId"

    /** Construye la ruta del formulario: sin argumento crea un caso nuevo. */
    fun formularioDe(casoId: Long = 0) = "formulario/$casoId"
}
