package com.example.territorydashboard.ui.painel

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

class PainelViewModel(
    private val repositorio: TerritorioRepositorio,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val montarPainel = MontarPainel(repositorio, MotorPrevisao())

    private val _uiState = MutableStateFlow<PainelUiState>(PainelUiState.Carregando)
    val uiState: StateFlow<PainelUiState> = _uiState.asStateFlow()

    // a carga dos csv so acontece uma vez enquanto o viewmodel existir
    private var cargaFeita = false
    private var cargaFalhou = false

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _uiState.value = PainelUiState.Carregando
            try {
                if (!cargaFeita) {
                    cargaFalhou = !repositorio.atualizarDados()
                    cargaFeita = true
                }

                val territorios = repositorio.listarTerritorios()
                if (territorios.isEmpty()) {
                    _uiState.value = PainelUiState.Vazio("Nenhum território com dados no aparelho.")
                    return@launch
                }

                val id = savedStateHandle.get<String>(CHAVE_TERRITORIO) ?: territorios.first().id
                val painel = montarPainel.executar(id, LocalDate.now())

                if (painel == null) {
                    _uiState.value = PainelUiState.Vazio("Território não encontrado.")
                } else {
                    _uiState.value = PainelUiState.Sucesso(painel, territorios, cargaFalhou)
                }
            } catch (e: Exception) {
                _uiState.value = PainelUiState.Erro("Não foi possível ler os dados salvos no aparelho.")
            }
        }
    }

    fun selecionarTerritorio(id: String) {
        savedStateHandle[CHAVE_TERRITORIO] = id
        carregar()
    }

    companion object {
        private const val CHAVE_TERRITORIO = "territorioId"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TerritoryApp
                PainelViewModel(app.repositorio, createSavedStateHandle())
            }
        }
    }
}