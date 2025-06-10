package com.example.academiaui.feature_agent.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.feature_agent.viewmodel.AgentViewModel

@Composable
fun ChatBox(
    agentViewModel: AgentViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var showChatScreen by agentViewModel.showChatDialog
    // 使用密度转换
    Log.i("Agent", "Popup")
    AnimatedVisibility(
        visible = showChatScreen,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center)
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .shadow(16.dp, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 30.dp, horizontal = 10.dp)
                    .fillMaxSize()
            ) {
                WindowTitleBar(
                    onClose = onDismiss,
                )
                ChatContent(
                    modifier = Modifier.weight(1f)
                )
                ChatInput(
                    userInput = userInput,
                    onInputChange = { userInput = it },
                    onSend = {
                        agentViewModel.sendMessage(userInput)
//                        agentViewModel.setQuery(userInput)
                        userInput = ""
                    }
                )
            }
        }
    }
}
