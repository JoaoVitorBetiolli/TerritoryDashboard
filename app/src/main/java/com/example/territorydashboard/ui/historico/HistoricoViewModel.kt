package com.example.territorydashboard.ui.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.territorydashboard.TerritoryApp
import com.example.territorydashboard.domain.modelo.Decisao
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoricoViewModel(repositorio: TerritorioRepositorio) : ViewModel() {

    // null = ainda carregando; lista vazia = nenhuma decisao
    val decisoes: StateFlow<List<Decisao>?> = repositorio.observarDecisoes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TerritoryApp
                HistoricoViewModel(app.repositorio)
            }
        }
    }
}