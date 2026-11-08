package com.example.territorydashboard.domain.modelo

import java.time.LocalDate
import java.time.LocalDateTime

enum class Tema { ENERGIA, CLIMA }

enum class OrigemDado { PUBLICA, SINTETICA, AUTORIZADA }

enum class NivelAlerta { INFORMATIVO, ATENCAO, CRITICO }

enum class AcaoDecisao { ACATAR, ADIAR, DESCARTAR }

data class Territorio(
    val id: String,
    val nome: String,
    val tipo: String,
    val latitude: Double,
    val longitude: Double
)

data class Indicador(
    val id: String,
    val tema: Tema,
    val nome: String,
    val unidade: String,
    val limiarAtencao: Double,
    val limiarCritico: Double
)

data class Leitura(
    val indicadorId: String,
    val territorioId: String,
    val data: LocalDate,
    val valor: Double,
    val origem: OrigemDado
)

data class Alerta(
    val id: String,
    val territorioId: String,
    val indicadorId: String,
    val nivel: NivelAlerta,
    val titulo: String,
    val justificativa: String,
    val confianca: Int, // em porcentagem
    val periodoPrevisto: String,
    val fonte: String,
    val atualizadoEm: LocalDate,
    val validoAte: LocalDate
)

data class Decisao(
    val id: Long = 0,
    val alertaId: String,
    val tituloAlerta: String,
    val responsavel: String,
    val acao: AcaoDecisao,
    val observacao: String,
    val dataHora: LocalDateTime
)