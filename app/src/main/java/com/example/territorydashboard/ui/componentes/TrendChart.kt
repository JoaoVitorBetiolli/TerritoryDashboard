package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.territorydashboard.ui.theme.Espaco
import com.example.territorydashboard.ui.theme.coresStatus

// grafico de linha simples desenhado no Canvas:
// historico (linha cheia), previsao (tracejada) e os dois limiares
@Composable
fun TrendChart(
    valores: List<Double>,
    previsto: Double?,
    diasPrevisao: Int,
    limiarAtencao: Double,
    limiarCritico: Double,
    unidade: String,
    descricao: String,
    modifier: Modifier = Modifier
) {
    val corHistorico = MaterialTheme.colorScheme.primary
    val corPrevisao = MaterialTheme.colorScheme.secondary
    val cores = coresStatus()

    Column(modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .semantics { contentDescription = descricao }
        ) {
            if (valores.isEmpty()) return@Canvas

            // escala do eixo y: cabe o historico, a previsao e os limiares
            val todos = valores + listOf(limiarAtencao, limiarCritico) + listOfNotNull(previsto)
            var minimo = todos.min()
            var maximo = todos.max()
            val folga = (maximo - minimo) * 0.1 + 1
            minimo -= folga
            maximo += folga

            // eixo x: dias do historico + dias da previsao
            val totalX = (valores.size - 1 + diasPrevisao).toFloat()

            fun posX(i: Int): Float = i / totalX * size.width
            fun posY(v: Double): Float = (size.height - (v - minimo) / (maximo - minimo) * size.height).toFloat()

            val tracejado = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))

            drawLine(
                cores.atencao,
                Offset(0f, posY(limiarAtencao)),
                Offset(size.width, posY(limiarAtencao)),
                strokeWidth = 2.dp.toPx(),
                pathEffect = tracejado
            )
            drawLine(
                cores.critico,
                Offset(0f, posY(limiarCritico)),
                Offset(size.width, posY(limiarCritico)),
                strokeWidth = 2.dp.toPx(),
                pathEffect = tracejado
            )

            val caminho = Path()
            for (i in valores.indices) {
                val x = posX(i)
                val y = posY(valores[i])
                if (i == 0) caminho.moveTo(x, y) else caminho.lineTo(x, y)
            }
            drawPath(caminho, corHistorico, style = Stroke(width = 3.dp.toPx()))

            if (previsto != null) {
                val inicio = Offset(posX(valores.size - 1), posY(valores.last()))
                val fim = Offset(size.width, posY(previsto))
                drawLine(corPrevisao, inicio, fim, strokeWidth = 3.dp.toPx(), pathEffect = tracejado)
                drawCircle(corPrevisao, radius = 5.dp.toPx(), center = fim)
            }
        }

        Spacer(Modifier.height(Espaco.medio))
        ItemLegenda(corHistorico, "Histórico (${valores.size} dias)")
        if (previsto != null) {
            ItemLegenda(corPrevisao, "Previsão em $diasPrevisao dias: ${formatarNumero(previsto)} $unidade")
        }
        ItemLegenda(cores.atencao, "Limiar de atenção: ${formatarNumero(limiarAtencao)} $unidade")
        ItemLegenda(cores.critico, "Limiar crítico: ${formatarNumero(limiarCritico)} $unidade")
    }
}

@Composable
private fun ItemLegenda(cor: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(12.dp)
                .background(cor, CircleShape)
        )
        Spacer(Modifier.width(Espaco.medio))
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}