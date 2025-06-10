package com.example.academiaui.feature_agent.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Delta(
    val role: String?,
    val content: String?
)