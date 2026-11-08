package com.example.territorydashboard.domain.repositorio

// preferencias simples do usuario, guardadas no aparelho
interface PreferenciasRepositorio {

    fun territorioFavorito(): String?

    fun salvarTerritorioFavorito(id: String)

    // para nao notificar o mesmo alerta duas vezes
    fun jaNotificado(alertaId: String): Boolean

    fun marcarNotificado(alertaId: String)
}