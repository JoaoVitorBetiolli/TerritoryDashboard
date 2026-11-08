package com.example.territorydashboard.ui.alerta

import com.example.territorydashboard.domain.casodeuso.ResumoIndicador
import com.example.territorydashboard.domain.modelo.Alerta

sealed interface AlertaUiState {

    object Carregando : AlertaUiState

    data class Sucesso(
        val alerta: Alerta,
        val territorioNome: String,
        val resumo: ResumoIndicador?,
        val previsto: Double?,
        val recomendacao: String
    ) : AlertaUiState

    data class Erro(val mensagem: String) : AlertaUiState
}