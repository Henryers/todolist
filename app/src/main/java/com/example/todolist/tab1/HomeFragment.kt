package com.example.todolist.tab1

import com.example.todolist.utils.DatabaseHelper
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.todolist.R

interface FragmentCallback {
    fun freshTasks()
}

class HomeFragment : Fragment(), FragmentCallback {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var db: DatabaseHelper
    private lateinit var taskListView: ListView
    private lateinit var addTaskButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 用 inflater 将布局文件加载为 View 对象
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化数据库和视图
        db = DatabaseHelper(requireContext())
        taskListView = view.findViewById(R.id.taskListView)
        addTaskButton = view.findViewById(R.id.addTaskButton)

        // 加载任务列表
        loadTasks()

        // 设置新增任务按钮的点击事件
        addTaskButton.setOnClickListener {
            // 跳转到新增任务页面
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivity(intent)
        }

        // 设置任务列表的点击事件，跳转到各个任务的详情页
        taskListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedTask = taskAdapter.getItem(position)
            if (selectedTask != null) {
                val intent = Intent(requireContext(), TaskDetailActivity::class.java)
                intent.putExtra("TASK_ID", selectedTask.id)
                intent.putExtra("TASK_TITLE", selectedTask.title)
                intent.putExtra("TASK_CONTENT", selectedTask.content)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "未找到选中的任务", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTasks() {
        val tasks = db.getTodayTasks() // 从数据库中获取当天任务
        taskAdapter = TaskAdapter(requireContext(), tasks)
        taskListView.adapter = taskAdapter
    }

    override fun freshTasks() {
        val tasks = db.getTodayTasks() // 从数据库中获取当天任务
        taskAdapter = TaskAdapter(requireContext(), tasks)
        taskListView.adapter = taskAdapter
    }


    override fun onResume() {
        super.onResume()
        loadTasks() // 每次返回页面时重新加载数据
    }
}
