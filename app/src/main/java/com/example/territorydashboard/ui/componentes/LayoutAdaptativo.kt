package com.example.territorydashboard.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.territorydashboard.ui.theme.Espaco

enum class TamanhoTela { COMPACTO, MEDIO, EXPANDIDO }

// largura maxima do conteudo: acima disso as linhas de texto ficam longas demais para ler
val LARGURA_MAXIMA_CONTEUDO = 840.dp

// pontos de quebra pelo conteudo, nao pelo modelo do aparelho:
// um card precisa de uns 300dp para caber sem apertar, entao 2 cards lado a lado pedem 600dp
fun tamanhoTela(largura: Dp): TamanhoTela {
    return when {
        largura < 600.dp -> TamanhoTela.COMPACTO
        largura < 840.dp -> TamanhoTela.MEDIO
        else -> TamanhoTela.EXPANDIDO
    }
}

fun colunasPara(tamanho: TamanhoTela): Int {
    return if (tamanho == TamanhoTela.COMPACTO) 1 else 2
}

// coloca ate "colunas" cards lado a lado; se faltar card na linha, completa com espaco vazio
@Composable
fun <T> LinhaDeCards(
    itens: List<T>,
    colunas: Int,
    card: @Composable (T) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Espaco.medio)
    ) {
        for (item in itens) {
            Box(Modifier.weight(1f)) {
                card(item)
            }
        }
        repeat(colunas - itens.size) {
            Spacer(Modifier.weight(1f))
        }
    }
}