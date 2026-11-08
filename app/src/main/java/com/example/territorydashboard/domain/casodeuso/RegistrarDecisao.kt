package com.example.territorydashboard.domain.casodeuso

import com.example.territorydashboard.domain.modelo.AcaoDecisao
import com.example.territorydashboard.domain.modelo.Alerta
import com.example.territorydashboard.domain.modelo.Decisao
import com.example.territorydashboard.domain.modelo.NivelAlerta
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio
import java.time.LocalDateTime

// o app so recomenda. quem decide e uma pessoa, e a decisao fica registrada
fun recomendarAcao(nivel: NivelAlerta): String {
    return when (nivel) {
        NivelAlerta.CRITICO ->
            "Acionar a equipe responsável e preparar um comunicado aos moradores. " +
                    "Medidas de alto impacto (como interrupção de serviço) só com confirmação do gestor."
        NivelAlerta.ATENCAO ->
            "Reforçar o monitoramento nos próximos dias e revisar o plano de contingência."
        NivelAlerta.INFORMATIVO ->
            "Apenas acompanhar a evolução do indicador."
    }
}

class RegistrarDecisao(private val repositorio: TerritorioRepositorio) {

    // retorna null se deu certo, ou a mensagem do que esta faltando
    suspend fun executar(
        alerta: Alerta,
        acao: AcaoDecisao,
        responsavel: String,
        observacao: String,
        agora: LocalDateTime
    ): String? {
        if (responsavel.isBlank()) {
            return "Informe o nome do responsável pela decisão."
        }
        if (acao == AcaoDecisao.DESCARTAR && observacao.isBlank()) {
            return "Para descartar um alerta, explique o motivo na observação."
        }

        val decisao = Decisao(
            alertaId = alerta.id,
            tituloAlerta = alerta.titulo,
            responsavel = responsavel.trim(),
            acao = acao,
            observacao = observacao.trim(),
            dataHora = agora
        )
        repositorio.salvarDecisao(decisao)
        return null
    }
}