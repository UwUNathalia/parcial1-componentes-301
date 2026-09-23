package com.example.notaviva

import com.example.notaviva.domain.model.Caso
import com.example.notaviva.domain.model.Entrevista
import com.example.notaviva.domain.model.EstadoCaso
import com.example.notaviva.domain.model.Evidencia
import com.example.notaviva.domain.model.TipoEvidencia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelosTest {

    // --- Caso ---

    @Test
    fun `un caso sin titulo no es valido`() {
        val caso = Caso(titulo = "   ")
        assertFalse(caso.esValido)
    }

    @Test
    fun `un caso con titulo es valido`() {
        val caso = Caso(titulo = "Corrupción en la obra pública")
        assertTrue(caso.esValido)
    }

    @Test
    fun `un caso sin id se considera nuevo`() {
        assertTrue(Caso(titulo = "Nuevo").esNuevo)
        assertFalse(Caso(id = 7, titulo = "Guardado").esNuevo)
    }

    @Test
    fun `la busqueda encuentra el texto en el titulo sin importar mayusculas`() {
        val caso = Caso(titulo = "Deforestación en la Amazonía")
        assertTrue(caso.coincideCon("amazon"))
        assertTrue(caso.coincideCon("DEFORESTACIÓN"))
    }

    @Test
    fun `la busqueda tambien mira la descripcion y el tema`() {
        val caso = Caso(
            titulo = "Voces estudiantiles",
            descripcion = "Testimonios sobre la reforma",
            tema = "Educación"
        )
        assertTrue(caso.coincideCon("reforma"))
        assertTrue(caso.coincideCon("educación"))
    }

    @Test
    fun `una busqueda vacia devuelve todos los casos`() {
        val caso = Caso(titulo = "Cualquiera")
        assertTrue(caso.coincideCon(""))
        assertTrue(caso.coincideCon("    "))
    }

    @Test
    fun `la busqueda no encuentra lo que no esta`() {
        val caso = Caso(titulo = "Transporte y ciudad")
        assertFalse(caso.coincideCon("presupuesto"))
    }

    // --- EstadoCaso ---

    @Test
    fun `solo el estado cerrado cuenta como cerrado`() {
        assertTrue(EstadoCaso.CERRADO.estaCerrado)
        assertFalse(EstadoCaso.EN_INVESTIGACION.estaCerrado)
        assertFalse(EstadoCaso.EN_EDICION.estaCerrado)
        assertFalse(EstadoCaso.PUBLICADO.estaCerrado)
    }

    @Test
    fun `un estado desconocido en la base de datos no rompe la aplicacion`() {
        assertEquals(EstadoCaso.EN_INVESTIGACION, EstadoCaso.desdeNombre("BASURA"))
        assertEquals(EstadoCaso.EN_INVESTIGACION, EstadoCaso.desdeNombre(null))
        assertEquals(EstadoCaso.PUBLICADO, EstadoCaso.desdeNombre("PUBLICADO"))
    }

    // --- Entrevista ---

    @Test
    fun `una entrevista sin nombre solo vale si es anonima`() {
        val sinNombre = Entrevista(casoId = 1, nombreEntrevistado = "")
        assertFalse(sinNombre.esValida)
        assertTrue(sinNombre.copy(anonima = true).esValida)
    }

    @Test
    fun `una entrevista sin caso no es valida`() {
        val huerfana = Entrevista(casoId = 0, nombreEntrevistado = "Ana Torres")
        assertFalse(huerfana.esValida)
    }

    // --- Evidencia ---

    @Test
    fun `el tipo de evidencia se deduce de la extension`() {
        assertEquals(TipoEvidencia.DOCUMENTO, Evidencia.tipoSegunNombre("contrato.pdf"))
        assertEquals(TipoEvidencia.IMAGEN, Evidencia.tipoSegunNombre("foto_obra.JPG"))
        assertEquals(TipoEvidencia.AUDIO, Evidencia.tipoSegunNombre("entrevista1.mp3"))
        assertEquals(TipoEvidencia.VIDEO, Evidencia.tipoSegunNombre("video.mp4"))
        assertEquals(TipoEvidencia.OTRO, Evidencia.tipoSegunNombre("archivo_sin_extension"))
    }

    @Test
    fun `el tamano se muestra en la unidad adecuada`() {
        assertEquals("512 B", Evidencia(casoId = 1, nombre = "a", tamanoBytes = 512).tamanoLegible)
        assertEquals("2 KB", Evidencia(casoId = 1, nombre = "a", tamanoBytes = 2048).tamanoLegible)
        assertEquals(
            "1.0 MB",
            Evidencia(casoId = 1, nombre = "a", tamanoBytes = 1024L * 1024).tamanoLegible
        )
    }

    @Test
    fun `una evidencia sin tamano muestra un guion`() {
        assertEquals("-", Evidencia(casoId = 1, nombre = "a", tamanoBytes = 0).tamanoLegible)
    }
}
