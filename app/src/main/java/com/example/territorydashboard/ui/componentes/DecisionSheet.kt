package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.territorydashboard.domain.modelo.AcaoDecisao
import com.example.territorydashboard.ui.theme.Espaco

fun nomeDaAcao(acao: AcaoDecisao): String {
    return when (acao) {
        AcaoDecisao.ACATAR -> "Acatar"
        AcaoDecisao.ADIAR -> "Adiar"
        AcaoDecisao.DESCARTAR -> "Descartar"
    }
}

// folha que sobe de baixo para o gestor registrar a decisao.
// a validacao fica no caso de uso; aqui so mostramos o erro que voltar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionSheet(
    tituloAlerta: String,
    recomendacao: String,
    erro: String?,
    onConfirmar: (acao: AcaoDecisao, responsavel: String, observacao: String) -> Unit,
    onFechar: () -> Unit
) {
    var acao by rememberSaveable { mutableStateOf<AcaoDecisao?>(null) }
    var responsavel by rememberSaveable { mutableStateOf("") }
    var observacao by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onFechar) {
        Column(
            modifier = Modifier
                .padding(horizontal = Espaco.grande)
                .padding(bottom = Espaco.extra)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Espaco.medio)
        ) {
            Text("Registrar decisão", style = MaterialTheme.typography.titleLarge)
            Text(tituloAlerta, style = MaterialTheme.typography.bodyMedium)
            Text("Recomendação do app: $recomendacao", style = MaterialTheme.typography.bodyMedium)

            Text("O que você decide?", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(Espaco.medio)) {
                AcaoDecisao.entries.forEach { opcao ->
                    FilterChip(
                        selected = acao == opcao,
                        onClick = { acao = opcao },
                        label = { Text(nomeDaAcao(opcao)) }
                    )
                }
            }

            OutlinedTextField(
                value = responsavel,
                onValueChange = { responsavel = it },
                label = { Text("Responsável") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = observacao,
                onValueChange = { observacao = it },
                label = { Text("Observação") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            if (erro != null) {
                Text(erro, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = {
                    val escolhida = acao
                    if (escolhida != null) {
                        onConfirmar(escolhida, responsavel, observacao)
                    }
                },
                enabled = acao != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar decisão")
            }
        }
    }
}