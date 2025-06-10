package com.example.academiaui.feature_agent.data.model

import com.google.gson.annotations.SerializedName

data class RequestMessage(
    @SerializedName("role") val role: String,   // "user", "assistant", "system"
    @SerializedName("content") val content: String
)