package com.example.territorydashboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun inserirTerritorios(lista: List<TerritorioEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun inserirIndicadores(lista: List<IndicadorEntity>)

    @Insert
    abstract suspend fun inserirLeituras(lista: List<LeituraEntity>)

    @Query("DELETE FROM leituras")
    abstract suspend fun apagarLeituras()

    @Query("SELECT COUNT(*) FROM leituras")
    abstract suspend fun contarLeituras(): Int

    @Query("SELECT * FROM territorios ORDER BY nome")
    abstract suspend fun listarTerritorios(): List<TerritorioEntity>

    @Query("SELECT * FROM indicadores")
    abstract suspend fun listarIndicadores(): List<IndicadorEntity>

    @Query("SELECT * FROM leituras WHERE territorioId = :territorioId ORDER BY data")
    abstract suspend fun leiturasDoTerritorio(territorioId: String): List<LeituraEntity>

    @Insert
    abstract suspend fun inserirDecisao(decisao: DecisaoEntity)

    @Query("SELECT * FROM decisoes ORDER BY dataHora DESC")
    abstract fun observarDecisoes(): Flow<List<DecisaoEntity>>

    @Query("DELETE FROM decisoes")
    abstract suspend fun apagarDecisoes()

    // tudo ou nada: se der erro no meio, o banco volta como estava
    @Transaction
    open suspend fun substituirDados(
        territorios: List<TerritorioEntity>,
        indicadores: List<IndicadorEntity>,
        leituras: List<LeituraEntity>
    ) {
        apagarLeituras()
        inserirTerritorios(territorios)
        inserirIndicadores(indicadores)
        inserirLeituras(leituras)
    }
}