package com.xyram.incha.customCalendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class TeamsCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val verticalScrollView = ScrollView(context)
    private val contentLayout = LinearLayout(context)
    private val hourLabelsLayout = LinearLayout(context)
    private val eventGrid = GridLayout(context)

    private var events: List<CalendarEvent> = emptyList() // <-- Hold event data

    init {
        setupLayout()
    }

    private fun setupLayout() {
        contentLayout.orientation = LinearLayout.HORIZONTAL

        // Hour Labels
        hourLabelsLayout.orientation = LinearLayout.VERTICAL
        for (i in 0..23) {
            val hourLabel = TextView(context).apply {
                text = String.format("%02d:00", i)
                setPadding(16, 32, 16, 32)
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            hourLabelsLayout.addView(hourLabel)
        }

        // Event Grid
        eventGrid.rowCount = 24
        eventGrid.columnCount = 7
        eventGrid.layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        )

        // Fill grid with empty slots initially
        for (i in 0 until 24 * 7) {
            val slot = FrameLayout(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 200
                    height = 150
                    rowSpec = GridLayout.spec(i / 7)
                    columnSpec = GridLayout.spec(i % 7)
                    setMargins(4, 4, 4, 4)
                }
                setBackgroundColor(Color.LTGRAY)
            }
            eventGrid.addView(slot)
        }

        contentLayout.addView(hourLabelsLayout)
        contentLayout.addView(eventGrid)

        verticalScrollView.addView(contentLayout)
        addView(verticalScrollView)
    }

    // 🔁 Inject new events into the calendar
    fun setEvents(eventList: List<CalendarEvent>) {
        this.events = eventList
        renderEvents()
    }

    // 🖼️ Display each event on the grid
    private fun renderEvents() {
        for (event in events) {
            val rowIndex = event.startHour
            val colIndex = event.dayOfWeek.ordinal // Sunday = 0, Monday = 1, ...

            val eventView = TextView(context).apply {
                text = event.title
                setBackgroundColor(Color.BLUE)
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 200
                    height = (150 * (event.endHour - event.startHour)).coerceAtLeast(150)
                    rowSpec = GridLayout.spec(rowIndex)
                    columnSpec = GridLayout.spec(colIndex)
                    setMargins(4, 4, 4, 4)
                }
            }

            eventGrid.addView(eventView)
        }
    }
}