package com.example.academiaui.feature_agent.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.feature_agent.viewmodel.AgentViewModel

@Preview
@Composable
fun ChatContent(
    agentViewModel: AgentViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val messages by agentViewModel.chatMessages.collectAsState()
    val query by agentViewModel.query.collectAsState()
    val isLoading by agentViewModel.isLoading.collectAsState()

    // 重启页面时清空消息
    LaunchedEffect(Unit) {
        agentViewModel.clearMessages()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column {
//            Row(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text(
//                    text = query,
//                    style = MaterialTheme.typography.titleLarge
//                )
//            }

            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                reverseLayout = false
            ) {
                if (messages.isEmpty()) {
                    item {
                        Text(
                            text = "请开始与AI助手对话...",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(messages) { message ->
                        MessageBubble(
                            text = message.text,
                            isUser = message.isUser,
                            isError = message.isError,
                            timestamp = message.timestamp,
                            showProgress = !message.isComplete && !message.isError
                        )
                    }
                }
            }

            // 底部加载指示器
            if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}