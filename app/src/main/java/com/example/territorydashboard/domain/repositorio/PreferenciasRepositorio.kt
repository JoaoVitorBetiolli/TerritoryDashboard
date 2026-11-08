package com.example.territorydashboard.domain.repositorio

// preferencias simples do usuario, guardadas no aparelho
interface PreferenciasRepositorio {

    fun territorioFavorito(): String?

    fun salvarTerritorioFavorito(id: String)
}