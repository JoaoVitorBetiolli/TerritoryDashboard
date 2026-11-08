package com.example.territorydashboard.ui.configuracoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.territorydashboard.TerritoryApp
import com.example.territorydashboard.domain.modelo.Indicador
import com.example.territorydashboard.domain.modelo.Territorio
import com.example.territorydashboard.domain.repositorio.PreferenciasRepositorio
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConfiguracoesEstado(
    val carregando: Boolean = true,
    val territorios: List<Territorio> = emptyList(),
    val favoritoId: String? = null,
    val indicadores: List<Indicador> = emptyList(),
    val mensagem: String? = null
)

class ConfiguracoesViewModel(
    private val repositorio: TerritorioRepositorio,
    private val preferencias: PreferenciasRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(ConfiguracoesEstado())
    val estado: StateFlow<ConfiguracoesEstado> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val territorios = repositorio.listarTerritorios()
            _estado.value = ConfiguracoesEstado(
                carregando = false,
                territorios = territorios,
                favoritoId = preferencias.territorioFavorito() ?: territorios.firstOrNull()?.id,
                indicadores = repositorio.listarIndicadores()
            )
        }
    }

    fun escolherFavorito(id: String) {
        preferencias.salvarTerritorioFavorito(id)
        _estado.value = _estado.value.copy(favoritoId = id)
    }

    // LGPD: o usuario pode apagar os registros que ele mesmo criou
    fun apagarDecisoes() {
        viewModelScope.launch {
            try {
                repositorio.apagarDecisoes()
                _estado.value = _estado.value.copy(mensagem = "Histórico de decisões apagado.")
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(mensagem = "Não foi possível apagar. Tente novamente.")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TerritoryApp
                ConfiguracoesViewModel(app.repositorio, app.preferencias)
            }
        }
    }
}