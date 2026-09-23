package com.example.notaviva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notaviva.data.repository.CasoRepository
import com.example.notaviva.ui.navigation.NavegacionNotaViva
import com.example.notaviva.ui.theme.NotaVivaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repositorio = CasoRepository.obtener(applicationContext)

        setContent {
            NotaVivaTheme {
                NavegacionNotaViva(repositorio = repositorio)
            }
        }
    }
}
