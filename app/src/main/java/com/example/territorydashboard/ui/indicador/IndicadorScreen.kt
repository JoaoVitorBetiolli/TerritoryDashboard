package com.example.territorydashboard.ui.indicador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.territorydashboard.domain.modelo.OrigemDado
import com.example.territorydashboard.domain.previsao.DIAS_PREVISAO
import com.example.territorydashboard.ui.componentes.AlertCard
import com.example.territorydashboard.ui.componentes.AvisoDados
import com.example.territorydashboard.ui.componentes.KpiCard
import com.example.territorydashboard.ui.componentes.StateView
import com.example.territorydashboard.ui.componentes.TipoEstado
import com.example.territorydashboard.ui.componentes.TrendChart
import com.example.territorydashboard.ui.componentes.formatarData
import com.example.territorydashboard.ui.componentes.formatarNumero
import com.example.territorydashboard.ui.theme.Espaco

@Composable
fun IndicadorScreen(
    modifier: Modifier = Modifier,
    onAbrirAlerta: (territorioId: String, alertaId: String) -> Unit = { _, _ -> },
    viewModel: IndicadorViewModel = viewModel(factory = IndicadorViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    when (val e = estado) {
        is IndicadorUiState.Carregando ->
            StateView(TipoEstado.CARREGANDO, "Carregando indicador...", modifier)

        is IndicadorUiState.Erro ->
            StateView(TipoEstado.ERRO, e.mensagem, modifier, onTentarNovamente = { viewModel.carregar() })

        is IndicadorUiState.Sucesso ->
            ConteudoIndicador(e, onAbrirAlerta, modifier)
    }
}

@Composable
private fun ConteudoIndicador(
    estado: IndicadorUiState.Sucesso,
    onAbrirAlerta: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val resumo = estado.resumo
    val indicador = resumo.indicador
    val ultimos = resumo.historico.takeLast(30)
    val sintetico = resumo.historico.lastOrNull()?.origem == OrigemDado.SINTETICA

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Espaco.grande),
        verticalArrangement = Arrangement.spacedBy(Espaco.medio)
    ) {
        item {
            Text(estado.territorioNome, style = MaterialTheme.typography.bodyMedium)
            Text(indicador.nome, style = MaterialTheme.typography.headlineSmall)
        }

        if (estado.desatualizado) {
            item { AvisoDados("Dados desatualizados: sem previsão até chegarem dados novos.") }
        }

        item {
            KpiCard(
                titulo = "Valor atual",
                valor = resumo.valorAtual,
                unidade = indicador.unidade,
                variacao = resumo.variacaoSemana,
                status = resumo.status,
                onClick = {}
            )
        }

        item {
            Text("Tendência", style = MaterialTheme.typography.titleLarge)
            val descricao = "Gráfico de ${indicador.nome}. Valor atual " +
                    "${formatarNumero(resumo.valorAtual ?: 0.0)} ${indicador.unidade}. " +
                    if (estado.previsto != null) "Previsão ${formatarNumero(estado.previsto)} ${indicador.unidade}." else "Sem previsão."
            TrendChart(
                valores = ultimos.map { it.valor },
                previsto = estado.previsto,
                diasPrevisao = DIAS_PREVISAO,
                limiarAtencao = indicador.limiarAtencao,
                limiarCritico = indicador.limiarCritico,
                unidade = indicador.unidade,
                descricao = descricao
            )
        }

        item {
            Text("Alertas deste indicador", style = MaterialTheme.typography.titleLarge)
        }
        if (estado.alertas.isEmpty()) {
            item { Text("Nenhum alerta para este indicador.", style = MaterialTheme.typography.bodyMedium) }
        }
        items(estado.alertas) { alerta ->
            AlertCard(
                nivel = alerta.nivel,
                titulo = alerta.titulo,
                confianca = alerta.confianca,
                periodo = alerta.periodoPrevisto,
                onClick = { onAbrirAlerta(alerta.territorioId, alerta.id) }
            )
        }

        item {
            Text("Últimas leituras", style = MaterialTheme.typography.titleLarge)
        }
        items(resumo.historico.takeLast(7).reversed()) { leitura ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatarData(leitura.data), style = MaterialTheme.typography.bodyLarge)
                Text("${formatarNumero(leitura.valor)} ${indicador.unidade}", style = MaterialTheme.typography.bodyLarge)
            }
            HorizontalDivider()
        }

        item {
            Text("Fonte dos dados", style = MaterialTheme.typography.titleLarge)
            val fonte = if (sintetico) {
                "Dados sintéticos gerados pelo script gerar_dados.py (valores fictícios)."
            } else {
                "Dados públicos."
            }
            Text(fonte, style = MaterialTheme.typography.bodyMedium)
        }
    }
}