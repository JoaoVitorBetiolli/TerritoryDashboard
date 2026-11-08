package com.example.territorydashboard.nativo

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.territorydashboard.ui.theme.Espaco
import com.example.territorydashboard.ui.theme.coresStatus

// os tres caminhos da permissao: concedida, negada e negada para sempre
enum class EstadoPermissao { CONCEDIDA, NAO_CONCEDIDA, NEGADA_PARA_SEMPRE }

// cartao que pede a permissao de notificacao no momento certo, explicando o motivo.
// se a pessoa negar, o app continua funcionando: os alertas aparecem no Painel (plano B)
@Composable
fun CartaoPermissaoNotificacao(
    mostrarQuandoConcedida: Boolean,
    onConcedida: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    var negadaParaSempre by rememberSaveable { mutableStateOf(false) }
    var concedida by remember { mutableStateOf(temPermissaoNotificacao(context)) }

    // confere de novo quando a pessoa volta das configuracoes do Android
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val agora = temPermissaoNotificacao(context)
        if (agora && !concedida) onConcedida()
        concedida = agora
    }

    val pedirPermissao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { aceitou ->
        concedida = aceitou
        if (aceitou) {
            onConcedida()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity != null) {
            // se o Android nao deixa mais perguntar, a pessoa marcou "nao perguntar de novo"
            negadaParaSempre = !activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val estado = when {
        concedida -> EstadoPermissao.CONCEDIDA
        negadaParaSempre -> EstadoPermissao.NEGADA_PARA_SEMPRE
        else -> EstadoPermissao.NAO_CONCEDIDA
    }

    if (estado == EstadoPermissao.CONCEDIDA && !mostrarQuandoConcedida) return

    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(Espaco.grande)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (estado == EstadoPermissao.CONCEDIDA) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = coresStatus().normal)
                } else {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = coresStatus().atencao)
                }
                Spacer(Modifier.width(Espaco.medio))
                Text("Notificações de alerta crítico", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(Espaco.medio))

            when (estado) {
                EstadoPermissao.CONCEDIDA -> {
                    Text(
                        "Ativadas. Você será avisado quando um alerta ficar crítico.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                EstadoPermissao.NAO_CONCEDIDA -> {
                    Text(
                        "Ative para ser avisado quando um alerta ficar crítico, mesmo com o app fechado. " +
                                "Sem isso, os alertas continuam aparecendo aqui no Painel.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(Espaco.medio))
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                pedirPermissao.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                abrirConfiguracoes(context)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ativar notificações")
                    }
                }

                EstadoPermissao.NEGADA_PARA_SEMPRE -> {
                    Text(
                        "As notificações estão bloqueadas. Os alertas críticos continuam aparecendo no Painel. " +
                                "Para receber avisos, libere nas configurações do Android.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(Espaco.medio))
                    OutlinedButton(
                        onClick = { abrirConfiguracoes(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Abrir configurações do Android")
                    }
                }
            }
        }
    }
}

private fun abrirConfiguracoes(context: android.content.Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}