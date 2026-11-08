package com.example.territorydashboard.data.repositorio

import com.example.territorydashboard.data.csv.LeitorCsv
import com.example.territorydashboard.data.local.AppDao
import com.example.territorydashboard.data.local.DecisaoEntity
import com.example.territorydashboard.data.local.IndicadorEntity
import com.example.territorydashboard.data.local.LeituraEntity
import com.example.territorydashboard.data.local.TerritorioEntity
import com.example.territorydashboard.domain.modelo.AcaoDecisao
import com.example.territorydashboard.domain.modelo.Decisao
import com.example.territorydashboard.domain.modelo.Indicador
import com.example.territorydashboard.domain.modelo.Leitura
import com.example.territorydashboard.domain.modelo.OrigemDado
import com.example.territorydashboard.domain.modelo.Tema
import com.example.territorydashboard.domain.modelo.Territorio
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class TerritorioRepositorioImpl(
    private val dao: AppDao,
    private val leitorCsv: LeitorCsv
) : TerritorioRepositorio {

    override suspend fun atualizarDados(): Boolean {
        return try {
            // leitura de arquivo nao pode rodar na thread principal
            withContext(Dispatchers.IO) {
                val territorios = leitorCsv.lerTerritorios()
                val indicadores = leitorCsv.lerIndicadores()
                val leituras = leitorCsv.lerLeituras()
                dao.substituirDados(territorios, indicadores, leituras)
            }
            true
        } catch (e: Exception) {
            // se o arquivo falhar, o app continua com o que ja estava no banco
            false
        }
    }

    override suspend fun listarTerritorios(): List<Territorio> {
        return dao.listarTerritorios().map { paraModelo(it) }
    }

    override suspend fun listarIndicadores(): List<Indicador> {
        return dao.listarIndicadores().map { paraModelo(it) }
    }

    override suspend fun leiturasDoTerritorio(territorioId: String): List<Leitura> {
        return dao.leiturasDoTerritorio(territorioId).map { paraModelo(it) }
    }

    override suspend fun salvarDecisao(decisao: Decisao) {
        val entity = DecisaoEntity(
            alertaId = decisao.alertaId,
            tituloAlerta = decisao.tituloAlerta,
            responsavel = decisao.responsavel,
            acao = decisao.acao.name,
            observacao = decisao.observacao,
            dataHora = decisao.dataHora.toString()
        )
        dao.inserirDecisao(entity)
    }

    override fun observarDecisoes(): Flow<List<Decisao>> {
        return dao.observarDecisoes().map { lista ->
            lista.map { paraModelo(it) }
        }
    }

    override suspend fun apagarDecisoes() {
        dao.apagarDecisoes()
    }

    // conversoes entre o formato do banco e o modelo do dominio

    private fun paraModelo(e: TerritorioEntity): Territorio {
        return Territorio(e.id, e.nome, e.tipo, e.latitude, e.longitude)
    }

    private fun paraModelo(e: IndicadorEntity): Indicador {
        return Indicador(e.id, Tema.valueOf(e.tema), e.nome, e.unidade, e.limiarAtencao, e.limiarCritico)
    }

    private fun paraModelo(e: LeituraEntity): Leitura {
        return Leitura(e.indicadorId, e.territorioId, LocalDate.parse(e.data), e.valor, OrigemDado.valueOf(e.origem))
    }

    private fun paraModelo(e: DecisaoEntity): Decisao {
        return Decisao(
            id = e.id,
            alertaId = e.alertaId,
            tituloAlerta = e.tituloAlerta,
            responsavel = e.responsavel,
            acao = AcaoDecisao.valueOf(e.acao),
            observacao = e.observacao,
            dataHora = LocalDateTime.parse(e.dataHora)
        )
    }
}