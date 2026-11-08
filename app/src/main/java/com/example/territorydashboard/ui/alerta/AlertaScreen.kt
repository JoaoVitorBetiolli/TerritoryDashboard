package com.example.territorydashboard.ui.alerta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.territorydashboard.domain.modelo.AcaoDecisao
import com.example.territorydashboard.domain.modelo.Decisao
import com.example.territorydashboard.domain.previsao.DIAS_PREVISAO
import com.example.territorydashboard.ui.componentes.DecisionSheet
import com.example.territorydashboard.ui.componentes.EtiquetaNivelAlerta
import com.example.territorydashboard.ui.componentes.JustificationPanel
import com.example.territorydashboard.ui.componentes.StateView
import com.example.territorydashboard.ui.componentes.TipoEstado
import com.example.territorydashboard.ui.componentes.TrendChart
import com.example.territorydashboard.ui.componentes.formatarNumero
import com.example.territorydashboard.ui.componentes.nomeDaAcao
import com.example.territorydashboard.ui.theme.Espaco
import java.time.format.DateTimeFormatter

@Composable
fun AlertaScreen(
    modifier: Modifier = Modifier,
    viewModel: AlertaViewModel = viewModel(factory = AlertaViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val decisoes by viewModel.decisoes.collectAsStateWithLifecycle()
    val folhaAberta by viewModel.folhaAberta.collectAsStateWithLifecycle()
    val erroDecisao by viewModel.erroDecisao.collectAsStateWithLifecycle()

    when (val e = estado) {
        is AlertaUiState.Carregando ->
            StateView(TipoEstado.CARREGANDO, "Carregando alerta...", modifier)

        is AlertaUiState.Erro ->
            StateView(TipoEstado.ERRO, e.mensagem, modifier, onTentarNovamente = { viewModel.carregar() })

        is AlertaUiState.Sucesso -> {
            ConteudoAlerta(
                estado = e,
                decisoes = decisoes,
                onRegistrar = { viewModel.abrirFolha() },
                modifier = modifier
            )

            if (folhaAberta) {
                DecisionSheet(
                    tituloAlerta = e.alerta.titulo,
                    recomendacao = e.recomendacao,
                    erro = erroDecisao,
                    onConfirmar = { acao: AcaoDecisao, responsavel: String, observacao: String ->
                        viewModel.registrar(acao, responsavel, observacao)
                    },
                    onFechar = { viewModel.fecharFolha() }
                )
            }
        }
    }
}

@Composable
private fun ConteudoAlerta(
    estado: AlertaUiState.Sucesso,
    decisoes: List<Decisao>,
    onRegistrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alerta = estado.alerta
    val formatoDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Espaco.grande),
        verticalArrangement = Arrangement.spacedBy(Espaco.medio)
    ) {
        item {
            EtiquetaNivelAlerta(alerta.nivel)
            Text(alerta.titulo, style = MaterialTheme.typography.headlineSmall)
            Text(estado.territorioNome, style = MaterialTheme.typography.bodyMedium)
        }

        item {
            JustificationPanel(
                justificativa = alerta.justificativa,
                confianca = alerta.confianca,
                periodo = alerta.periodoPrevisto,
                fonte = alerta.fonte,
                atualizadoEm = alerta.atualizadoEm,
                validoAte = alerta.validoAte
            )
        }

        val resumo = estado.resumo
        if (resumo != null) {
            item {
                Text("Tendência", style = MaterialTheme.typography.titleLarge)
                TrendChart(
                    valores = resumo.historico.takeLast(30).map { it.valor },
                    previsto = estado.previsto,
                    diasPrevisao = DIAS_PREVISAO,
                    limiarAtencao = resumo.indicador.limiarAtencao,
                    limiarCritico = resumo.indicador.limiarCritico,
                    unidade = resumo.indicador.unidade,
                    descricao = "Gráfico de ${resumo.indicador.nome}. Previsão " +
                            "${formatarNumero(estado.previsto ?: 0.0)} ${resumo.indicador.unidade}."
                )
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Espaco.grande)) {
                    Text("Recomendação do app", style = MaterialTheme.typography.titleMedium)
                    Text(estado.recomendacao, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "O app apenas recomenda. A decisão é sempre de uma pessoa responsável.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Button(onClick = onRegistrar, modifier = Modifier.fillMaxWidth()) {
                Text("Registrar decisão")
            }
        }

        item {
            Text("Decisões sobre este alerta", style = MaterialTheme.typography.titleLarge)
        }
        if (decisoes.isEmpty()) {
            item { Text("Nenhuma decisão registrada ainda.", style = MaterialTheme.typography.bodyMedium) }
        }
        items(decisoes) { d ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Espaco.grande)) {
                    Text(nomeDaAcao(d.acao), style = MaterialTheme.typography.titleMedium)
                    Text("${d.responsavel} | ${d.dataHora.format(formatoDataHora)}", style = MaterialTheme.typography.bodyMedium)
                    if (d.observacao.isNotBlank()) {
                        Text(d.observacao, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}