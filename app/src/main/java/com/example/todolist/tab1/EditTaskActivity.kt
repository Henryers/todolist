package com.example.todolist.tab1

import com.example.todolist.utils.DatabaseHelper
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.dataclass.Task
import java.text.SimpleDateFormat
import java.util.*

class EditTaskActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var attachmentEditText: EditText
    private lateinit var dueDateButton: Button
    private lateinit var importanceSpinner: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var tagsEditText: EditText
    private lateinit var isCompletedCheckbox: CheckBox
    private lateinit var saveTaskButton: Button

    private var taskId: Long = -1 // 用于存储任务的 ID
    private var dueDate: Long = System.currentTimeMillis() // 初始截止日期

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        dbHelper = DatabaseHelper(this)

        // 初始化视图
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        attachmentEditText = findViewById(R.id.attachmentEditText)
        dueDateButton = findViewById(R.id.dueDateButton)
        importanceSpinner = findViewById(R.id.importanceSpinner)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        tagsEditText = findViewById(R.id.tagsEditText)
        isCompletedCheckbox = findViewById(R.id.isCompletedCheckbox)
        saveTaskButton = findViewById(R.id.saveTaskButton)

        // 获取传入的任务 ID
        taskId = intent.getLongExtra("TASK_ID", -1)

        // 日期选择器
        dueDateButton.setOnClickListener {
            showDatePicker()
        }
        // 修改任务
        saveTaskButton.setOnClickListener {
            saveTaskChanges()
        }
    }

    private fun loadTaskData() {
        val task = dbHelper.getTaskById(taskId)
        if (task != null) {
            titleEditText.setText(task.title)
            contentEditText.setText(task.content)
            attachmentEditText.setText(task.attachment ?: "无")
            dueDate = task.dueDate
            dueDateButton.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dueDate))
            importanceSpinner.setSelection(task.importance - 1) // 假设重要性从1开始

            val categories = resources.getStringArray(R.array.category_options)
            val categoryIndex = categories.indexOf(task.category) // 获取 task.category 的索引
            if (categoryIndex >= 0) {
                spinnerCategory.setSelection(categoryIndex)
            } else {
                spinnerCategory.setSelection(0) // 如果找不到匹配项，选择默认值
            }

            tagsEditText.setText(task.tags.joinToString(", ")) // 将标签列表转为逗号分隔的字符串
            isCompletedCheckbox.isChecked = task.isCompleted
        } else {
            Toast.makeText(this, "未找到任务信息", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dueDate

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dueDate = calendar.timeInMillis
                dueDateButton.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveTaskChanges() {
        val newTitle = titleEditText.text.toString()
        val newContent = contentEditText.text.toString()
        val newAttachment = attachmentEditText.text.toString()
        val newImportance = importanceSpinner.selectedItemPosition + 1
        val newCategory = spinnerCategory.selectedItem.toString()
        val newTags = tagsEditText.text.toString().split(",").map { it.trim() }
        val isCompleted = isCompletedCheckbox.isChecked

        if (newTitle.isNotBlank() && newContent.isNotBlank()) {
            // 更新任务数据
            val updatedTask = Task(
                id = taskId,
                title = newTitle,
                content = newContent,
                attachment = newAttachment,
                dueDate = dueDate,
                importance = newImportance,
                category = newCategory,
                tags = newTags,
                isCompleted = isCompleted
            )

            val result = dbHelper.updateTask(updatedTask)
            if (result > 0) {
                val intent = Intent().apply {
                    putExtra("UPDATED_TASK_ID", taskId)
                }
                setResult(RESULT_OK, intent)
                Toast.makeText(this, "任务修改成功", Toast.LENGTH_SHORT).show()
                finish() // 关闭当前页面并返回上一页
            } else {
                Toast.makeText(this, "修改任务失败", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "请填写完整的任务信息", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次进入页面时重新加载任务数据
        loadTaskData()
    }
}
