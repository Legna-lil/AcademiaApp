package com.example.academiaui.feature_agent.data.model

import com.google.gson.annotations.SerializedName

data class ChoiceChunk(
    @SerializedName("delta") val delta: Delta,
    @SerializedName("index") val index: Int,
    @SerializedName("finish_reason") val finish_reason: String?
)