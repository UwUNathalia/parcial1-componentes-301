package com.example.notaviva

import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.Entrevista
import com.example.notaviva.domain.model.Evidencia
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas de las reglas de negocio del repositorio.
 *
 * Se ejecutan contra [RepositorioFalso], que reproduce el mismo contrato que
 * la implementación con SQLite. De esta forma las reglas quedan verificadas
 * sin necesidad de un dispositivo.
 */
class ReglasDeNegocioTest {

    private lateinit var repositorio: RepositorioFalso

    @Before
    fun preparar() {
        repositorio = RepositorioFalso()
    }

    @Test
    fun `guardar un caso le asigna un identificador`() = runBlocking {
        val id = repositorio.guardarCaso(Caso(titulo = "Corrupción en la obra pública"))
        assertTrue(id > 0)
        assertNotNull(repositorio.obtenerCaso(id))
    }

    @Test
    fun `no se puede guardar un caso sin titulo`() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { repositorio.guardarCaso(Caso(titulo = "")) }
        }
    }

    @Test
    fun `editar un caso no crea uno nuevo`() = runBlocking {
        val id = repositorio.guardarCaso(Caso(titulo = "Título original"))
        repositorio.guardarCaso(Caso(id = id, titulo = "Título corregido"))

        assertEquals(1, repositorio.listarCasos().size)
        assertEquals("Título corregido", repositorio.obtenerCaso(id)?.titulo)
    }

    @Test
    fun `eliminar un caso borra tambien sus entrevistas y evidencias`() = runBlocking {
        val id = repositorio.guardarCaso(Caso(titulo = "Caso con material"))
        repositorio.guardarEntrevista(Entrevista(casoId = id, nombreEntrevistado = "Ana Torres"))
        repositorio.guardarEvidencia(Evidencia(casoId = id, nombre = "contrato.pdf"))

        repositorio.eliminarCaso(id)

        assertNull(repositorio.obtenerCaso(id))
        assertTrue(repositorio.listarEntrevistas(id).isEmpty())
        assertTrue(repositorio.listarEvidencias(id).isEmpty())
    }

    @Test
    fun `un caso cerrado no acepta nuevas entrevistas`() {
        runBlocking {
            val id = repositorio.guardarCaso(Caso(titulo = "Caso terminado"))
            repositorio.cambiarEstado(id, EstadoCaso.CERRADO)

            assertThrows(IllegalStateException::class.java) {
                runBlocking {
                    repositorio.guardarEntrevista(
                        Entrevista(casoId = id, nombreEntrevistado = "Carlos Méndez")
                    )
                }
            }
        }
    }

    @Test
    fun `al reabrir el caso se pueden volver a agregar entrevistas`() = runBlocking {
        val id = repositorio.guardarCaso(Caso(titulo = "Caso reabierto"))
        repositorio.cambiarEstado(id, EstadoCaso.CERRADO)
        repositorio.cambiarEstado(id, EstadoCaso.EN_EDICION)

        repositorio.guardarEntrevista(Entrevista(casoId = id, nombreEntrevistado = "Laura Silva"))

        assertEquals(1, repositorio.listarEntrevistas(id).size)
    }

    @Test
    fun `la busqueda filtra por texto`() = runBlocking {
        repositorio.guardarCaso(Caso(titulo = "Deforestación en la Amazonía"))
        repositorio.guardarCaso(Caso(titulo = "Transporte y ciudad"))

        val resultados = repositorio.listarCasos(consulta = "transporte")

        assertEquals(1, resultados.size)
        assertEquals("Transporte y ciudad", resultados.first().caso.titulo)
    }

    @Test
    fun `la busqueda se puede combinar con el filtro de estado`() = runBlocking {
        val publicado = repositorio.guardarCaso(Caso(titulo = "Informe de obras"))
        repositorio.cambiarEstado(publicado, EstadoCaso.PUBLICADO)
        repositorio.guardarCaso(Caso(titulo = "Informe de contratos"))

        val soloPublicados = repositorio.listarCasos(
            consulta = "informe",
            estado = EstadoCaso.PUBLICADO
        )

        assertEquals(1, soloPublicados.size)
        assertEquals("Informe de obras", soloPublicados.first().caso.titulo)
    }

    @Test
    fun `el listado informa cuantas entrevistas tiene cada caso`() = runBlocking {
        val id = repositorio.guardarCaso(Caso(titulo = "Caso con tres fuentes"))
        repeat(3) { indice ->
            repositorio.guardarEntrevista(
                Entrevista(casoId = id, nombreEntrevistado = "Fuente $indice")
            )
        }

        assertEquals(3, repositorio.listarCasos().first().entrevistas)
    }

    @Test
    fun `el resumen cuenta casos abiertos y entrevistas`() = runBlocking {
        val abierto = repositorio.guardarCaso(Caso(titulo = "Abierto"))
        val cerrado = repositorio.guardarCaso(Caso(titulo = "Cerrado"))
        repositorio.cambiarEstado(cerrado, EstadoCaso.CERRADO)
        repositorio.guardarEntrevista(Entrevista(casoId = abierto, nombreEntrevistado = "Ana"))

        val resumen = repositorio.obtenerResumen()

        assertEquals(2, resumen.totalCasos)
        assertEquals(1, resumen.casosAbiertos)
        assertEquals(1, resumen.totalEntrevistas)
    }

    @Test
    fun `la conclusion se guarda sin espacios sobrantes`() = runBlocking {
        val id = repositorio.guardarCaso(Caso(titulo = "Caso con conclusión"))
        repositorio.guardarConclusion(id, "  Las entrevistas coinciden.  ")

        assertEquals("Las entrevistas coinciden.", repositorio.obtenerCaso(id)?.conclusion)
    }
}
