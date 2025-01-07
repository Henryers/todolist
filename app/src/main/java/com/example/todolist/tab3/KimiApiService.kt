package com.example.todolist.tab3

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface KimiApiService {
    @Headers("Authorization: Bearer sk-TcbmMKIKXbRrejmmcy9BPSY9qEXWTzBYr81nMNj5rYxuITPI")  // 实际的 API Key
    @POST("https://api.moonshot.cn/v1/chat/completions")  // 实际的 API 路径
    fun getChatResponse(@Body requestBody: ChatRequest): Call<ChatResponse>
}

// 定义请求数据结构
data class ChatRequest(
    val model: String,
    val messages: List<Map<String, String>>,
    val temperature: Double
)
// 定义响应数据结构
data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageContent
)

data class MessageContent(
    val content: String
)

