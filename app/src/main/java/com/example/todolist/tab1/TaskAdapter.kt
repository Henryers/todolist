package com.example.todolist.tab1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.todolist.dataclass.Task

class TaskAdapter(context: Context, tasks: List<Task>) : ArrayAdapter<Task>(context, 0, tasks) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val task = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        val titleView = view.findViewById<TextView>(android.R.id.text1)
        val contentView = view.findViewById<TextView>(android.R.id.text2)

        titleView.text = task.title
        contentView.text = task.content

        return view
    }
}
