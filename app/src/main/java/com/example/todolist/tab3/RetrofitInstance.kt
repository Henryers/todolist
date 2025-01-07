package com.example.todolist.tab3

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//object RetrofitInstance {
//    private const val BASE_URL = "https://api.moonshot.cn/v1/" // 替换为你的 Kimi API 基础 URL
//
//    val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    fun <T> create(service: Class<T>): T {
//        return retrofit.create(service)
//    }
//}



import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

// 这个 RetrofitInstance 单例对象完成了 Retrofit 框架的基础配置工作
// 包括设置请求基础 URL、配置网络请求客户端、添加数据转换器等，并且提供了一个方便的方法来创建具体的网络服务接口实例，
// 以便在项目中便捷地进行网络请求相关操作。
object RetrofitInstance {
    private const val BASE_URL = "https://api.moonshot.cn/v1/"

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}

