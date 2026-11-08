package com.example.territorydashboard.ui.painel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.territorydashboard.domain.modelo.NivelAlerta
import com.example.territorydashboard.domain.modelo.OrigemDado
import com.example.territorydashboard.nativo.CartaoPermissaoNotificacao
import com.example.territorydashboard.ui.componentes.AlertCard
import com.example.territorydashboard.ui.componentes.AvisoDados
import com.example.territorydashboard.ui.componentes.KpiCard
import com.example.territorydashboard.ui.componentes.StateView
import com.example.territorydashboard.ui.componentes.TerritorySelector
import com.example.territorydashboard.ui.componentes.TipoEstado
import com.example.territorydashboard.ui.componentes.formatarData
import com.example.territorydashboard.ui.theme.Espaco

@Composable
fun PainelScreen(
    modifier: Modifier = Modifier,
    onAbrirIndicador: (territorioId: String, indicadorId: String) -> Unit = { _, _ -> },
    onAbrirAlerta: (territorioId: String, alertaId: String) -> Unit = { _, _ -> },
    viewModel: PainelViewModel = viewModel(factory = PainelViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // roda toda vez que o painel volta a aparecer (ex.: voltando das configuracoes)
    LaunchedEffect(Unit) {
        viewModel.conferirFavorito()
    }

    when (val e = estado) {
        is PainelUiState.Carregando ->
            StateView(TipoEstado.CARREGANDO, "Carregando dados do território...", modifier)

        is PainelUiState.Erro ->
            StateView(TipoEstado.ERRO, e.mensagem, modifier, onTentarNovamente = { viewModel.carregar() })

        is PainelUiState.Vazio ->
            StateView(TipoEstado.VAZIO, e.mensagem, modifier)

        is PainelUiState.Sucesso ->
            ConteudoPainel(
                estado = e,
                onSelecionarTerritorio = { id -> viewModel.selecionarTerritorio(id) },
                onAbrirIndicador = onAbrirIndicador,
                onAbrirAlerta = onAbrirAlerta,
                onPermissaoConcedida = { viewModel.notificarCriticos() },
                modifier = modifier
            )
    }
}

@Composable
private fun ConteudoPainel(
    estado: PainelUiState.Sucesso,
    onSelecionarTerritorio: (String) -> Unit,
    onAbrirIndicador: (String, String) -> Unit,
    onAbrirAlerta: (String, String) -> Unit,
    onPermissaoConcedida: () -> Unit,
    modifier: Modifier = Modifier
) {
    val painel = estado.painel
    val territorioId = painel.territorio.id
    val sintetico = painel.indicadores.any { it.historico.lastOrNull()?.origem == OrigemDado.SINTETICA }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Espaco.grande),
        verticalArrangement = Arrangement.spacedBy(Espaco.medio)
    ) {
        item {
            TerritorySelector(
                territorios = estado.territorios,
                selecionadoId = territorioId,
                onSelecionar = onSelecionarTerritorio
            )
        }

        item {
            Text(painel.territorio.nome, style = MaterialTheme.typography.headlineSmall)
            val data = if (painel.ultimaAtualizacao == null) "sem dados" else formatarData(painel.ultimaAtualizacao)
            val origem = if (sintetico) " | Dados sintéticos" else ""
            Text(
                "Atualizado em $data$origem",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (estado.usandoDadosSalvos) {
            item { AvisoDados("Não foi possível atualizar. Mostrando os últimos dados salvos.") }
        }

        if (painel.desatualizado) {
            item { AvisoDados("Dados desatualizados: o app não emite alertas para este território até receber dados novos.") }
        }
        // so pede a permissao quando ela faz sentido: quando existe alerta critico
        if (painel.alertas.any { it.nivel == NivelAlerta.CRITICO }) {
            item {
                CartaoPermissaoNotificacao(
                    mostrarQuandoConcedida = false,
                    onConcedida = onPermissaoConcedida
                )
            }
        }

        item {
            Text("Alertas ativos", style = MaterialTheme.typography.titleLarge)
        }
        if (painel.alertas.isEmpty()) {
            item {
                Text(
                    "Nenhum alerta ativo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(painel.alertas) { alerta ->
            AlertCard(
                nivel = alerta.nivel,
                titulo = alerta.titulo,
                confianca = alerta.confianca,
                periodo = alerta.periodoPrevisto,
                onClick = { onAbrirAlerta(territorioId, alerta.id) }
            )
        }

        item {
            Text("Indicadores", style = MaterialTheme.typography.titleLarge)
        }
        items(painel.indicadores) { resumo ->
            KpiCard(
                titulo = resumo.indicador.nome,
                valor = resumo.valorAtual,
                unidade = resumo.indicador.unidade,
                variacao = resumo.variacaoSemana,
                status = resumo.status,
                onClick = { onAbrirIndicador(territorioId, resumo.indicador.id) }
            )
        }
    }
}