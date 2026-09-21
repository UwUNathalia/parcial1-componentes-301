package com.example.notaviva.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notaviva.data.repository.RepositorioCasos

/**
 * Fábricas de ViewModels.
 *
 * Los ViewModels de esta aplicación reciben el repositorio por el constructor
 * (inyección de dependencias manual), y Android no sabe construirlos solo. Una
 * fábrica le explica cómo hacerlo.
 *
 * La ventaja de pasar el repositorio en vez de crearlo dentro del ViewModel es
 * que en las pruebas se puede inyectar uno falso.
 */
object Fabricas {

    /** Fábrica del ViewModel del listado y la pantalla de inicio. */
    fun casos(repositorio: RepositorioCasos): ViewModelProvider.Factory = viewModelFactory {
        initializer { CasosViewModel(repositorio) }
    }

    /**
     * Fábrica del formulario.
     *
     * @param casoId 0 para crear un caso nuevo, o el id del caso a editar.
     */
    fun formulario(
        repositorio: RepositorioCasos,
        casoId: Long
    ): ViewModelProvider.Factory = viewModelFactory {
        initializer { FormularioCasoViewModel(repositorio, casoId) }
    }

    /** Fábrica del detalle de un caso. */
    fun detalle(
        repositorio: RepositorioCasos,
        casoId: Long
    ): ViewModelProvider.Factory = viewModelFactory {
        initializer { DetalleCasoViewModel(repositorio, casoId) }
    }
}
