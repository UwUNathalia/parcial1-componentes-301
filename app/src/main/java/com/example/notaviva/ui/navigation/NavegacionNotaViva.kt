package com.example.notaviva.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notaviva.R
import com.example.notaviva.data.repository.RepositorioCasos
import com.example.notaviva.ui.cases.PantallaCasos
import com.example.notaviva.ui.detail.PantallaDetalleCaso
import com.example.notaviva.ui.form.PantallaFormularioCaso
import com.example.notaviva.ui.home.PantallaInicio
import com.example.notaviva.viewmodel.CasosViewModel
import com.example.notaviva.viewmodel.DetalleCasoViewModel
import com.example.notaviva.viewmodel.Fabricas
import com.example.notaviva.viewmodel.FormularioCasoViewModel

/** Grafo de navegación de la aplicación. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavegacionNotaViva(repositorio: RepositorioCasos) {
    val navegador = rememberNavController()
    val entradaActual by navegador.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val anfitrionMensajes = remember { SnackbarHostState() }

    val casosViewModel: CasosViewModel = viewModel(factory = Fabricas.casos(repositorio))
    val estadoCasos by casosViewModel.estado.collectAsStateWithLifecycle()

    LaunchedEffect(rutaActual) {
        if (rutaActual == Rutas.INICIO || rutaActual == Rutas.CASOS) {
            casosViewModel.cargar()
        }
    }

    LaunchedEffect(estadoCasos.mensaje) {
        estadoCasos.mensaje?.let {
            anfitrionMensajes.showSnackbar(it)
            casosViewModel.mensajeMostrado()
        }
    }

    val enPantallaPrincipal = rutaActual == Rutas.INICIO || rutaActual == Rutas.CASOS

    Scaffold(
        topBar = {
            if (rutaActual == Rutas.CASOS) {
                TopAppBar(title = { Text(stringResource(R.string.cases_title)) })
            }
        },
        bottomBar = {
            if (enPantallaPrincipal) {
                BarraInferior(
                    rutaActual = rutaActual,
                    alNavegar = { ruta ->
                        navegador.navigate(ruta) {
                            launchSingleTop = true
                            popUpTo(Rutas.INICIO)
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (rutaActual == Rutas.CASOS) {
                FloatingActionButton(
                    onClick = { navegador.navigate(Rutas.formularioDe()) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.home_new_case)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(anfitrionMensajes) }
    ) { relleno ->

        NavHost(
            navController = navegador,
            startDestination = Rutas.INICIO,
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
        ) {

            composable(Rutas.INICIO) {
                PantallaInicio(
                    resumen = estadoCasos.resumen,
                    alCrearCaso = { navegador.navigate(Rutas.formularioDe()) },
                    alVerCasos = { navegador.navigate(Rutas.CASOS) }
                )
            }

            composable(Rutas.CASOS) {
                PantallaCasos(
                    estado = estadoCasos,
                    alBuscar = casosViewModel::buscar,
                    alFiltrar = casosViewModel::filtrarPor,
                    alAbrirCaso = { id -> navegador.navigate(Rutas.detalleDe(id)) }
                )
            }

            composable(
                route = Rutas.FORMULARIO,
                arguments = listOf(
                    navArgument(Rutas.ARG_CASO_ID) { type = NavType.LongType }
                )
            ) { entrada ->
                val casoId = entrada.arguments?.getLong(Rutas.ARG_CASO_ID) ?: 0L
                val formularioViewModel: FormularioCasoViewModel = viewModel(
                    factory = Fabricas.formulario(repositorio, casoId)
                )
                val estadoFormulario by formularioViewModel.estado.collectAsStateWithLifecycle()

                LaunchedEffect(estadoFormulario.guardado) {
                    if (estadoFormulario.guardado) navegador.popBackStack()
                }

                PantallaFormularioCaso(
                    estado = estadoFormulario,
                    alCambiarTitulo = formularioViewModel::cambiarTitulo,
                    alCambiarDescripcion = formularioViewModel::cambiarDescripcion,
                    alCambiarTema = formularioViewModel::cambiarTema,
                    alCambiarFecha = formularioViewModel::cambiarFecha,
                    alCambiarEstado = formularioViewModel::cambiarEstado,
                    alGuardar = formularioViewModel::guardar,
                    alVolver = { navegador.popBackStack() }
                )
            }

            composable(
                route = Rutas.DETALLE,
                arguments = listOf(
                    navArgument(Rutas.ARG_CASO_ID) { type = NavType.LongType }
                )
            ) { entrada ->
                val casoId = entrada.arguments?.getLong(Rutas.ARG_CASO_ID) ?: 0L
                val detalleViewModel: DetalleCasoViewModel = viewModel(
                    factory = Fabricas.detalle(repositorio, casoId)
                )
                val estadoDetalle by detalleViewModel.estado.collectAsStateWithLifecycle()
                val conclusionGuardada = stringResource(R.string.conclusion_saved)

                PantallaDetalleCaso(
                    estado = estadoDetalle,
                    alVolver = { navegador.popBackStack() },
                    alEditar = { navegador.navigate(Rutas.formularioDe(casoId)) },
                    alEliminarCaso = detalleViewModel::eliminarCaso,
                    alAlternarCierre = detalleViewModel::alternarCierre,
                    alAgregarEntrevista = { nombre, rol, hallazgos, anonima ->
                        detalleViewModel.agregarEntrevista(nombre, rol, hallazgos, anonima)
                    },
                    alEliminarEntrevista = detalleViewModel::eliminarEntrevista,
                    alAgregarEvidencia = detalleViewModel::agregarEvidencia,
                    alEliminarEvidencia = detalleViewModel::eliminarEvidencia,
                    alCambiarConclusion = detalleViewModel::cambiarBorradorConclusion,
                    alGuardarConclusion = {
                        detalleViewModel.guardarConclusion(conclusionGuardada)
                    },
                    alMensajeMostrado = detalleViewModel::mensajeMostrado
                )
            }
        }
    }
}

/** Barra de navegación inferior de la aplicación. */
@Composable
private fun BarraInferior(
    rutaActual: String?,
    alNavegar: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = rutaActual == Rutas.INICIO,
            onClick = { alNavegar(Rutas.INICIO) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_home)) }
        )
        NavigationBarItem(
            selected = rutaActual == Rutas.CASOS,
            onClick = { alNavegar(Rutas.CASOS) },
            icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_cases)) }
        )
    }
}
