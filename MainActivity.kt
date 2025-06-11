package com.example.academiaui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.academiaui.core.ui.theme.AcademiaUITheme
import com.example.academiaui.core.ui.components.AcademiaApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcademiaUITheme {
                AcademiaApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

