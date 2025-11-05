package com.example.academiaui.feature_agent.service

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

const val DEEPSEEK_API_KEY = "Avoid KEY API LEAK, please replace it with yours."
