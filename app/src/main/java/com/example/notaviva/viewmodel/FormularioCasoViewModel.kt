package com.example.notaviva.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notaviva.data.repository.RepositorioCasos
import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.EstadoCaso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Estado del formulario de creación y edición.
 *
 * @property tituloInvalido se enciende solo cuando el usuario ya intentó
 *   guardar. Mostrar el error apenas abre la pantalla, con los campos todavía
 *   vacíos, resulta agresivo sin aportar nada.
 */
data class EstadoFormularioCaso(
    val id: Long = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val tema: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val estado: EstadoCaso = EstadoCaso.EN_INVESTIGACION,
    val tituloInvalido: Boolean = false,
    val guardado: Boolean = false,
    val cargando: Boolean = false
) {
    /** True cuando se está editando un caso que ya existe. */
    val esEdicion: Boolean
        get() = id != 0L
}

/**
 * ViewModel del formulario de caso.
 *
 * La misma pantalla sirve para crear y para editar: si recibe un id distinto
 * de cero, carga ese caso y lo actualiza; si no, crea uno nuevo.
 */
class FormularioCasoViewModel(
    private val repositorio: RepositorioCasos,
    private val casoId: Long
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoFormularioCaso(id = casoId))
    val estado: StateFlow<EstadoFormularioCaso> = _estado.asStateFlow()

    init {
        if (casoId != 0L) cargarCaso()
    }

    /** Trae de la base de datos el caso que se va a editar. */
    private fun cargarCaso() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val caso = repositorio.obtenerCaso(casoId)
            if (caso != null) {
                _estado.update {
                    it.copy(
                        id = caso.id,
                        titulo = caso.titulo,
                        descripcion = caso.descripcion,
                        tema = caso.tema,
                        fecha = caso.fecha,
                        estado = caso.estado,
                        cargando = false
                    )
                }
            } else {
                _estado.update { it.copy(cargando = false) }
            }
        }
    }

    fun cambiarTitulo(valor: String) {
        // Si el usuario ya está corrigiendo, se apaga el error de inmediato.
        _estado.update { it.copy(titulo = valor, tituloInvalido = false) }
    }

    fun cambiarDescripcion(valor: String) {
        _estado.update { it.copy(descripcion = valor) }
    }

    fun cambiarTema(valor: String) {
        _estado.update { it.copy(tema = valor) }
    }

    fun cambiarFecha(valor: LocalDate) {
        _estado.update { it.copy(fecha = valor) }
    }

    fun cambiarEstado(valor: EstadoCaso) {
        _estado.update { it.copy(estado = valor) }
    }

    /**
     * Valida y guarda.
     *
     * La validación se hace aquí y no en la vista, para que las pruebas
     * unitarias puedan comprobarla sin necesidad de un emulador.
     */
    fun guardar() {
        val actual = _estado.value
        if (actual.titulo.isBlank()) {
            _estado.update { it.copy(tituloInvalido = true) }
            return
        }

        viewModelScope.launch {
            val caso = Caso(
                id = actual.id,
                titulo = actual.titulo,
                descripcion = actual.descripcion,
                tema = actual.tema,
                fecha = actual.fecha,
                estado = actual.estado
            )
            repositorio.guardarCaso(caso)
            _estado.update { it.copy(guardado = true) }
        }
    }
}
