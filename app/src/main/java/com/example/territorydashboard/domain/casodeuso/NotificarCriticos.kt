package com.example.territorydashboard.domain.casodeuso

import com.example.territorydashboard.domain.modelo.Alerta
import com.example.territorydashboard.domain.modelo.NivelAlerta
import com.example.territorydashboard.domain.repositorio.Notificador
import com.example.territorydashboard.domain.repositorio.PreferenciasRepositorio

// regra de quando notificar: so nivel critico e nunca o mesmo alerta duas vezes
class NotificarCriticos(
    private val notificador: Notificador,
    private val preferencias: PreferenciasRepositorio
) {

    // retorna quantas notificacoes foram mostradas
    fun executar(alertas: List<Alerta>): Int {
        if (!notificador.podeNotificar()) return 0

        var enviadas = 0
        for (alerta in alertas) {
            if (alerta.nivel != NivelAlerta.CRITICO) continue
            if (preferencias.jaNotificado(alerta.id)) continue

            notificador.mostrar(alerta)
            preferencias.marcarNotificado(alerta.id)
            enviadas++
        }
        return enviadas
    }
}