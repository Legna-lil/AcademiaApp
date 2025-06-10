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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.feature_agent.viewmodel.AgentViewModel
import com.example.academiaui.feature_agent.viewmodel.AgentViewModel.ChatState


@Preview
@Composable
fun ChatContent(
    agentViewModel: AgentViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val messages by agentViewModel.chatState.collectAsState()
    val query by agentViewModel.query.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Log.i("Agent", "Chat Content")
        Column {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text( query,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp),
                reverseLayout = true
            ) {
                when (messages) {
                    is ChatState.Idle -> {
                        item {
                            Text("请开始与AI助手对话...", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    is ChatState.Loading -> {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("思考中...", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                    is ChatState.Content -> {
                        val content = (messages as ChatState.Content).text
                        item {
                            MessageBubble(
                                text = content,
                                isUser = false,
                                timestamp = "刚刚"
                            )
                        }
                    }
                    is ChatState.Success -> {
                        val content = (messages as ChatState.Success).fullText
                        item {
                            MessageBubble(
                                text = content,
                                isUser = false,
                                timestamp = "刚刚"
                            )
                        }
                    }
                    is ChatState.Error -> {
                        val error = (messages as ChatState.Error).message
                        item {
                            MessageBubble(
                                text = "错误: $error",
                                isUser = false,
                                isError = true,
                                timestamp = "刚刚"
                            )
                        }
                    }
                }
            }
        }
    }
}