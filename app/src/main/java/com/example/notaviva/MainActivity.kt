package com.example.notaviva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notaviva.data.repository.CasoRepository
import com.example.notaviva.ui.navigation.NavegacionNotaViva
import com.example.notaviva.ui.theme.NotaVivaTheme

/**
 * Única Activity de la aplicación.
 *
 * Todo lo demás son composables dentro del grafo de navegación. Su trabajo se
 * reduce a montar el tema, construir el repositorio y entregar el control a
 * [NavegacionNotaViva].
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // El repositorio se crea una sola vez y se comparte con toda la
        // aplicación. Es la inyección de dependencias manual del proyecto: la
        // interfaz no sabe que por debajo hay SQLite.
        val repositorio = CasoRepository.obtener(applicationContext)

        setContent {
            NotaVivaTheme {
                NavegacionNotaViva(repositorio = repositorio)
            }
        }
    }
}
