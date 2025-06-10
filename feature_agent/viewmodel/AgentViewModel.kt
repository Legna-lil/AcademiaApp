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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AgentViewModel @Inject constructor(
    private val deepSeekService: DeepSeekService
) : ViewModel() {

    // 使用 StateFlow 替代 LiveData 以便更好地处理流式数据
    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState: StateFlow<ChatState> get() = _chatState.asStateFlow()

    private val _query = MutableStateFlow<String>("提出一个问题吧！")
    val query: StateFlow<String> get() = _query.asStateFlow()

    val showChatDialog = mutableStateOf(false)
    // 当前累积的回复内容
    private var accumulatedResponse = StringBuilder()

    // 密封类表示不同状态
    sealed class ChatState {
        object Idle : ChatState()
        object Loading : ChatState()
        data class Content(val text: String) : ChatState()
        data class Success(val fullText: String) : ChatState()
        data class Error(val message: String) : ChatState()
    }

    fun sendMessage(userInput: String) {
        viewModelScope.launch {
            try {
                // 1. 创建请求（使用 RequestMessage）
                val request = ChatRequest(
                    messages = listOf(
                        RequestMessage(role = "user", content = userInput)
                    )
                )
                // 2. 发送流式请求
                val response = withContext(Dispatchers.IO) {
                    deepSeekService.sendRequest(request)
                }
                if (!response.isSuccessful) {
                    _chatState.value = ChatState.Error("API错误: ${response.code()}")
                    return@launch
                }
                // 3. 处理流式响应
                response.body()?.source()?.use { source ->
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line() ?: continue

                        if (line.startsWith("data:") && line != "data: [DONE]") {
                            val json = line.substringAfter("data:").trim()
                            processChunk(json)
                        }  else if (line == "data: [DONE]") {
                            break
                        }
                    }
                }
                // 4. 完成处理
                _chatState.value = ChatState.Success(accumulatedResponse.toString())

            } catch (e: Exception) {
                _chatState.value = ChatState.Error("异常: ${e.localizedMessage}")
            }
        }
    }

    private fun processChunk(json: String) {
        try {
            // 使用 Gson 解析（您可以根据需要替换为其他库）
            val chunk = Gson().fromJson(json, ChatChunkResponse::class.java)
            val content = chunk.choices.firstOrNull()?.delta?.content
            if (!content.isNullOrBlank()) {
                // 累积内容并更新状态
                accumulatedResponse.append(content)
                _chatState.value = ChatState.Content(accumulatedResponse.toString())
            }
        } catch (e: Exception) {
            Log.e("Agent", "解析错误: $json", e)
        }
    }

    fun toggleShowDialog() {
        showChatDialog.value = !showChatDialog.value
    }

    fun setQuery(query: String) {
        _query.value = query
        Log.i("Agent", _query.value)
    }
}