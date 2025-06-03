package com.xyram.incha.customCalendar


import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class WeeklyCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private val weekContainer = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(weekContainer)
        buildWeeks()
    }

    private fun buildWeeks(startDate: LocalDate = LocalDate.now().with(DayOfWeek.SUNDAY), weeks: Int = 1) {
        var currentStartDate = startDate
        repeat(weeks) {
            val weekHeader = generateWeekHeader(currentStartDate)
            weekContainer.addView(weekHeader)

            for (i in 0..6) {
                val dayRow = generateDayRow(currentStartDate.plusDays(i.toLong()))
                weekContainer.addView(dayRow)
            }
            currentStartDate = currentStartDate.plusWeeks(1)
        }
    }

    private fun generateWeekHeader(startDate: LocalDate): View {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.LTGRAY)
        }
        for (i in 0..6) {
            val day = startDate.plusDays(i.toLong())
            val tv = TextView(context).apply {
                text = day.dayOfWeek.name.take(3) + "\n" + day.dayOfMonth
                gravity = Gravity.CENTER
                setPadding(16, 16, 16, 16)
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            }
            layout.addView(tv)
        }
        return layout
    }

    private fun generateDayRow(day: LocalDate): View {
        val scroll = HorizontalScrollView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, WRAP_CONTENT)
        }

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        for (hour in 0..23) {
            val slot = TextView(context).apply {
                text = String.format("%02d:00", hour)
                setPadding(32, 32, 32, 32)
                setBackgroundColor(Color.parseColor("#EEEEEE"))
                setTextColor(Color.DKGRAY)
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            }
            row.addView(slot)
        }

        scroll.addView(row)
        return scroll
    }
}



data class CalendarEvent(
    val title: String,
    val dayOfWeek: DayOfWeek,
    val startHour: Int,  // e.g., 9 for 9 AM
    val endHour: Int     // e.g., 10 for 10 AM
)