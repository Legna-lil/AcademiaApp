package com.example.academiaui.feature_agent.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academiaui.feature_agent.data.model.ChatChunkResponse
import com.example.academiaui.feature_agent.data.model.ChatRequest
import com.example.academiaui.feature_agent.data.model.RequestMessage
import com.example.academiaui.feature_agent.service.DeepSeekService
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgentViewModel @Inject constructor(
    private val deepSeekService: DeepSeekService
) : ViewModel() {

    // 使用列表存储所有聊天消息
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _query = MutableStateFlow<String>("提出一个问题吧！")
    val query: StateFlow<String> = _query.asStateFlow()

    // 当前是否正在加载
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val showChatDialog = mutableStateOf(false)

    // 消息数据类
    data class ChatMessage(
        val id: String = UUID.randomUUID().toString(), // 唯一ID用于标识消息
        val text: String,
        val isUser: Boolean,
        val timestamp: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
        val isComplete: Boolean = true, // 流式消息是否完成
        val isError: Boolean = false
    )

    fun sendMessage(userInput: String) {
        viewModelScope.launch {
            try {
                // 添加用户消息
                addMessage(userInput, true)
                // 添加初始AI消息（占位）
                val aiMessage = addMessage("思考中...", false, false)
                _isLoading.value = true
                // 创建请求
                val request = ChatRequest(
                    messages = listOf(RequestMessage(role = "user", content = userInput))
                )
                // 发送流式请求
                val response = withContext(Dispatchers.IO) {
                    deepSeekService.sendRequest(request)
                }
                if (!response.isSuccessful) {
                    updateMessage(aiMessage.id, "API错误: ${response.code()}", true)
                    return@launch
                }
                // 处理流式响应
                val source = response.body()?.source()
                source?.use { source ->
                    val buffer = StringBuilder()
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line() ?: continue
                        when {
                            line.startsWith("data:") && line != "data: [DONE]" -> {
                                val json = line.substringAfter("data:").trim()
                                val content = parseChunkContent(json)
                                if (content != null) {
                                    buffer.append(content)
                                    updateMessage(aiMessage.id, buffer.toString(), false)
                                    delay(25)
                                }
                            }
                            line == "data: [DONE]" -> {
                                markMessageComplete(aiMessage.id)
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                updateMessage(_chatMessages.value.last().id, "异常: ${e.localizedMessage}", true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean, isComplete: Boolean = true): ChatMessage {
        val newMessage = ChatMessage(
            text = text,
            isUser = isUser,
            isComplete = isComplete
        )
        _chatMessages.update { currentList ->
            currentList + newMessage
        }
        return newMessage
    }

    private fun updateMessage(id: String, newText: String, isError: Boolean) {
        _chatMessages.update { messages ->
            messages.map { message ->
                if (message.id == id) {
                    message.copy(text = newText, isError = isError)
                } else {
                    message
                }
            }
        }
    }

    private fun markMessageComplete(id: String) {
        _chatMessages.update { messages ->
            messages.map { message ->
                if (message.id == id) {
                    message.copy(isComplete = true)
                } else {
                    message
                }
            }
        }
    }

    private fun parseChunkContent(json: String): String? {
        return try {
            Log.i("Agent", "Original JSON: $json")
            val jsonObject = JSONObject(json)
            val content = jsonObject.optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("delta")
                ?.optString("content", "")
                ?.takeIf { it.isNotBlank() }
            Log.d("Agent", "Parsed content: $content")
            content
        } catch (e: Exception) {
            Log.e("Agent", "解析错误: $json", e)
            null
        }
    }

    // 清空聊天记录
    fun clearMessages() {
        _chatMessages.value = emptyList()
        _query.value = "提出一个问题吧！"
    }

    fun toggleShowDialog() {
        showChatDialog.value = !showChatDialog.value
    }

    fun setQuery(query: String) {
        _query.value = query
    }
}