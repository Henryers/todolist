package com.example.todolist.dataclass

data class Task(
    val id: Long,
    var title: String,
    var content: String,
    var attachment: String?, // 文件路径
    var dueDate: Long, // 时间戳
    var importance: Int, // 重要程度
    var category: String,
    var tags: List<String>,
    var isCompleted: Boolean = false
)
