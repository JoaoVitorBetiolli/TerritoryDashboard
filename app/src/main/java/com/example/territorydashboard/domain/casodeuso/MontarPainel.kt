package com.example.territorydashboard.domain.casodeuso

import com.example.territorydashboard.domain.modelo.Alerta
import com.example.territorydashboard.domain.modelo.Indicador
import com.example.territorydashboard.domain.modelo.Leitura
import com.example.territorydashboard.domain.modelo.Territorio
import com.example.territorydashboard.domain.previsao.MotorPrevisao
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio
import java.time.LocalDate

enum class StatusIndicador { NORMAL, ATENCAO, CRITICO, SEM_DADOS }

data class ResumoIndicador(
    val indicador: Indicador,
    val valorAtual: Double?,
    val variacaoSemana: Double?,
    val status: StatusIndicador,
    val historico: List<Leitura>
)

data class Painel(
    val territorio: Territorio,
    val indicadores: List<ResumoIndicador>,
    val alertas: List<Alerta>,
    val ultimaAtualizacao: LocalDate?,
    val desatualizado: Boolean
)

class MontarPainel(
    private val repositorio: TerritorioRepositorio,
    private val motor: MotorPrevisao
) {

    suspend fun executar(territorioId: String, hoje: LocalDate): Painel? {
        val territorio = repositorio.listarTerritorios().find { it.id == territorioId }
        if (territorio == null) return null

        val indicadores = repositorio.listarIndicadores()
        val leituras = repositorio.leiturasDoTerritorio(territorioId)

        val resumos = mutableListOf<ResumoIndicador>()
        val alertas = mutableListOf<Alerta>()

        for (indicador in indicadores) {
            val doIndicador = leituras
                .filter { it.indicadorId == indicador.id }
                .sortedBy { it.data }

            val atual = doIndicador.lastOrNull()?.valor

            // compara com a leitura de uns 7 registros atras
            val anterior = doIndicador.dropLast(7).lastOrNull()?.valor
            var variacao: Double? = null
            if (atual != null && anterior != null) {
                variacao = atual - anterior
            }

            resumos.add(
                ResumoIndicador(indicador, atual, variacao, calcularStatus(indicador, atual), doIndicador)
            )

            val alerta = motor.avaliar(indicador, territorio, doIndicador, hoje)
            if (alerta != null) {
                alertas.add(alerta)
            }
        }

        // alertas mais graves primeiro
        alertas.sortByDescending { it.nivel.ordinal }

        return Painel(
            territorio = territorio,
            indicadores = resumos,
            alertas = alertas,
            ultimaAtualizacao = leituras.maxOfOrNull { it.data },
            desatualizado = motor.dadosDesatualizados(leituras, hoje)
        )
    }

    private fun calcularStatus(indicador: Indicador, valor: Double?): StatusIndicador {
        if (valor == null) return StatusIndicador.SEM_DADOS
        if (valor >= indicador.limiarCritico) return StatusIndicador.CRITICO
        if (valor >= indicador.limiarAtencao) return StatusIndicador.ATENCAO
        return StatusIndicador.NORMAL
    }
}