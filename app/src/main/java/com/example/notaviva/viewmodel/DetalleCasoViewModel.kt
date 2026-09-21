package com.example.notaviva.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notaviva.data.repository.RepositorioCasos
import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.Entrevista
import com.example.notaviva.domain.model.Evidencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Estado de la pantalla de detalle del caso. */
data class EstadoDetalleCaso(
    val cargando: Boolean = true,
    val caso: Caso? = null,
    val entrevistas: List<Entrevista> = emptyList(),
    val evidencias: List<Evidencia> = emptyList(),
    val borradorConclusion: String = "",
    val mensaje: String? = null,
    val casoEliminado: Boolean = false
) {
    /** Un caso cerrado no admite nuevas entrevistas ni evidencias. */
    val estaCerrado: Boolean
        get() = caso?.estado?.estaCerrado == true
}

/**
 * ViewModel del detalle: entrevistas, hallazgos, evidencias y conclusión.
 *
 * Todas las operaciones que pueden fallar por una regla de negocio (agregar
 * información a un caso cerrado, por ejemplo) se ejecutan dentro de un
 * `try/catch` y el mensaje de la excepción se muestra al usuario, en lugar de
 * dejar que la aplicación se cierre.
 */
class DetalleCasoViewModel(
    private val repositorio: RepositorioCasos,
    private val casoId: Long
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoDetalleCaso())
    val estado: StateFlow<EstadoDetalleCaso> = _estado.asStateFlow()

    init {
        cargar()
    }

    /** Lee el caso con sus entrevistas y evidencias. */
    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val caso = repositorio.obtenerCaso(casoId)
            _estado.update {
                it.copy(
                    cargando = false,
                    caso = caso,
                    entrevistas = repositorio.listarEntrevistas(casoId),
                    evidencias = repositorio.listarEvidencias(casoId),
                    borradorConclusion = caso?.conclusion.orEmpty()
                )
            }
        }
    }

    // --- Entrevistas ---

    /** Registra una entrevista con sus hallazgos. */
    fun agregarEntrevista(
        nombre: String,
        rol: String,
        hallazgos: String,
        anonima: Boolean,
        fecha: LocalDate = LocalDate.now()
    ) {
        ejecutar {
            repositorio.guardarEntrevista(
                Entrevista(
                    casoId = casoId,
                    nombreEntrevistado = nombre,
                    rol = rol,
                    hallazgos = hallazgos,
                    anonima = anonima,
                    fecha = fecha
                )
            )
        }
    }

    /** Borra una entrevista del caso. */
    fun eliminarEntrevista(id: Long) {
        ejecutar { repositorio.eliminarEntrevista(id) }
    }

    // --- Evidencias ---

    /**
     * Registra una evidencia.
     *
     * @param tamanoKb tamaño en kilobytes tal como lo escribió el usuario; se
     *   convierte a bytes, que es como lo guarda el modelo.
     */
    fun agregarEvidencia(nombre: String, tamanoKb: Long) {
        ejecutar {
            repositorio.guardarEvidencia(
                Evidencia(
                    casoId = casoId,
                    nombre = nombre,
                    tipo = Evidencia.tipoSegunNombre(nombre),
                    tamanoBytes = tamanoKb * 1024
                )
            )
        }
    }

    /** Borra una evidencia del caso. */
    fun eliminarEvidencia(id: Long) {
        ejecutar { repositorio.eliminarEvidencia(id) }
    }

    // --- Conclusión y estado ---

    /** Actualiza el texto de la conclusión mientras el usuario escribe. */
    fun cambiarBorradorConclusion(texto: String) {
        _estado.update { it.copy(borradorConclusion = texto) }
    }

    /** Guarda la conclusión en la base de datos. */
    fun guardarConclusion(mensajeExito: String) {
        ejecutar(mensajeExito) {
            repositorio.guardarConclusion(casoId, _estado.value.borradorConclusion)
        }
    }

    /** Cambia el estado del caso. */
    fun cambiarEstado(estado: EstadoCaso) {
        ejecutar { repositorio.cambiarEstado(casoId, estado) }
    }

    /**
     * Cierra el caso, o lo reabre si ya estaba cerrado.
     *
     * Al reabrirlo vuelve a "en edición" y no a "en investigación", porque un
     * caso que llegó a cerrarse ya pasó por la etapa de recolección.
     */
    fun alternarCierre() {
        val actual = _estado.value.caso ?: return
        val nuevo = if (actual.estado.estaCerrado) EstadoCaso.EN_EDICION else EstadoCaso.CERRADO
        cambiarEstado(nuevo)
    }

    /** Elimina el caso completo. La vista navega hacia atrás al terminar. */
    fun eliminarCaso() {
        viewModelScope.launch {
            repositorio.eliminarCaso(casoId)
            _estado.update { it.copy(casoEliminado = true) }
        }
    }

    /** Oculta el mensaje emergente una vez mostrado. */
    fun mensajeMostrado() {
        _estado.update { it.copy(mensaje = null) }
    }

    /**
     * Ejecuta una operación sobre el repositorio y vuelve a cargar la pantalla.
     *
     * Centraliza el manejo de errores: si una regla de negocio rechaza la
     * operación, el mensaje se le muestra al usuario en vez de cerrar la app.
     */
    private fun ejecutar(mensajeExito: String? = null, operacion: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                operacion()
                if (mensajeExito != null) {
                    _estado.update { it.copy(mensaje = mensajeExito) }
                }
            } catch (e: Exception) {
                _estado.update { it.copy(mensaje = e.message) }
            }
            cargar()
        }
    }
}
