package com.example.todolist.dataclass

data class CalendarDay(
    val day: Int,
    val dateInMillis: Long,     // 当天的时间戳
    val tasks: List<String>     // 存储当天的任务
)
