package com.example.territorydashboard

import android.app.Application
import com.example.territorydashboard.data.csv.LeitorCsv
import com.example.territorydashboard.data.local.AppDatabase
import com.example.territorydashboard.data.repositorio.TerritorioRepositorioImpl
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio

class TerritoryApp : Application() {

    // criado uma vez so, na primeira vez que alguem usar
    val repositorio: TerritorioRepositorio by lazy {
        val banco = AppDatabase.getInstance(this)
        TerritorioRepositorioImpl(banco.dao(), LeitorCsv(this))
    }
}