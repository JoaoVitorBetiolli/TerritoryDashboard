package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.territorydashboard.ui.theme.Espaco
import com.example.territorydashboard.ui.theme.coresStatus

enum class TipoEstado { CARREGANDO, VAZIO, ERRO }

// padroniza as telas de carregando, vazio e erro
@Composable
fun StateView(
    tipo: TipoEstado,
    mensagem: String,
    modifier: Modifier = Modifier,
    onTentarNovamente: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize().padding(Espaco.extra),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (tipo) {
            TipoEstado.CARREGANDO -> CircularProgressIndicator()
            TipoEstado.VAZIO -> Icon(
                Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = coresStatus().semDados
            )
            TipoEstado.ERRO -> Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(Espaco.grande))
        Text(mensagem, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)

        if (onTentarNovamente != null) {
            Spacer(Modifier.height(Espaco.grande))
            Button(onClick = onTentarNovamente) {
                Text("Tentar novamente")
            }
        }
    }
}

// aviso que aparece no topo da tela quando os dados estao velhos ou nao atualizaram
@Composable
fun AvisoDados(mensagem: String, modifier: Modifier = Modifier) {
    val cor = coresStatus().atencao
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cor.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(Espaco.grande),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Warning, contentDescription = "Aviso", tint = cor)
            Spacer(Modifier.width(Espaco.medio))
            Text(mensagem, style = MaterialTheme.typography.bodyMedium)
        }
    }
}