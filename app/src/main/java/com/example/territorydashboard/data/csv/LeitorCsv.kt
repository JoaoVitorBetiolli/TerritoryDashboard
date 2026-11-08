package com.example.territorydashboard.data.csv

import android.content.Context
import com.example.territorydashboard.data.local.IndicadorEntity
import com.example.territorydashboard.data.local.LeituraEntity
import com.example.territorydashboard.data.local.TerritorioEntity

class LeitorCsv(private val context: Context) {

    // le o arquivo da pasta assets/dados e pula o cabecalho
    private fun lerLinhas(arquivo: String): List<List<String>> {
        val linhas = context.assets.open("dados/$arquivo")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readLines() }

        return linhas.drop(1)
            .filter { it.isNotBlank() }
            .map { it.split(",") }
    }

    fun lerTerritorios(): List<TerritorioEntity> {
        return lerLinhas("territorios.csv").map {
            TerritorioEntity(it[0], it[1], it[2], it[3].toDouble(), it[4].toDouble())
        }
    }

    fun lerIndicadores(): List<IndicadorEntity> {
        return lerLinhas("indicadores.csv").map {
            IndicadorEntity(it[0], it[1], it[2], it[3], it[4].toDouble(), it[5].toDouble())
        }
    }

    fun lerLeituras(): List<LeituraEntity> {
        return lerLinhas("leituras.csv").map {
            LeituraEntity(
                indicadorId = it[0],
                territorioId = it[1],
                data = it[2],
                valor = it[3].toDouble(),
                origem = it[4]
            )
        }
    }
}