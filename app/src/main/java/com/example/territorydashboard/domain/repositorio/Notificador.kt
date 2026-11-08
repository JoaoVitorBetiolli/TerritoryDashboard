package com.example.territorydashboard.domain.repositorio

import com.example.territorydashboard.domain.modelo.Alerta

// contrato do recurso nativo. o dominio nao sabe que existe Android por tras
interface Notificador {

    fun podeNotificar(): Boolean

    fun mostrar(alerta: Alerta)
}