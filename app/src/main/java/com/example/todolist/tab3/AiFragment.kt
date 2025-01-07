package com.example.todolist.tab3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AiFragment : Fragment() {

    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 将布局文件设置为 fragment 视图
        val view = inflater.inflate(R.layout.fragment_ai, container, false)

        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        buttonSend.setOnClickListener {
            val userMessage = editTextMessage.text.toString()
            if (userMessage.isNotBlank()) {
                addMessage(userMessage, true)
                sendMessageToApi(userMessage)
                editTextMessage.text.clear()
            }
        }

        return view
    }

    private fun addMessage(text: String, isUser: Boolean) {
        messages.add(Message(text, isUser))
        adapter.notifyItemInserted(messages.size - 1)
    }

    private fun sendMessageToApi(userMessage: String) {
        // 创建网络请求接口实例
        val service = RetrofitInstance.create(KimiApiService::class.java)
        val requestBody = ChatRequest(
            model = "moonshot-v1-8k",
            messages = listOf(
                mapOf("role" to "system", "content" to "你是 Kimi，由 Moonshot AI 提供的人工智能助手..."),
                mapOf("role" to "user", "content" to userMessage)
            ),
            temperature = 0.3
        )

        service.getChatResponse(requestBody).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val botResponse = response.body()?.choices?.get(0)?.message?.content
                        ?: "抱歉，我无法理解您的问题。"
                    addMessage(botResponse, false)
                } else {
                    addMessage("请求失败，错误代码: ${response.code()}", false)
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                addMessage("请求失败: ${t.message}", false)
            }
        })
    }
}
