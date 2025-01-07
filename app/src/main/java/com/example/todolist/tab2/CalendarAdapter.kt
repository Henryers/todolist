package com.example.todolist.tab2

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.dataclass.CalendarDay
import com.example.todolist.R

/**
 * 适配器类
 * 用于将一个月的日历数据：List<CalendarDay> 展示在 RecyclerView 中
 * 具体是定义一些方法，功能为：
 * 1、将一个个item_calendar_day.xml给放到Calendar中
 * 2、显示当前的日期及其任务
 */
class CalendarAdapter(
    private val calendarDays: List<CalendarDay>,
    private val onItemClick: (CalendarDay) -> Unit // 点击事件
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    // 创建 ViewHolder 的方法，负责加载单项布局 item_calendar_day.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    // 绑定数据的方法，将指定位置的数据与 ViewHolder 绑定
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {

        val calendarDay = calendarDays[position] // 获取当前位置的 CalendarDay 数据
        holder.bind(calendarDay) // 将数据绑定到 ViewHolder 中的视图
        Log.e("error", position.toString())
        Log.e("error", calendarDay.dateInMillis.toString())
        Log.e("error", calendarDays.toString())
        // 设置点击事件，将点击的日期传递出去
        holder.itemView.setOnClickListener {
            onItemClick(calendarDay)
        }
    }

    // 返回数据项的数量，即列表的大小
    override fun getItemCount() = calendarDays.size

    // 内部类，用于持有每个单项布局(每一天)的视图
    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 定义单项布局中的视图
        private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView) // 显示日期
//        private val taskTextView: TextView = itemView.findViewById(R.id.taskTextView) // 显示任务
        private val taskContainer: LinearLayout = itemView.findViewById(R.id.taskContainer)  //

        // 绑定数据的方法，用于将 CalendarDay 数据绑定到视图中
        fun bind(calendarDay: CalendarDay) {
            dayTextView.text = calendarDay.day.toString()

            // 清除之前的任务视图，避免重复添加
            taskContainer.removeAllViews()

            // 为每个任务动态创建 TextView
            calendarDay.tasks.forEach { task ->
                val taskTextView = TextView(itemView.context).apply {
                    text = task
                    setTypeface(null, Typeface.BOLD) // 设置加粗
                    setTextColor(ContextCompat.getColor(context, R.color.white)) // 设置文字颜色
                    setBackgroundColor(ContextCompat.getColor(context, R.color.yellow2)) // 设置背景颜色
                    setPadding(8, 1, 1, 1) // 设置内边距
                }

                // 设置 margin
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(1, 2, 1, 2) // 左、上、右、下的 margin
                taskTextView.layoutParams = params

                // 将每个任务的 TextView 添加到 taskContainer 中
                taskContainer.addView(taskTextView)
            }
        }
    }
}






//        fun bind(calendarDay: CalendarDay) {
//            // 设置日期
//            dayTextView.text = calendarDay.day.toString()
//
//            // 判断当天是否有任务
//            if (calendarDay.tasks.isNotEmpty()) {
//                taskTextView.visibility = View.VISIBLE // 显示任务视图
//
//                // 创建一个 SpannableStringBuilder，用于存放带样式的任务文本
//                val spannableBuilder = SpannableStringBuilder()
//                // 为每个任务设置样式，并添加到 spannableBuilder 中
//                calendarDay.tasks.forEach { task ->
//                    val spannable = SpannableString(task + "\n") // 每个任务后加换行
//                    // 设置任务的样式（例如加粗和颜色）
//                    spannable.setSpan(
//                        StyleSpan(Typeface.BOLD),  // 加粗样式
//                        0, task.length,
//                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    // 设置颜色（可根据不同条件设置不同颜色）
//                    val color = ContextCompat.getColor(itemView.context, R.color.blue1)
//                    spannable.setSpan(
//                        ForegroundColorSpan(color),
//                        0, task.length,
//                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    // 设置背景颜色
//                    val backgroundColor = ContextCompat.getColor(itemView.context, R.color.white1)
//                    spannable.setSpan(
//                        BackgroundColorSpan(backgroundColor),
//                        0, task.length,
//                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    spannableBuilder.append(spannable) // 将样式化的任务文本添加到构建器
//                }
//                // 将生成的 SpannableStringBuilder 设置为 TextView 的文本
//                taskTextView.text = spannableBuilder
//                // taskTextView.text = calendarDay.tasks.joinToString("\n") // 将任务列表转换为字符串显示
//            } else {
//                taskTextView.visibility = View.GONE // 隐藏任务视图
//            }
//        }