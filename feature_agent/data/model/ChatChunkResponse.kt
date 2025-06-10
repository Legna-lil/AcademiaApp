package com.example.academiaui.feature_agent.data.model

import com.google.gson.annotations.SerializedName

// 支持流式
data class ChatChunkResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("object") val objectType: String?,
    @SerializedName("created") val created: Long?,
    @SerializedName("model") val model: String?,
    @SerializedName("choices") val choices: List<ChoiceChunk>
)