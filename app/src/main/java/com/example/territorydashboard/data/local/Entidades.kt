package com.example.territorydashboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "territorios")
data class TerritorioEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val tipo: String,
    val latitude: Double,
    val longitude: Double
)

@Entity(tableName = "indicadores")
data class IndicadorEntity(
    @PrimaryKey val id: String,
    val tema: String,
    val nome: String,
    val unidade: String,
    val limiarAtencao: Double,
    val limiarCritico: Double
)

@Entity(tableName = "leituras")
data class LeituraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val indicadorId: String,
    val territorioId: String,
    val data: String, // formato 2026-11-08
    val valor: Double,
    val origem: String
)

@Entity(tableName = "decisoes")
data class DecisaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alertaId: String,
    val tituloAlerta: String,
    val responsavel: String,
    val acao: String,
    val observacao: String,
    val dataHora: String
)