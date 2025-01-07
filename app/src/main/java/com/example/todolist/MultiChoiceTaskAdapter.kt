package com.example.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.todolist.dataclass.Task

class MultiChoiceTaskAdapter(private val context: Context, private val tasks: List<Task>,
                             private var selectedTaskIds: HashSet<Long>) :
    ArrayAdapter<Task>(context, R.layout.list_item_task, tasks) { // 使用自定义列表项布局

    private class ViewHolder {
        lateinit var checkBox: CheckBox
        lateinit var titleView: TextView // 这里应该是TextView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_task, parent, false) // 使用自定义列表项布局
            holder = ViewHolder().apply {
                checkBox = view.findViewById(R.id.checkboxTask)
                titleView = view.findViewById(R.id.titleView) // 这里应该是titleView
            }
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val task = getItem(position) ?: return view
        holder.titleView.text = task.title
        holder.checkBox.isChecked = selectedTaskIds.contains(task.id)
        holder.checkBox.tag = task.id

        holder.checkBox.setOnClickListener {
            val id = it.tag as Long
            if (holder.checkBox.isChecked) {
                selectedTaskIds.add(id)
            } else {
                selectedTaskIds.remove(id)
            }
        }

        return view
    }
}