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
    val estaCerrado: Boolean
        get() = caso?.estado?.estaCerrado == true
}

class DetalleCasoViewModel(
    private val repositorio: RepositorioCasos,
    private val casoId: Long
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoDetalleCaso())
    val estado: StateFlow<EstadoDetalleCaso> = _estado.asStateFlow()

    init {
        cargar()
    }

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

    fun eliminarEntrevista(id: Long) {
        ejecutar { repositorio.eliminarEntrevista(id) }
    }

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

    fun eliminarEvidencia(id: Long) {
        ejecutar { repositorio.eliminarEvidencia(id) }
    }

    fun cambiarBorradorConclusion(texto: String) {
        _estado.update { it.copy(borradorConclusion = texto) }
    }

    fun guardarConclusion(mensajeExito: String) {
        ejecutar(mensajeExito) {
            repositorio.guardarConclusion(casoId, _estado.value.borradorConclusion)
        }
    }

    fun cambiarEstado(estado: EstadoCaso) {
        ejecutar { repositorio.cambiarEstado(casoId, estado) }
    }

    fun alternarCierre() {
        val actual = _estado.value.caso ?: return
        val nuevo = if (actual.estado.estaCerrado) EstadoCaso.EN_EDICION else EstadoCaso.CERRADO
        cambiarEstado(nuevo)
    }

    fun eliminarCaso() {
        viewModelScope.launch {
            repositorio.eliminarCaso(casoId)
            _estado.update { it.copy(casoEliminado = true) }
        }
    }

    fun mensajeMostrado() {
        _estado.update { it.copy(mensaje = null) }
    }

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
