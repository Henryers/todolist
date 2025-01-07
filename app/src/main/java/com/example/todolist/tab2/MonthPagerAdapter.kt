package com.example.todolist.tab2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.Calendar

/**
 * 月份滑动的适配器
 * 能根据当前位置，计算对应的月份和日期，再传给MonthFragment去创建实例（相当于预处理了）
 */
class MonthPagerAdapter(fragment: Fragment, private val initialMonth: Calendar)
    : FragmentStateAdapter(fragment) {

    private val basePosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE // 无限滑动

    override fun createFragment(position: Int): Fragment {
        // 以初始页面的（位置，月份）为基准，在左右滑动的过程中，根据位置动态计算出对应的月份和日期
        val calendar = (initialMonth.clone() as Calendar).apply {
            add(Calendar.MONTH, position - basePosition) // 动态计算每个页面的月份
        }
        // 将计算得到的日期传递给 MonthFragment（当月的1号）
        return MonthFragment.newInstance(calendar.time)
    }
}
