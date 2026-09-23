package com.example.notaviva.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notaviva.data.repository.RepositorioCasos

object Fabricas {

    fun casos(repositorio: RepositorioCasos): ViewModelProvider.Factory = viewModelFactory {
        initializer { CasosViewModel(repositorio) }
    }

    fun formulario(
        repositorio: RepositorioCasos,
        casoId: Long
    ): ViewModelProvider.Factory = viewModelFactory {
        initializer { FormularioCasoViewModel(repositorio, casoId) }
    }

    fun detalle(
        repositorio: RepositorioCasos,
        casoId: Long
    ): ViewModelProvider.Factory = viewModelFactory {
        initializer { DetalleCasoViewModel(repositorio, casoId) }
    }
}
