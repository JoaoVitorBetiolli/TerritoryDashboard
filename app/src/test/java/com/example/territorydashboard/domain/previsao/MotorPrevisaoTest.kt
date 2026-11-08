package com.example.territorydashboard.domain.previsao

import com.example.territorydashboard.domain.modelo.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MotorPrevisaoTest {

    private val motor = MotorPrevisao()
    private val hoje = LocalDate.of(2026, 11, 8)

    private val territorio = Territorio("t1", "Bairro Teste", "BAIRRO", 0.0, 0.0)
    private val indicador = Indicador("carga", Tema.ENERGIA, "Carga", "%", 80.0, 95.0)

    // cria uma leitura por dia terminando na data informada
    private fun criarLeituras(valores: List<Double>, terminaEm: LocalDate = hoje): List<Leitura> {
        val lista = mutableListOf<Leitura>()
        for (i in valores.indices) {
            val data = terminaEm.minusDays((valores.size - 1 - i).toLong())
            lista.add(Leitura("carga", "t1", data, valores[i], OrigemDado.SINTETICA))
        }
        return lista
    }

    @Test
    fun regressaoEmRetaPerfeita() {
        val xs = listOf(0.0, 1.0, 2.0, 3.0)
        val ys = listOf(1.0, 3.0, 5.0, 7.0) // y = 1 + 2x
        val reta = motor.regressaoLinear(xs, ys)
        assertEquals(1.0, reta.a, 0.001)
        assertEquals(2.0, reta.b, 0.001)
        assertEquals(1.0, reta.r2, 0.001)
    }

    @Test
    fun tendenciaDeAltaGeraAlertaCritico() {
        // sobe 2 por dia: 70, 72, 74 ... 96
        val valores = (0 until 14).map { 70.0 + it * 2 }
        val alerta = motor.avaliar(indicador, territorio, criarLeituras(valores), hoje)
        assertNotNull(alerta)
        assertEquals(NivelAlerta.CRITICO, alerta!!.nivel)
        assertTrue(alerta.justificativa.isNotBlank())
    }

    @Test
    fun valoresBaixosNaoGeramAlerta() {
        val valores = List(14) { 50.0 }
        assertNull(motor.avaliar(indicador, territorio, criarLeituras(valores), hoje))
    }

    @Test
    fun dadosDesatualizadosNaoGeramAlerta() {
        val valores = (0 until 14).map { 70.0 + it * 2 }
        val leituras = criarLeituras(valores, terminaEm = hoje.minusDays(10))
        assertTrue(motor.dadosDesatualizados(leituras, hoje))
        assertNull(motor.avaliar(indicador, territorio, leituras, hoje))
    }

    @Test
    fun poucasLeiturasNaoGeramAlerta() {
        val valores = listOf(90.0, 92.0, 94.0)
        assertNull(motor.avaliar(indicador, territorio, criarLeituras(valores), hoje))
    }
}