package com.example.territorydashboard

import android.app.Application
import com.example.territorydashboard.data.csv.LeitorCsv
import com.example.territorydashboard.data.local.AppDatabase
import com.example.territorydashboard.data.local.PreferenciasLocal
import com.example.territorydashboard.data.repositorio.TerritorioRepositorioImpl
import com.example.territorydashboard.domain.repositorio.Notificador
import com.example.territorydashboard.domain.repositorio.PreferenciasRepositorio
import com.example.territorydashboard.domain.repositorio.TerritorioRepositorio
import com.example.territorydashboard.nativo.NotificadorAndroid

class TerritoryApp : Application() {

    // criado uma vez so, na primeira vez que alguem usar
    val repositorio: TerritorioRepositorio by lazy {
        val banco = AppDatabase.getInstance(this)
        TerritorioRepositorioImpl(banco.dao(), LeitorCsv(this))
    }

    val preferencias: PreferenciasRepositorio by lazy {
        PreferenciasLocal(this)
    }

    val notificador: Notificador by lazy {
        NotificadorAndroid(this)
    }
}