package com.example.territorydashboard.data.local

import android.content.Context
import com.example.territorydashboard.domain.repositorio.PreferenciasRepositorio

// SharedPreferences: chave-valor simples, suficiente para poucas configuracoes
class PreferenciasLocal(context: Context) : PreferenciasRepositorio {

    private val prefs = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE)

    override fun territorioFavorito(): String? {
        return prefs.getString("territorio_favorito", null)
    }

    override fun salvarTerritorioFavorito(id: String) {
        prefs.edit().putString("territorio_favorito", id).apply()
    }

    override fun jaNotificado(alertaId: String): Boolean {
        val ids = prefs.getStringSet("alertas_notificados", emptySet()) ?: emptySet()
        return ids.contains(alertaId)
    }

    override fun marcarNotificado(alertaId: String) {
        val ids = prefs.getStringSet("alertas_notificados", emptySet()) ?: emptySet()
        // precisa criar um conjunto novo, o SharedPreferences nao aceita alterar o antigo
        val novos = HashSet(ids)
        novos.add(alertaId)
        prefs.edit().putStringSet("alertas_notificados", novos).apply()
    }
}