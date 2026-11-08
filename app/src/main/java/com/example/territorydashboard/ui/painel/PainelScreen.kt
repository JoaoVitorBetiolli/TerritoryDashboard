package com.example.territorydashboard.ui.painel

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// versao provisoria: so para ver os dados chegando na tela.
// na fase de componentes ela vai ganhar KpiCard, AlertCard etc.
@Composable
fun PainelScreen(
    modifier: Modifier = Modifier,
    viewModel: PainelViewModel = viewModel(factory = PainelViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    when (val e = estado) {
        is PainelUiState.Carregando -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is PainelUiState.Erro -> {
            Column(
                modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(e.mensagem)
                Button(onClick = { viewModel.carregar() }) {
                    Text("Tentar novamente")
                }
            }
        }

        is PainelUiState.Vazio -> {
            Box(modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(e.mensagem)
            }
        }

        is PainelUiState.Sucesso -> {
            ConteudoPainel(
                estado = e,
                onSelecionarTerritorio = { id -> viewModel.selecionarTerritorio(id) },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ConteudoPainel(
    estado: PainelUiState.Sucesso,
    onSelecionarTerritorio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val painel = estado.painel

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                estado.territorios.forEach { t ->
                    FilterChip(
                        selected = t.id == painel.territorio.id,
                        onClick = { onSelecionarTerritorio(t.id) },
                        label = { Text(t.nome) }
                    )
                }
            }
        }

        item {
            Text(painel.territorio.nome, style = MaterialTheme.typography.headlineSmall)
            Text("Última atualização: ${painel.ultimaAtualizacao ?: "sem dados"}")
        }

        if (estado.usandoDadosSalvos) {
            item {
                Text("Não foi possível atualizar. Mostrando os últimos dados salvos.")
            }
        }

        if (painel.desatualizado) {
            item {
                Text(
                    "Dados desatualizados: o app não emite alertas para este território.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        item {
            Text("Indicadores", style = MaterialTheme.typography.titleMedium)
        }
        items(painel.indicadores) { resumo ->
            Text("${resumo.indicador.nome}: ${resumo.valorAtual ?: "-"} ${resumo.indicador.unidade} (${resumo.status})")
        }

        item {
            Text("Alertas", style = MaterialTheme.typography.titleMedium)
        }
        if (painel.alertas.isEmpty()) {
            item { Text("Nenhum alerta ativo.") }
        }
        items(painel.alertas) { alerta ->
            Text("${alerta.nivel} - ${alerta.titulo} (confiança ${alerta.confianca}%)")
        }
    }
}