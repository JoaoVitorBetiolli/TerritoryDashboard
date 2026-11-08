package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.territorydashboard.domain.casodeuso.StatusIndicador
import com.example.territorydashboard.ui.theme.Espaco

// mostra um indicador. nao calcula nada: recebe tudo pronto por parametro
@Composable
fun KpiCard(
    titulo: String,
    valor: Double?,
    unidade: String,
    variacao: Double?,
    status: StatusIndicador,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(Espaco.grande)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaStatusIndicador(status)
            }

            Spacer(Modifier.height(Espaco.medio))

            val textoValor = if (valor == null) "--" else "${formatarNumero(valor)} $unidade"
            Text(textoValor, style = MaterialTheme.typography.headlineSmall)

            if (variacao != null) {
                val sinal = if (variacao > 0) "+" else ""
                Text(
                    "$sinal${formatarNumero(variacao)} $unidade em 7 dias",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}