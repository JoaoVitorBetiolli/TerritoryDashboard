package com.example.territorydashboard.ui.indicador

import com.example.territorydashboard.domain.casodeuso.ResumoIndicador
import com.example.territorydashboard.domain.modelo.Alerta

sealed interface IndicadorUiState {

    object Carregando : IndicadorUiState

    data class Sucesso(
        val territorioNome: String,
        val resumo: ResumoIndicador,
        val alertas: List<Alerta>,
        val previsto: Double?,
        val desatualizado: Boolean
    ) : IndicadorUiState

    data class Erro(val mensagem: String) : IndicadorUiState
}