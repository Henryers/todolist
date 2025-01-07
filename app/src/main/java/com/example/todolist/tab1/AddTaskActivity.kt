package com.example.todolist.tab1

import com.example.todolist.utils.DatabaseHelper
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.dataclass.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var attachment: EditText
    private lateinit var dueDateButton: Button
    private lateinit var importanceSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var tagsEditText: EditText
    private lateinit var isCompletedCheckbox: CheckBox
    private lateinit var submitTaskButton: Button

    private var dueDate: Long? = null // 将 dueDate 初始设为 null，表示还未选择

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        db = DatabaseHelper(this)

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        attachment = findViewById(R.id.attachment)
        dueDateButton = findViewById(R.id.dueDateButton)
        importanceSpinner = findViewById(R.id.spinnerImportance)
        categorySpinner = findViewById(R.id.spinnerCategory)
        tagsEditText = findViewById(R.id.etTags)
        isCompletedCheckbox = findViewById(R.id.checkIsCompleted)
        submitTaskButton = findViewById(R.id.submitTaskButton)
        submitTaskButton = findViewById(R.id.submitTaskButton)

        // 设置按钮点击事件，弹出日期选择对话框
        dueDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    calendar.set(Calendar.HOUR_OF_DAY, 8)  // 设置小时为 0
                    calendar.set(Calendar.MINUTE, 0)       // 设置分钟为 0
                    calendar.set(Calendar.SECOND, 0)       // 设置秒为 0
                    calendar.set(Calendar.MILLISECOND, 0)  // 设置毫秒为 0
                    dueDate = calendar.timeInMillis
                    // 将选择的日期显示在按钮上
                    dueDateButton.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        submitTaskButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            var attachment = attachment.text.toString()
            val importance = importanceSpinner.selectedItemPosition + 1 // 假设 1 表示最低重要性
            val category = categorySpinner.selectedItem.toString()
            val tags = tagsEditText.text.toString().split(",").map { it.trim() } // 将标签以逗号分隔存储为列表
            val isCompleted = isCompletedCheckbox.isChecked


            // 检查是否选择了截止日期
            if (title.isNotBlank() && content.isNotBlank() && dueDate != null) {
                // 创建新的任务对象
                val newTask = Task(
                    id = 0,
                    title = title,
                    content = content,
                    attachment = attachment,
                    dueDate = dueDate!!, // 确保 non-null
                    importance = importance,
                    category = category,
                    tags = tags,
                    isCompleted = isCompleted
                )

                // 插入任务到数据库
                val result = db.insertTask(newTask)
                if (result != -1L) {
                    Toast.makeText(this, "任务添加成功", Toast.LENGTH_SHORT).show()
                    Log.d("AddTaskActivity", "任务已添加到数据库")
                    finish() // 结束当前 Activity 并返回上一页
                } else {
                    Toast.makeText(this, "添加任务失败", Toast.LENGTH_SHORT).show()
                    Log.e("AddTaskActivity", "任务添加到数据库失败")
                }
            } else {
                // 提示用户输入标题、内容并选择截止日期
                Toast.makeText(this, "请填写任务标题、内容并选择截止日期", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
