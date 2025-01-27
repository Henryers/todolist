package com.example.todolist.tab2

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.todolist.R
import com.example.todolist.dataclass.SubTask

class TaskDayAdapter(context: Context, private val resource: Int, private val items: List<SubTask>)
    : ArrayAdapter<SubTask>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        val contentTextView = view.findViewById<TextView>(R.id.contentTextView)
        val importanceTextView = view.findViewById<TextView>(R.id.importanceTextView)

        val item = getItem(position)
        titleTextView.text = item?.title
        contentTextView.text = item?.content

        // 根据 importance 的值设置 importanceTextView 的背景颜色和文本内容
        when (item?.importance) {
            "1" -> {
                importanceTextView.text = "小事"
                importanceTextView.background.colorFilter =
                    PorterDuffColorFilter(Color.parseColor("#00CC66"),
                        PorterDuff.Mode.SRC_IN)
                importanceTextView.setTextColor(Color.WHITE)
            }
            "2" -> {
                importanceTextView.text = "中等"
                importanceTextView.background.colorFilter =
                    PorterDuffColorFilter(Color.parseColor("#CCCC00"),
                        PorterDuff.Mode.SRC_IN)
                importanceTextView.setTextColor(Color.WHITE)
            }
            "3" -> {
                importanceTextView.text = "重要"
                importanceTextView.background.colorFilter =
                    PorterDuffColorFilter(Color.parseColor("#CC0000"),
                        PorterDuff.Mode.SRC_IN)
                importanceTextView.setTextColor(Color.WHITE)
            }
            else -> {
                importanceTextView.text = "未知"
                importanceTextView.background.colorFilter =
                    PorterDuffColorFilter(Color.parseColor("#CCCCCC"),
                        PorterDuff.Mode.SRC_IN)
                importanceTextView.setTextColor(Color.BLACK)
            }
        }

        return view
    }
}