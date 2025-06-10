package com.example.academiaui.feature_agent.presentation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.data.viewmodel.AppStateViewModel
import com.example.academiaui.feature_agent.viewmodel.AgentViewModel


@Composable
fun AgentFAB(
    agentViewModel: AgentViewModel = viewModel()
) {
    FloatingActionButton(
        onClick = {
            agentViewModel.toggleShowDialog()
            Log.i("Agent", "Chat Dialog Open")
        }
    ) {
        Icon(Icons.Outlined.SmartToy, contentDescription = "AI Agent")
    }
}