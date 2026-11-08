package com.example.territorydashboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CoresClaras = lightColorScheme(
    primary = VerdeAzulado,
    onPrimary = Color.White,
    secondary = AzulAgua,
    onSecondary = Color.White,
    error = StatusCriticoClaro,
    background = FundoClaro,
    surface = Color.White,
    // sem isso os cards e chips ficam com o lilas padrao do Material
    secondaryContainer = Color(0xFFCDE8E4),
    onSecondaryContainer = Color(0xFF00201C),
    surfaceVariant = Color(0xFFE3ECEA),
    surfaceContainerHighest = Color(0xFFE3ECEA)
)

private val CoresEscuras = darkColorScheme(
    primary = VerdeAzuladoEscuro,
    onPrimary = Color.Black,
    secondary = AzulAguaEscuro,
    onSecondary = Color.Black,
    error = StatusCriticoEscuro,
    background = FundoEscuro,
    surface = Color(0xFF1E1E1E),
    secondaryContainer = Color(0xFF1F4D47),
    onSecondaryContainer = Color(0xFFCDE8E4),
    surfaceVariant = Color(0xFF2A3230),
    surfaceContainerHighest = Color(0xFF2A3230)
)

// cores de status que o Material nao tem: normal, atencao, critico e sem dados
data class CoresStatus(
    val normal: Color,
    val atencao: Color,
    val critico: Color,
    val semDados: Color
)

private val StatusClaro = CoresStatus(StatusNormalClaro, StatusAtencaoClaro, StatusCriticoClaro, SemDadosClaro)
private val StatusEscuro = CoresStatus(StatusNormalEscuro, StatusAtencaoEscuro, StatusCriticoEscuro, SemDadosEscuro)

@Composable
fun coresStatus(): CoresStatus {
    return if (isSystemInDarkTheme()) StatusEscuro else StatusClaro
}

// espacamentos padrao, para nao ter numero solto nas telas
object Espaco {
    val pequeno = 4.dp
    val medio = 8.dp
    val grande = 16.dp
    val extra = 24.dp
}

private val Formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun TerritoryDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // sem cor dinamica (Android 12+) para o app manter a identidade propria
    val cores = if (darkTheme) CoresEscuras else CoresClaras

    MaterialTheme(
        colorScheme = cores,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}