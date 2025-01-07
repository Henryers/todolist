package com.example.todolist.tab2

import com.example.todolist.utils.DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.dataclass.CalendarDay
import com.example.todolist.R
import java.util.Calendar
import java.util.Date

/**
 * 滑动月份视图组件
 * 显示recycler_view_layout.xml中的内容，即RecyclerView
 * 会根据从适配器MonthPagerAdapter.kt中传过来的日期，生成整个月的日期分布
 * 同时去数据库拿这些日期对应下的任务名称，做回显
 */
class MonthFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter

    companion object {
        private const val ARG_DATE = "date"
        // 根据传过来的日期，计算出当月的日期分布
        fun newInstance(date: Date): MonthFragment {
            val fragment = MonthFragment()
            val args = Bundle()
            args.putSerializable(ARG_DATE, date)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化 dbHelper
        dbHelper = DatabaseHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycler_view_layout, container, false)

        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendarDays = generateCalendarData(date)

        dbHelper = DatabaseHelper(requireContext())

        // 用于创建日历视图的容器，布局为一行7个元素的网格文件
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)

        // 点击该月份其中的某一天，能够进行页面跳转，到TaskDayActivity中，进行当天任务列表的展示
        calendarAdapter = CalendarAdapter(calendarDays) { day ->
            // day 是点击的 CalendarDay 对象
            val intent = Intent(requireContext(), TaskDayActivity::class.java)
            intent.putExtra("date", day.dateInMillis) // 假设 day 中包含 dateInMillis 字段，表示当天的时间戳
            startActivity(intent)
        }
        calendarRecyclerView.adapter = calendarAdapter

        return view
    }

    /**
     * 根据某一天，生成这个月的所有日期，包括上个月前几天
     */
    private fun generateCalendarData(date: Date): List<CalendarDay> {
        val daysInMonth = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.time = date

        // 设置到该月的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // 生成上个月的占位日期
        val previousMonth = calendar.clone() as Calendar
        previousMonth.add(Calendar.MONTH, -1)
        val lastDayOfPreviousMonth = previousMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

        /**
         * xx月.apply { set(Calendar.DAY_OF_MONTH, day) } 设置为xx月的第day天
         * .time 获得日期对象
         */
        for (i in 0 until firstDayOfWeek) {
            val day = lastDayOfPreviousMonth - firstDayOfWeek + i + 1
            val previousMonthDate = previousMonth.apply {
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 8)  // 设置小时为 0
                set(Calendar.MINUTE, 0)       // 设置分钟为 0
                set(Calendar.SECOND, 0)       // 设置秒为 0
                set(Calendar.MILLISECOND, 0)  // 设置毫秒为 0
            }.time
            val tasks = dbHelper.getTitlesByDate(previousMonthDate.time) // 查询上个月的任务
            daysInMonth.add(
                CalendarDay(
                day = day,
                dateInMillis = previousMonth.timeInMillis,
                tasks = tasks)
            )
        }

        // 填充本月的日期
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDay) {
            val currentDate = calendar.apply {
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 8)  // 设置小时为 0
                set(Calendar.MINUTE, 0)       // 设置分钟为 0
                set(Calendar.SECOND, 0)       // 设置秒为 0
                set(Calendar.MILLISECOND, 0)  // 设置毫秒为 0
            }.time
            val tasks = dbHelper.getTitlesByDate(currentDate.time) // 查询本月的任务
            daysInMonth.add(
                CalendarDay(
                day = day,
                dateInMillis = calendar.timeInMillis,
                tasks = tasks)
            )
        }
        return daysInMonth
    }

}
