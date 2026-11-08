package com.example.territorydashboard.domain.repositorio

import com.example.territorydashboard.domain.modelo.Decisao
import com.example.territorydashboard.domain.modelo.Indicador
import com.example.territorydashboard.domain.modelo.Leitura
import com.example.territorydashboard.domain.modelo.Territorio
import kotlinx.coroutines.flow.Flow

interface TerritorioRepositorio {

    // retorna false se nao conseguiu ler os arquivos (app segue com os dados antigos)
    suspend fun atualizarDados(): Boolean

    suspend fun listarTerritorios(): List<Territorio>

    suspend fun listarIndicadores(): List<Indicador>

    suspend fun leiturasDoTerritorio(territorioId: String): List<Leitura>

    suspend fun salvarDecisao(decisao: Decisao)

    fun observarDecisoes(): Flow<List<Decisao>>

    suspend fun apagarDecisoes()
}