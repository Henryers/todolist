package com.example.todolist.tab2

import com.example.todolist.utils.DatabaseHelper
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 点击某一天，能够显示当天的一个任务列表
 * 每个任务都有展示标题和内容
 */
class TaskDayActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var taskListView: ListView
    private lateinit var dateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_day) // 使用新的布局文件

        val btnReturn: Button = findViewById(R.id.btnReturn)
        // 设置“返回”按钮点击事件
        btnReturn.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)
        taskListView = findViewById(R.id.taskListView)
        dateTextView = findViewById(R.id.dateTextView)

        // 获取传递过来的日期时间戳
        val dateInMillis = intent.getLongExtra("date", 0L)
        Log.e("info", "获取到了吗？")
        Log.e("info", dateInMillis.toString())
        val date = Date(dateInMillis)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateTextView.text = dateFormat.format(date) // 设置显示的日期

        // 从数据库中获取任务列表并格式化为单字符串
        val tasks = dbHelper.getTitlesAndContentsByDate(dateInMillis).map {
            "标题:   ${it.first}\n内容: ${it.second}"
        }
        // 使用简单的 ArrayAdapter 直接将数据加载到 ListView 中
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        taskListView.adapter = adapter
    }
}
