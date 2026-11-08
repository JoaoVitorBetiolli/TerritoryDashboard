package com.example.territorydashboard.ui.painel

import com.example.territorydashboard.domain.casodeuso.Painel
import com.example.territorydashboard.domain.modelo.Territorio

sealed interface PainelUiState {

    object Carregando : PainelUiState

    data class Sucesso(
        val painel: Painel,
        val territorios: List<Territorio>,
        val usandoDadosSalvos: Boolean // true quando a carga dos arquivos falhou
    ) : PainelUiState

    data class Vazio(val mensagem: String) : PainelUiState

    data class Erro(val mensagem: String) : PainelUiState
}