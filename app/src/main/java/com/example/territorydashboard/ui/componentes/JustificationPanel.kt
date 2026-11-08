package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.territorydashboard.ui.theme.Espaco
import java.time.LocalDate

// mostra por que o alerta foi emitido: dados, regra, periodo, confianca e fonte
@Composable
fun JustificationPanel(
    justificativa: String,
    confianca: Int,
    periodo: String,
    fonte: String,
    atualizadoEm: LocalDate,
    validoAte: LocalDate,
    modifier: Modifier = Modifier
) {
    val textoConfianca = when {
        confianca >= 80 -> "Alta"
        confianca >= 60 -> "Média"
        else -> "Baixa"
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(Espaco.grande)) {
            Text("Por que este alerta?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Espaco.medio))
            Text(justificativa, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(Espaco.grande))
            Text("Confiança: $textoConfianca ($confianca%)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Espaco.pequeno))
            LinearProgressIndicator(
                progress = { confianca / 100f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Espaco.grande))
            HorizontalDivider()
            LinhaInfo("Período previsto", periodo)
            LinhaInfo("Fonte", fonte)
            LinhaInfo("Atualizado em", formatarData(atualizadoEm))
            LinhaInfo("Válido até", formatarData(validoAte))
        }
    }
}

@Composable
private fun LinhaInfo(rotulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Espaco.medio),
        horizontalArrangement = Arrangement.spacedBy(Espaco.grande)
    ) {
        Text(rotulo, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(2f))
    }
}