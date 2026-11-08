package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.territorydashboard.domain.modelo.Territorio
import com.example.territorydashboard.ui.theme.Espaco

@Composable
fun TerritorySelector(
    territorios: List<Territorio>,
    selecionadoId: String?,
    onSelecionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Espaco.medio)
    ) {
        territorios.forEach { t ->
            FilterChip(
                selected = t.id == selecionadoId,
                onClick = { onSelecionar(t.id) },
                label = { Text(t.nome) }
            )
        }
    }
}