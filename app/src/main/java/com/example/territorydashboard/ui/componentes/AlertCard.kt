package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.territorydashboard.domain.modelo.NivelAlerta
import com.example.territorydashboard.ui.theme.Espaco

@Composable
fun AlertCard(
    nivel: NivelAlerta,
    titulo: String,
    confianca: Int,
    periodo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, corDoNivel(nivel))
    ) {
        Column(Modifier.padding(Espaco.grande)) {
            EtiquetaNivelAlerta(nivel)
            Spacer(Modifier.height(Espaco.medio))
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                "Confiança $confianca% | $periodo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Espaco.medio))
            Text(
                "Ver justificativa",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}