package com.example.todolist

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.todolist.dataclass.Task
import com.example.todolist.tab1.HomeFragment
import com.example.todolist.tab1.TaskAdapter
import com.example.todolist.tab2.CalendarFragment
import com.example.todolist.tab3.AiFragment
import com.example.todolist.utils.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
//    val homeFragment: HomeFragment

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)

        // 查询未完成的任务并显示Dialog
        val incompleteTasks = db.getIncompleteTasks()
        if (incompleteTasks.isNotEmpty()) {
            showIncompleteTasksDialog(incompleteTasks)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 设置初始 Fragment
        loadFragment(HomeFragment())

        // 设置 BottomNavigationView 的选择事件监听器
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tab_time -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.tab_calendar -> {
                    loadFragment(CalendarFragment())
                    true
                }
                R.id.tab_ai -> {
                    loadFragment(AiFragment())
                    true
                }

                else -> false
            }
        }
    }

    /**
     * 显示弹窗，其中包含多个列表项
     * 用户可自定义选择：将任务挪到当天 or 标记为已完成
     */
    private fun showIncompleteTasksDialog(tasks: List<Task>) {
        val selectedTaskIds = HashSet<Long>() // 用于跟踪选中的任务
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_multi_choice, null)

        val listView = view.findViewById<ListView>(R.id.listViewTasks) // 确保这里的 ID 是 listViewTasks

        val adapter = MultiChoiceTaskAdapter(this, tasks, selectedTaskIds)
        listView.adapter = adapter

        dialogBuilder.setView(view)
            .setTitle("未完成的任务")
            .setPositiveButton("将任务挪到当天") { dialog, which ->
                val selectedTasks = tasks.filter { task -> selectedTaskIds.contains(task.id) }
                if (selectedTasks.isNotEmpty()) {
                    // 将所有任务的截止日期更新为当天
                    db.updateTasksDueDate(selectedTasks)
                    // 重新刷新当前Fragment
                    refreshHomeFragmentTasks()
                }
                dialogBuilder.create().dismiss() // 这里不需要再次调用 create()
            }
            .setNegativeButton("标记为已完成") { dialog, which ->
                val selectedTasks = tasks.filter { task -> selectedTaskIds.contains(task.id) }
                if (selectedTasks.isNotEmpty()) {
                    // 将所选任务标记为已完成
                    db.markTasksAsCompleted(selectedTasks)
                    refreshHomeFragmentTasks()
                }
                dialogBuilder.create().dismiss() // 这里不需要再次调用 create()
            }
            .show() // 直接调用 show() 显示对话框
    }

    /**
     * 供测试的简单弹窗方法
     */
    private fun showIncompleteTasksDialog1(tasks: List<Task>) {
        val message = StringBuilder("您有以下未完成的任务：\n")
        tasks.forEach { task ->
            message.append("- ${task.title}\n")
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("未完成的任务")
            .setMessage(message.toString())
            .setPositiveButton("将任务挪到当天") { dialog, which ->
                // 将所有任务的截止日期更新为当天
                db.updateTasksDueDate(tasks)
            }
            .setNegativeButton("标记为已完成") { dialog, which ->
                // 将所有任务标记为已完成
                db.markTasksAsCompleted(tasks)
            }
            .create()

        dialog.show()
    }

    // 加载选中的 Fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // 关闭对话框，刷新当日列表
    fun refreshHomeFragmentTasks() {
        // 获取当前 Activity 的 FragmentManager 实例
        val fragmentManager = supportFragmentManager
        // 查找 HomeFragment
        val homeFragment = fragmentManager.findFragmentById(R.id.fragmentContainer) as? HomeFragment
        // 调用刷新方法接口，实现列表更新
        homeFragment?.freshTasks()
    }

}
