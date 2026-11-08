package com.example.territorydashboard.ui.configuracoes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.territorydashboard.domain.modelo.Tema
import com.example.territorydashboard.ui.componentes.StateView
import com.example.territorydashboard.ui.componentes.TerritorySelector
import com.example.territorydashboard.ui.componentes.TipoEstado
import com.example.territorydashboard.ui.componentes.formatarNumero
import com.example.territorydashboard.ui.theme.Espaco

@Composable
fun ConfiguracoesScreen(
    modifier: Modifier = Modifier,
    viewModel: ConfiguracoesViewModel = viewModel(factory = ConfiguracoesViewModel.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var confirmarApagar by rememberSaveable { mutableStateOf(false) }

    if (estado.carregando) {
        StateView(TipoEstado.CARREGANDO, "Carregando configurações...", modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Espaco.grande),
        verticalArrangement = Arrangement.spacedBy(Espaco.medio)
    ) {
        item {
            Titulo("Território padrão")
            Text(
                "É o território que o Painel abre primeiro.",
                style = MaterialTheme.typography.bodyMedium
            )
            TerritorySelector(
                territorios = estado.territorios,
                selecionadoId = estado.favoritoId,
                onSelecionar = { id -> viewModel.escolherFavorito(id) }
            )
        }

        item {
            Titulo("Temas monitorados e limiares")
            Text(
                "Os limiares são definidos pelo curador de dados e vêm junto com a base de dados.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        items(estado.indicadores) { ind ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Espaco.grande)) {
                    val tema = if (ind.tema == Tema.ENERGIA) "Energia" else "Clima"
                    Text("$tema | ${ind.nome}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Atenção a partir de ${formatarNumero(ind.limiarAtencao)} ${ind.unidade}  |  " +
                                "Crítico a partir de ${formatarNumero(ind.limiarCritico)} ${ind.unidade}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Titulo("Fontes de dados")
            Text(
                "Todos os dados deste app são sintéticos (fictícios), gerados pelo script " +
                        "gerar_dados.py com semente fixa. Não há dados pessoais. Os bairros e valores " +
                        "foram inventados para fins acadêmicos.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            Titulo("Privacidade")
            Text(
                "As decisões ficam salvas só neste aparelho. Você pode apagá-las a qualquer momento.",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(
                onClick = { confirmarApagar = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apagar histórico de decisões")
            }
            val mensagem = estado.mensagem
            if (mensagem != null) {
                Text(mensagem, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    if (confirmarApagar) {
        AlertDialog(
            onDismissRequest = { confirmarApagar = false },
            title = { Text("Apagar histórico?") },
            text = { Text("Todas as decisões registradas serão apagadas deste aparelho. Isso não pode ser desfeito.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.apagarDecisoes()
                    confirmarApagar = false
                }) { Text("Apagar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarApagar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = Espaco.medio)
    )
}