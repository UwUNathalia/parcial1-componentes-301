package com.example.notaviva.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notaviva.data.repository.RepositorioCasos
import com.example.notaviva.domain.model.CasoConResumen
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.ResumenGeneral
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoListaCasos(
    val cargando: Boolean = true,
    val casos: List<CasoConResumen> = emptyList(),
    val consulta: String = "",
    val filtroEstado: EstadoCaso? = null,
    val resumen: ResumenGeneral = ResumenGeneral(),
    val mensaje: String? = null
) {
    val sinResultados: Boolean
        get() = !cargando && casos.isEmpty() && (consulta.isNotBlank() || filtroEstado != null)

    val sinCasos: Boolean
        get() = !cargando && casos.isEmpty() && consulta.isBlank() && filtroEstado == null
}

class CasosViewModel(
    private val repositorio: RepositorioCasos
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoListaCasos())

    val estado: StateFlow<EstadoListaCasos> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val actual = _estado.value
            val casos = repositorio.listarCasos(actual.consulta, actual.filtroEstado)
            val resumen = repositorio.obtenerResumen()
            _estado.update {
                it.copy(cargando = false, casos = casos, resumen = resumen)
            }
        }
    }

    /** El usuario escribió en el buscador. */
    fun buscar(consulta: String) {
        _estado.update { it.copy(consulta = consulta) }
        cargar()
    }

    /**
     * El usuario tocó un chip de filtro. Tocar el chip ya activo lo desactiva,
     * que es lo que la gente espera de un filtro de este tipo.
     */
    fun filtrarPor(estado: EstadoCaso?) {
        val nuevoFiltro = if (_estado.value.filtroEstado == estado) null else estado
        _estado.update { it.copy(filtroEstado = nuevoFiltro) }
        cargar()
    }

    /** Elimina un caso y recarga la lista. */
    fun eliminarCaso(id: Long, mensajeExito: String) {
        viewModelScope.launch {
            if (repositorio.eliminarCaso(id)) {
                _estado.update { it.copy(mensaje = mensajeExito) }
            }
            cargar()
        }
    }

    /** Oculta el mensaje emergente una vez que el usuario lo vio. */
    fun mensajeMostrado() {
        _estado.update { it.copy(mensaje = null) }
    }
}
