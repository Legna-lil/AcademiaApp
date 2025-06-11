package com.example.academiaui.feature_agent.service

import com.example.academiaui.feature_agent.data.model.ChatChunkResponse
import com.example.academiaui.feature_agent.data.model.ChatRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming


interface DeepSeekService {
    @Headers("Authorization: Bearer $DEEPSEEK_API_KEY", "Content-Type: application/json")
    @POST("chat/completions")
    @Streaming
    suspend fun sendRequest(
        @Body request: ChatRequest
    ): Response<ResponseBody>
}
const val DEEPSEEK_API_KEY = "sk-5606f67f82cc47eea0fc5f0a881e6290"