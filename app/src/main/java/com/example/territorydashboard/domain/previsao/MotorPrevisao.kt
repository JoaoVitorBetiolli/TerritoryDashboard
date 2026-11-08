package com.example.territorydashboard.domain.previsao

import com.example.territorydashboard.domain.modelo.Alerta
import com.example.territorydashboard.domain.modelo.Indicador
import com.example.territorydashboard.domain.modelo.Leitura
import com.example.territorydashboard.domain.modelo.NivelAlerta
import com.example.territorydashboard.domain.modelo.OrigemDado
import com.example.territorydashboard.domain.modelo.Territorio
import java.time.LocalDate
import java.time.temporal.ChronoUnit

const val DIAS_JANELA = 14
const val DIAS_PREVISAO = 5
const val MINIMO_LEITURAS = 7
const val VALIDADE_DIAS = 3

data class Reta(val a: Double, val b: Double, val r2: Double)

class MotorPrevisao {

    fun dadosDesatualizados(leituras: List<Leitura>, hoje: LocalDate): Boolean {
        if (leituras.isEmpty()) return true
        val ultimaData = leituras.maxOf { it.data }
        val diasSemDados = ChronoUnit.DAYS.between(ultimaData, hoje)
        return diasSemDados > VALIDADE_DIAS
    }

    fun avaliar(
        indicador: Indicador,
        territorio: Territorio,
        leituras: List<Leitura>,
        hoje: LocalDate
    ): Alerta? {
        // com dado velho o app nao arrisca previsao
        if (dadosDesatualizados(leituras, hoje)) return null

        val inicioJanela = hoje.minusDays((DIAS_JANELA - 1).toLong())
        val janela = leituras
            .filter { !it.data.isBefore(inicioJanela) }
            .sortedBy { it.data }

        if (janela.size < MINIMO_LEITURAS) return null

        val xs = janela.map { ChronoUnit.DAYS.between(inicioJanela, it.data).toDouble() }
        val ys = janela.map { it.valor }
        val reta = regressaoLinear(xs, ys)

        val xFuturo = (DIAS_JANELA - 1 + DIAS_PREVISAO).toDouble()
        val previsto = reta.a + reta.b * xFuturo
        val atual = janela.last().valor
        val referencia = maxOf(previsto, atual)

        val nivel = when {
            referencia >= indicador.limiarCritico -> NivelAlerta.CRITICO
            referencia >= indicador.limiarAtencao -> NivelAlerta.ATENCAO
            referencia >= indicador.limiarAtencao * 0.9 -> NivelAlerta.INFORMATIVO
            else -> return null
        }

        val titulo = when (nivel) {
            NivelAlerta.CRITICO -> "Risco alto: ${indicador.nome} em ${territorio.nome}"
            NivelAlerta.ATENCAO -> "Atenção: ${indicador.nome} em ${territorio.nome}"
            NivelAlerta.INFORMATIVO -> "Acompanhar: ${indicador.nome} em ${territorio.nome}"
        }

        val unidade = indicador.unidade
        val justificativa =
            "Nos últimos ${janela.size} registros, o valor variou em média " +
                    "${formatar(reta.b)} $unidade por dia (regressão linear). " +
                    "Valor atual: ${formatar(atual)} $unidade. " +
                    "Previsão para daqui a $DIAS_PREVISAO dias: ${formatar(previsto)} $unidade. " +
                    "Limiares: atenção ${formatar(indicador.limiarAtencao)} $unidade " +
                    "e crítico ${formatar(indicador.limiarCritico)} $unidade."

        val fonte = if (janela.last().origem == OrigemDado.SINTETICA) {
            "Dados sintéticos (script gerar_dados.py)"
        } else {
            "Dados públicos"
        }

        return Alerta(
            id = "${indicador.id}-${territorio.id}-$hoje",
            territorioId = territorio.id,
            indicadorId = indicador.id,
            nivel = nivel,
            titulo = titulo,
            justificativa = justificativa,
            confianca = calcularConfianca(reta.r2, janela.size),
            periodoPrevisto = "próximos $DIAS_PREVISAO dias",
            fonte = fonte,
            atualizadoEm = janela.last().data,
            validoAte = hoje.plusDays(1)
        )
    }

    // valor previsto para daqui a DIAS_PREVISAO dias (null quando nao da para prever)
    fun projetar(leituras: List<Leitura>, hoje: LocalDate): Double? {
        if (dadosDesatualizados(leituras, hoje)) return null

        val inicioJanela = hoje.minusDays((DIAS_JANELA - 1).toLong())
        val janela = leituras
            .filter { !it.data.isBefore(inicioJanela) }
            .sortedBy { it.data }

        if (janela.size < MINIMO_LEITURAS) return null

        val xs = janela.map { ChronoUnit.DAYS.between(inicioJanela, it.data).toDouble() }
        val ys = janela.map { it.valor }
        val reta = regressaoLinear(xs, ys)

        return reta.a + reta.b * (DIAS_JANELA - 1 + DIAS_PREVISAO)
    }


    fun regressaoLinear(xs: List<Double>, ys: List<Double>): Reta {
        val n = xs.size
        val mediaX = xs.average()
        val mediaY = ys.average()

        var somaXY = 0.0
        var somaXX = 0.0
        for (i in 0 until n) {
            somaXY += (xs[i] - mediaX) * (ys[i] - mediaY)
            somaXX += (xs[i] - mediaX) * (xs[i] - mediaX)
        }

        val b = if (somaXX == 0.0) 0.0 else somaXY / somaXX
        val a = mediaY - b * mediaX

        // r2: quanto a reta explica os pontos (0 = nada, 1 = perfeito)
        var erro = 0.0
        var total = 0.0
        for (i in 0 until n) {
            val estimado = a + b * xs[i]
            erro += (ys[i] - estimado) * (ys[i] - estimado)
            total += (ys[i] - mediaY) * (ys[i] - mediaY)
        }
        val r2 = if (total == 0.0) 0.0 else 1 - erro / total

        return Reta(a, b, r2)
    }

    fun calcularConfianca(r2: Double, quantidade: Int): Int {
        // metade vem do ajuste da reta, metade de quantos dias tem dado
        val completude = quantidade.toDouble() / DIAS_JANELA
        val valor = (r2 * 0.5 + completude * 0.5) * 100
        return valor.toInt().coerceIn(0, 100)
    }

    private fun formatar(valor: Double): String {
        return String.format("%.1f", valor)
    }
}