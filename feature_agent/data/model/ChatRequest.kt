package com.example.academiaui.feature_agent.data.model

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("model") val model: String = "deepseek-chat",
    @SerializedName("messages") val messages: List<RequestMessage>,
    @SerializedName("stream") val stream: Boolean = true
)