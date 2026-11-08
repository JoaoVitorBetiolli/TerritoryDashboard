package com.example.territorydashboard.data.local

import android.content.Context
import com.example.territorydashboard.domain.repositorio.PreferenciasRepositorio

// SharedPreferences: chave-valor simples, suficiente para 1 ou 2 configuracoes
class PreferenciasLocal(context: Context) : PreferenciasRepositorio {

    private val prefs = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE)

    override fun territorioFavorito(): String? {
        return prefs.getString("territorio_favorito", null)
    }

    override fun salvarTerritorioFavorito(id: String) {
        prefs.edit().putString("territorio_favorito", id).apply()
    }
}