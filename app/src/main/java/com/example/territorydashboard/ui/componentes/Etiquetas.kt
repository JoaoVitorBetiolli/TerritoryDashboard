package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.territorydashboard.domain.casodeuso.StatusIndicador
import com.example.territorydashboard.domain.modelo.NivelAlerta
import com.example.territorydashboard.ui.theme.Espaco
import com.example.territorydashboard.ui.theme.coresStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// etiqueta com icone + texto: a cor nunca aparece sozinha (acessibilidade)
@Composable
fun EtiquetaStatus(
    texto: String,
    cor: Color,
    icone: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(cor.copy(alpha = 0.12f))
            .padding(horizontal = Espaco.medio, vertical = Espaco.pequeno),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icone, contentDescription = null, tint = cor, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(Espaco.pequeno))
        Text(texto, color = cor, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun EtiquetaStatusIndicador(status: StatusIndicador, modifier: Modifier = Modifier) {
    val cores = coresStatus()
    when (status) {
        StatusIndicador.NORMAL -> EtiquetaStatus("Normal", cores.normal, Icons.Filled.CheckCircle, modifier)
        StatusIndicador.ATENCAO -> EtiquetaStatus("Atenção", cores.atencao, Icons.Filled.Warning, modifier)
        StatusIndicador.CRITICO -> EtiquetaStatus("Crítico", cores.critico, Icons.Filled.Warning, modifier)
        StatusIndicador.SEM_DADOS -> EtiquetaStatus("Sem dados", cores.semDados, Icons.Filled.Info, modifier)
    }
}

@Composable
fun EtiquetaNivelAlerta(nivel: NivelAlerta, modifier: Modifier = Modifier) {
    when (nivel) {
        NivelAlerta.INFORMATIVO -> EtiquetaStatus("Informativo", corDoNivel(nivel), Icons.Filled.Info, modifier)
        NivelAlerta.ATENCAO -> EtiquetaStatus("Atenção", corDoNivel(nivel), Icons.Filled.Warning, modifier)
        NivelAlerta.CRITICO -> EtiquetaStatus("Crítico", corDoNivel(nivel), Icons.Filled.Warning, modifier)
    }
}

@Composable
fun corDoNivel(nivel: NivelAlerta): Color {
    val cores = coresStatus()
    return when (nivel) {
        NivelAlerta.INFORMATIVO -> MaterialTheme.colorScheme.secondary
        NivelAlerta.ATENCAO -> cores.atencao
        NivelAlerta.CRITICO -> cores.critico
    }
}

fun formatarNumero(valor: Double): String {
    return String.format("%.1f", valor)
}

fun formatarData(data: LocalDate): String {
    return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}