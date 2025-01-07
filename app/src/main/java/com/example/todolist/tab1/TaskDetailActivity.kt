package com.example.todolist.tab1

import com.example.todolist.utils.DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.MainActivity
import com.example.todolist.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailActivity : AppCompatActivity() {

    companion object {
        const val EDIT_TASK_REQUEST_CODE = 1
    }

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTaskLauncher: ActivityResultLauncher<Intent>

    // 使用 var 并包含所有数据库字段
    private var titleTextView: TextView? = null
    private var contentTextView: TextView? = null
    private var attachmentTextView: TextView? = null
    private var dueDateTextView: TextView? = null
    private var importanceTextView: TextView? = null
    private var categoryTextView: TextView? = null
    private var tagsTextView: TextView? = null
    private var isCompletedTextView: TextView? = null

    private var editTaskButton: Button? = null
    private var deleteTaskButton: Button? = null

    private var taskId: Long = -1 // 用于存储任务的 ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val btnReturn: Button = findViewById(R.id.btnReturn)
        // 设置“返回”按钮点击事件
        btnReturn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        dbHelper = DatabaseHelper(this)

        titleTextView = findViewById(R.id.titleTextView)
        contentTextView = findViewById(R.id.contentTextView)
        attachmentTextView = findViewById(R.id.attachmentTextView)
        dueDateTextView = findViewById(R.id.dueDateTextView)
        importanceTextView = findViewById(R.id.importanceTextView)
        categoryTextView = findViewById(R.id.categoryTextView)
        tagsTextView = findViewById(R.id.tagsTextView)
        isCompletedTextView = findViewById(R.id.isCompletedTextView)

        editTaskButton = findViewById(R.id.editTaskButton)
        deleteTaskButton = findViewById(R.id.deleteTaskButton)

        // 与编辑页绑定契约，更新参数（不然onCreate中的其他方法只在开始执行一次）
        editTaskLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                // 更新任务的标题、内容和其他字段
                titleTextView?.text = data?.getStringExtra("UPDATED_TASK_TITLE") ?: titleTextView?.text
                contentTextView?.text = data?.getStringExtra("UPDATED_TASK_CONTENT") ?: contentTextView?.text
                attachmentTextView?.text = data?.getStringExtra("UPDATED_TASK_ATTACHMENT") ?: attachmentTextView?.text
                dueDateTextView?.text = data?.getStringExtra("UPDATED_TASK_DUE_DATE") ?: dueDateTextView?.text
                importanceTextView?.text = data?.getStringExtra("UPDATED_TASK_IMPORTANCE") ?: importanceTextView?.text
                categoryTextView?.text = data?.getStringExtra("UPDATED_TASK_CATEGORY") ?: categoryTextView?.text
                tagsTextView?.text = data?.getStringExtra("UPDATED_TASK_TAGS") ?: tagsTextView?.text
                isCompletedTextView?.text = if (data?.getBooleanExtra("UPDATED_TASK_IS_COMPLETED", false) == true) "已完成" else "未完成"
            }
        }

        // 获取传入的任务 ID 和信息
        taskId = intent.getLongExtra("TASK_ID", -1)
        loadTaskData()

        // 修改任务，携带参数，跳转到EditTaskActivity
        editTaskButton?.setOnClickListener {
            val intent = Intent(this, EditTaskActivity::class.java)
            intent.putExtra("TASK_ID", taskId)
            editTaskLauncher.launch(intent)
        }

        // 删除任务，删除成功后跳转回首页
        deleteTaskButton?.setOnClickListener {
            deleteTask()
        }
    }

    private fun deleteTask() {
        // 显示一个确认对话框
        AlertDialog.Builder(this)
            .setTitle("删除任务")
            .setMessage("确定删除这个任务吗？")
            .setPositiveButton("确定") { _, _ ->
                // 用户点击了“确定”按钮，删除任务
                val result = dbHelper.deleteTask(taskId)
                if (result > 0) {
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                    // 跳转到首页
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消") { _, _ ->
                // 用户点击了“取消”按钮，不执行删除操作
                Toast.makeText(this, "取消删除", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun loadTaskData() {
        // 使用任务 ID 从数据库查询任务
        val task = dbHelper.getTaskById(taskId)
        if (task != null) {
            // 填充视图
            titleTextView?.text = task.title
            contentTextView?.text = task.content
            attachmentTextView?.text = task.attachment ?: "无"
            dueDateTextView?.text = SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(Date(task.dueDate))
            importanceTextView?.text = when (task.importance) {
                1 -> "低"
                2 -> "中"
                3 -> "高"
                else -> "普通"
            }
            categoryTextView?.text = task.category ?: "无"
            tagsTextView?.text = task.tags.joinToString(", ") { it } ?: "无标签"  // 将列表转换为字符串显示
            isCompletedTextView?.text = if (task.isCompleted) "已完成" else "未完成"
        } else {
            Toast.makeText(this, "未找到任务", Toast.LENGTH_SHORT).show()
            finish() // 关闭 Activity
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次进入页面时重新加载任务数据
        loadTaskData()
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            val updatedTaskId = data.getLongExtra("UPDATED_TASK_ID", -1)
//            val updatedTitle = data.getStringExtra("UPDATED_TASK_TITLE") ?: ""
//            val updatedContent = data.getStringExtra("UPDATED_TASK_CONTENT") ?: ""
//
//            // 使用更新后的数据刷新页面
//            if (updatedTaskId != -1L) {
//                // 更新任务显示的内容（例如重新查询数据库或直接更新UI）
//                titleTextView.text = updatedTitle
//                contentTextView.text = updatedContent
//            }
//        }
//    }

}