package com.example.territorydashboard.nativo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.territorydashboard.MainActivity
import com.example.territorydashboard.domain.modelo.Alerta
import com.example.territorydashboard.domain.repositorio.Notificador

const val CANAL_ALERTAS = "alertas_criticos"

// adaptador: toda a parte de notificacao do Android fica aqui dentro
class NotificadorAndroid(private val context: Context) : Notificador {

    init {
        criarCanal()
    }

    override fun podeNotificar(): Boolean {
        return temPermissaoNotificacao(context)
    }

    override fun mostrar(alerta: Alerta) {
        if (!podeNotificar()) return

        // tocar na notificacao abre o app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_ALERTAS)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(alerta.titulo)
            .setContentText("Confiança ${alerta.confianca}%. Toque para ver a justificativa.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(alerta.justificativa))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(alerta.id.hashCode(), notificacao)
        } catch (e: SecurityException) {
            // permissao foi tirada no meio do caminho: so nao mostra
        }
    }

    private fun criarCanal() {
        val canal = NotificationChannel(
            CANAL_ALERTAS,
            "Alertas críticos",
            NotificationManager.IMPORTANCE_HIGH
        )
        canal.description = "Avisos quando um indicador do território pode passar do limite crítico"
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }
}

fun temPermissaoNotificacao(context: Context): Boolean {
    // do Android 13 em diante precisa da permissao POST_NOTIFICATIONS
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val resultado = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        if (resultado != PackageManager.PERMISSION_GRANTED) return false
    }
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}