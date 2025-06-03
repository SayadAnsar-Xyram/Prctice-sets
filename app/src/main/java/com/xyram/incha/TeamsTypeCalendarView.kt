package com.xyram.incha

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.logging.Handler

class TeamsTypeCalendarView : AppCompatActivity() {

    private lateinit var weekContainer: LinearLayout
    private lateinit var hourGrid: LinearLayout
    private lateinit var currentTimeLine: View

    private val allEvents = mutableListOf<CalendarEvent>()
    private var currentSelectedDate: Calendar = Calendar.getInstance()

    private val timeUpdateHandler = android.os.Handler()
    private val timeUpdateRunnable = object : Runnable {
        override fun run() {
            if (isSameDay(currentSelectedDate, Calendar.getInstance())) {
                updateCurrentTimeLine()
                timeUpdateHandler.postDelayed(this, 60 * 1000L) // every minute
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams_type_calendar_view)

        weekContainer = findViewById(R.id.weekContainer)
        hourGrid = findViewById(R.id.hourGrid)
        currentTimeLine = findViewById(R.id.currentTimeLine)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), 1)
        } else {
            loadEventsFromLocalCalendar()
        }

        renderWeekDays()
        updateDayView(currentSelectedDate)
    }

    private fun renderWeekDays() {
        weekContainer.removeAllViews()
        val startOfWeek = getStartOfWeek(currentSelectedDate)
        val sdf = SimpleDateFormat("EEE\nMMM d", Locale.getDefault())

        for (i in 0 until 7) {
            val dayView = layoutInflater.inflate(R.layout.item_day_label, weekContainer, false) as TextView
            val date = (startOfWeek.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, i) }
            dayView.text = sdf.format(date.time)
            dayView.setBackgroundColor(
                if (isSameDay(date, currentSelectedDate)) Color.LTGRAY else Color.TRANSPARENT
            )
            dayView.setOnClickListener {
                currentSelectedDate = date
                renderWeekDays()
                updateDayView(date)
            }
            weekContainer.addView(dayView)
        }
    }

    private fun updateDayView(date: Calendar) {
        hourGrid.removeAllViews()
        val dayIndex = getDayIndex(date)
        val eventsForDay = allEvents.filter { it.dayIndex == dayIndex }

        val now = Calendar.getInstance()
        val isToday = isSameDay(date, now)

        for (hour in 0 until 24) {
            val hourRow = layoutInflater.inflate(R.layout.item_hour_row, hourGrid, false) as LinearLayout
            val label = hourRow.findViewById<TextView>(R.id.hourLabel)
            val eventSlot = hourRow.findViewById<TextView>(R.id.eventText)

            label.text = String.format("%02d:00", hour)

            val event = eventsForDay.find { it.startHour <= hour && it.endHour > hour }
            event?.let {
                eventSlot.text = it.title
                eventSlot.visibility = View.VISIBLE
            } ?: run {
                eventSlot.visibility = View.INVISIBLE
            }

            hourGrid.addView(hourRow)
        }

        if (isToday) {
            hourGrid.post {
                updateCurrentTimeLine()
                timeUpdateHandler.removeCallbacks(timeUpdateRunnable)
                timeUpdateHandler.post(timeUpdateRunnable)
            }
        } else {
            currentTimeLine.visibility = View.GONE
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable)
        }
    }

    private fun updateCurrentTimeLine() {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)

        val hourHeight = hourGrid.getChildAt(0)?.height ?: return
        val totalMinutesToday = hour * 60 + minute
        val minutesSinceGridStart = totalMinutesToday - (0 * 60) // Grid starts at 0:00
        val offsetY = (minutesSinceGridStart.toFloat() / 60f) * hourHeight

        val maxOffset = hourGrid.childCount * hourHeight
        val clampedOffset = offsetY.coerceIn(0f, maxOffset.toFloat())

        currentTimeLine.translationY = clampedOffset
        currentTimeLine.visibility = View.VISIBLE

        Log.d("TimeLine", "Now: $hour:$minute -> offset: $offsetY px")
    }

    private fun getDayIndex(calendar: Calendar): Int {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
    }

    private fun getStartOfWeek(date: Calendar): Calendar {
        val startOfWeek = date.clone() as Calendar
        val dayOfWeek = startOfWeek.get(Calendar.DAY_OF_WEEK)
        val offset = if (dayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - dayOfWeek
        startOfWeek.add(Calendar.DAY_OF_MONTH, offset)
        return startOfWeek
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun loadEventsFromLocalCalendar() {
        val projection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )

        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val titleIdx = it.getColumnIndex(CalendarContract.Events.TITLE)
            val startIdx = it.getColumnIndex(CalendarContract.Events.DTSTART)
            val endIdx = it.getColumnIndex(CalendarContract.Events.DTEND)

            while (it.moveToNext()) {
                val title = it.getString(titleIdx) ?: "Untitled"
                val start = Calendar.getInstance().apply { timeInMillis = it.getLong(startIdx) }
                val end = Calendar.getInstance().apply { timeInMillis = it.getLong(endIdx) }
                val startHour = start.get(Calendar.HOUR_OF_DAY)
                val endHour = end.get(Calendar.HOUR_OF_DAY)
                val dayIndex = getDayIndex(start)

                allEvents.add(CalendarEvent(title, dayIndex, startHour, endHour))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeUpdateHandler.removeCallbacks(timeUpdateRunnable)
    }
}
