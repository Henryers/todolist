package com.example.todolist.tab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.todolist.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * tab2：日历页
 * 顶部能根据滑动的内容显示年月
 * 中间能滑动，显示指定月的具体日期以及对应的任务列表
 * 初始化时显示当前月份下各个日期的任务情况
 */
class CalendarFragment : Fragment() {

    private lateinit var monthTitle: TextView
    private lateinit var monthViewPager: ViewPager2
    private lateinit var monthPagerAdapter: MonthPagerAdapter
    private val initialMonth = Calendar.getInstance()

    // 定义基准位置，用于实现无限滑动效果
    private val basePosition = Int.MAX_VALUE / 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        monthTitle = view.findViewById(R.id.monthTitle)
        monthViewPager = view.findViewById(R.id.monthViewPager)

        // 初始化 ViewPager2 适配器
        monthPagerAdapter = MonthPagerAdapter(this, initialMonth)
        monthViewPager.adapter = monthPagerAdapter

        // 设置 ViewPager 起始位置
        monthViewPager.setCurrentItem(basePosition, false)

        // 手动调用 `updateMonthTitle`，以显示初始月份
        updateMonthTitle(initialMonth)

        // 设置滑动监听，更新月份标题
        monthViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val calendar = (initialMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, position - basePosition) // 动态计算当前月份和年份
                }
                updateMonthTitle(calendar)
            }
        })

        return view
    }

    /**
     * 更新年月的title
     * 将传进来的calendar对象，进行格式化处理，显示 July 2024 这种格式
     */
    private fun updateMonthTitle(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTitle.text = dateFormat.format(calendar.time)
    }

}
