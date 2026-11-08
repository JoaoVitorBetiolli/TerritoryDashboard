package com.example.territorydashboard.ui.indicador

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.territorydashboard.TerritoryApp
import com.example.territorydashboard.domain.casodeuso.MontarPainel
import com.example.territorydashboard.domain.previsao.MotorPrevisao
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class IndicadorViewModel(
    repositorio: TerritorioRepositorio,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // os ids chegam pela rota da navegacao
    private val territorioId: String = savedStateHandle["territorioId"] ?: ""
    private val indicadorId: String = savedStateHandle["indicadorId"] ?: ""

    private val motor = MotorPrevisao()
    private val montarPainel = MontarPainel(repositorio, motor)

    private val _uiState = MutableStateFlow<IndicadorUiState>(IndicadorUiState.Carregando)
    val uiState: StateFlow<IndicadorUiState> = _uiState.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _uiState.value = IndicadorUiState.Carregando
            try {
                val hoje = LocalDate.now()
                val painel = montarPainel.executar(territorioId, hoje)
                val resumo = painel?.indicadores?.find { it.indicador.id == indicadorId }

                if (painel == null || resumo == null) {
                    _uiState.value = IndicadorUiState.Erro("Indicador não encontrado.")
                } else {
                    _uiState.value = IndicadorUiState.Sucesso(
                        territorioNome = painel.territorio.nome,
                        resumo = resumo,
                        alertas = painel.alertas.filter { it.indicadorId == indicadorId },
                        previsto = motor.projetar(resumo.historico, hoje),
                        desatualizado = painel.desatualizado
                    )
                }
            } catch (e: Exception) {
                _uiState.value = IndicadorUiState.Erro("Não foi possível ler os dados salvos no aparelho.")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TerritoryApp
                IndicadorViewModel(app.repositorio, createSavedStateHandle())
            }
        }
    }
}