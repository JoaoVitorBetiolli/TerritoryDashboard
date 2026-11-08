package com.example.territorydashboard.ui.navegacao

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.territorydashboard.ui.indicador.IndicadorScreen
import com.example.territorydashboard.ui.painel.PainelScreen

// as rotas levam so os ids, nunca o objeto inteiro
object Rotas {
    const val PAINEL = "painel"
    const val INDICADOR = "indicador/{territorioId}/{indicadorId}"

    fun indicador(territorioId: String, indicadorId: String) = "indicador/$territorioId/$indicadorId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacao() {
    val navController = rememberNavController()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    val titulo = when (rotaAtual) {
        Rotas.INDICADOR -> "Detalhe do indicador"
        else -> "Radar do Bairro"
    }
    val podeVoltar = rotaAtual != Rotas.PAINEL

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (podeVoltar) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.PAINEL,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.PAINEL) {
                PainelScreen(
                    onAbrirIndicador = { territorioId, indicadorId ->
                        navController.navigate(Rotas.indicador(territorioId, indicadorId))
                    }
                )
            }

            composable(Rotas.INDICADOR) {
                IndicadorScreen()
            }
        }
    }
}