package com.example.territorydashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.territorydashboard.ui.navegacao.AppNavegacao
import com.example.territorydashboard.ui.theme.TerritoryDashboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TerritoryDashboardTheme {
                AppNavegacao()
            }
        }
    }
}