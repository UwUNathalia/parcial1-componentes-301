package com.example.notaviva

import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.viewmodel.FormularioCasoViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas de la validación del formulario.
 *
 * Verifican la parte síncrona del ViewModel: la validación ocurre antes de
 * lanzar cualquier corrutina, así que se puede comprobar directamente sin
 * necesidad de emulador ni de un hilo principal de Android.
 */
class FormularioCasoViewModelTest {

    private fun crearViewModel() = FormularioCasoViewModel(RepositorioFalso(), casoId = 0)

    @Test
    fun `el formulario empieza vacio y sin errores`() {
        val viewModel = crearViewModel()
        val estado = viewModel.estado.value

        assertEquals("", estado.titulo)
        assertFalse(estado.tituloInvalido)
        assertFalse(estado.esEdicion)
        assertEquals(EstadoCaso.EN_INVESTIGACION, estado.estado)
    }

    @Test
    fun `guardar sin titulo marca el error y no guarda`() {
        val viewModel = crearViewModel()

        viewModel.guardar()

        assertTrue(viewModel.estado.value.tituloInvalido)
        assertFalse(viewModel.estado.value.guardado)
    }

    @Test
    fun `un titulo de solo espacios tambien es invalido`() {
        val viewModel = crearViewModel()

        viewModel.cambiarTitulo("    ")
        viewModel.guardar()

        assertTrue(viewModel.estado.value.tituloInvalido)
    }

    @Test
    fun `escribir en el titulo apaga el error anterior`() {
        val viewModel = crearViewModel()

        viewModel.guardar()
        assertTrue(viewModel.estado.value.tituloInvalido)

        viewModel.cambiarTitulo("Corrupción en la obra pública")

        assertFalse(viewModel.estado.value.tituloInvalido)
    }

    @Test
    fun `los campos del formulario conservan lo que se escribe`() {
        val viewModel = crearViewModel()
        val fecha = LocalDate.of(2026, 3, 12)

        viewModel.cambiarTitulo("Deforestación en la Amazonía")
        viewModel.cambiarDescripcion("Seguimiento a la tala ilegal")
        viewModel.cambiarTema("Ambiente")
        viewModel.cambiarFecha(fecha)
        viewModel.cambiarEstado(EstadoCaso.EN_EDICION)

        val estado = viewModel.estado.value
        assertEquals("Deforestación en la Amazonía", estado.titulo)
        assertEquals("Seguimiento a la tala ilegal", estado.descripcion)
        assertEquals("Ambiente", estado.tema)
        assertEquals(fecha, estado.fecha)
        assertEquals(EstadoCaso.EN_EDICION, estado.estado)
    }
}
