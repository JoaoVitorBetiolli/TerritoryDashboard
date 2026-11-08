package com.example.territorydashboard.ui.historico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
import com.example.territorydashboard.ui.componentes.EtiquetaStatus
import com.example.territorydashboard.ui.componentes.StateView
import com.example.territorydashboard.ui.componentes.TipoEstado
import com.example.territorydashboard.ui.componentes.nomeDaAcao
import com.example.territorydashboard.ui.theme.Espaco
import com.example.territorydashboard.ui.theme.coresStatus
import java.time.format.DateTimeFormatter

@Composable
fun HistoricoScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoricoViewModel = viewModel(factory = HistoricoViewModel.Factory)
) {
    val decisoes by viewModel.decisoes.collectAsStateWithLifecycle()
    val lista = decisoes

    if (lista == null) {
        StateView(TipoEstado.CARREGANDO, "Carregando histórico...", modifier)
        return
    }

    if (lista.isEmpty()) {
        StateView(
            TipoEstado.VAZIO,
            "Nenhuma decisão registrada ainda. Abra um alerta no Painel para registrar a primeira.",
            modifier
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Espaco.grande),
        verticalArrangement = Arrangement.spacedBy(Espaco.medio)
    ) {
        item {
            val texto = if (lista.size == 1) "1 decisão registrada" else "${lista.size} decisões registradas"
            Text(texto, style = MaterialTheme.typography.titleLarge)
        }
        items(lista) { decisao ->
            CartaoDecisao(decisao)
        }
    }
}

@Composable
private fun CartaoDecisao(decisao: Decisao) {
    val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val cores = coresStatus()

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Espaco.grande)) {
            when (decisao.acao) {
                AcaoDecisao.ACATAR -> EtiquetaStatus(nomeDaAcao(decisao.acao), cores.normal, Icons.Filled.CheckCircle)
                AcaoDecisao.ADIAR -> EtiquetaStatus(nomeDaAcao(decisao.acao), cores.atencao, Icons.Filled.DateRange)
                AcaoDecisao.DESCARTAR -> EtiquetaStatus(nomeDaAcao(decisao.acao), cores.semDados, Icons.Filled.Close)
            }
            Spacer(Modifier.height(Espaco.medio))
            Text(decisao.tituloAlerta, style = MaterialTheme.typography.titleMedium)
            Text(
                "${decisao.responsavel} | ${decisao.dataHora.format(formato)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (decisao.observacao.isNotBlank()) {
                Spacer(Modifier.height(Espaco.pequeno))
                Text(decisao.observacao, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}