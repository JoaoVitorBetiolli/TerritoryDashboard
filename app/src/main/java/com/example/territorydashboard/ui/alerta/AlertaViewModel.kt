package com.example.territorydashboard.ui.alerta

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.territorydashboard.TerritoryApp
import com.example.territorydashboard.domain.casodeuso.MontarPainel
import com.example.territorydashboard.domain.casodeuso.RegistrarDecisao
import com.example.territorydashboard.domain.casodeuso.recomendarAcao
import com.example.territorydashboard.domain.modelo.AcaoDecisao
import com.example.territorydashboard.domain.modelo.Decisao
import com.example.territorydashboard.domain.previsao.MotorPrevisao
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class AlertaViewModel(
    repositorio: TerritorioRepositorio,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val territorioId: String = savedStateHandle["territorioId"] ?: ""
    private val alertaId: String = savedStateHandle["alertaId"] ?: ""

    private val motor = MotorPrevisao()
    private val montarPainel = MontarPainel(repositorio, motor)
    private val registrarDecisao = RegistrarDecisao(repositorio)

    private val _uiState = MutableStateFlow<AlertaUiState>(AlertaUiState.Carregando)
    val uiState: StateFlow<AlertaUiState> = _uiState.asStateFlow()

    // decisoes ja tomadas sobre este alerta (atualiza sozinho quando salva uma nova)
    val decisoes: StateFlow<List<Decisao>> = repositorio.observarDecisoes()
        .map { lista -> lista.filter { it.alertaId == alertaId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // a folha de decisao fica no viewmodel para nao fechar quando gira a tela
    private val _folhaAberta = MutableStateFlow(false)
    val folhaAberta: StateFlow<Boolean> = _folhaAberta.asStateFlow()

    private val _erroDecisao = MutableStateFlow<String?>(null)
    val erroDecisao: StateFlow<String?> = _erroDecisao.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _uiState.value = AlertaUiState.Carregando
            try {
                val hoje = LocalDate.now()
                val painel = montarPainel.executar(territorioId, hoje)
                val alerta = painel?.alertas?.find { it.id == alertaId }

                if (painel == null || alerta == null) {
                    _uiState.value = AlertaUiState.Erro("Este alerta não está mais ativo.")
                } else {
                    val resumo = painel.indicadores.find { it.indicador.id == alerta.indicadorId }
                    _uiState.value = AlertaUiState.Sucesso(
                        alerta = alerta,
                        territorioNome = painel.territorio.nome,
                        resumo = resumo,
                        previsto = if (resumo != null) motor.projetar(resumo.historico, hoje) else null,
                        recomendacao = recomendarAcao(alerta.nivel)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AlertaUiState.Erro("Não foi possível ler os dados salvos no aparelho.")
            }
        }
    }

    fun abrirFolha() {
        _erroDecisao.value = null
        _folhaAberta.value = true
    }

    fun fecharFolha() {
        _folhaAberta.value = false
    }

    fun registrar(acao: AcaoDecisao, responsavel: String, observacao: String) {
        val estado = _uiState.value
        if (estado !is AlertaUiState.Sucesso) return

        viewModelScope.launch {
            try {
                val erro = registrarDecisao.executar(estado.alerta, acao, responsavel, observacao, LocalDateTime.now())
                _erroDecisao.value = erro
                if (erro == null) {
                    _folhaAberta.value = false
                }
            } catch (e: Exception) {
                _erroDecisao.value = "Não foi possível salvar a decisão. Tente novamente."
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TerritoryApp
                AlertaViewModel(app.repositorio, createSavedStateHandle())
            }
        }
    }
}