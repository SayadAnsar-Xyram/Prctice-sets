package com.xyram.incha.testsample

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xyram.incha.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class SampleCalendarActivity : AppCompatActivity() {

    private lateinit var hourGrid: LinearLayout
    private lateinit var currentTimeLine: View
    private lateinit var dateSelector: LinearLayout
    private lateinit var timer: Timer
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_calendar)

        hourGrid = findViewById(R.id.hourGrid)
        currentTimeLine = findViewById(R.id.currentTimeLine)
        dateSelector = findViewById(R.id.dateSelector)

        createDateSelector()
        populateHourGrid()
        startCurrentTimeLineUpdater()

        if (checkSelfPermission(android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CALENDAR), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hourGrid.post { loadCalendarEvents() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun createDateSelector() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        for (i in 0 until 7) {
            val date = calendar.clone() as Calendar
            val button = Button(this).apply {
                val sdf = SimpleDateFormat("EEE\ndd", Locale.getDefault())
                text = sdf.format(date.time)
                setPadding(16, 16, 16, 16)
                setAllCaps(false)
                setBackgroundResource(R.drawable.date_button_bg)
                setTextColor(Color.BLACK)
                tag = date

                setOnClickListener {
                    selectedDate = date
                    highlightSelectedDate(this)
                    populateHourGrid()
                }
            }
            dateSelector.addView(button)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Auto-select today
        (dateSelector.getChildAt(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) as? Button)?.performClick()
    }

    private fun highlightSelectedDate(selectedButton: Button) {
        for (i in 0 until dateSelector.childCount) {
            val button = dateSelector.getChildAt(i) as Button
            button.setBackgroundResource(R.drawable.date_button_bg)
            button.setTextColor(Color.BLACK)
        }
        selectedButton.setBackgroundResource(R.drawable.date_button_selected_bg)
        selectedButton.setTextColor(Color.WHITE)
    }

    private fun populateHourGrid() {
        hourGrid.removeAllViews()
        for (i in 0..23) {
            val hourView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(60)
                )
                text = String.format("%02d:00", i)
                setPadding(16, 16, 16, 16)
                setBackgroundColor(Color.parseColor(if (i % 2 == 0) "#EEEEEE" else "#FFFFFF"))
                setTextColor(Color.BLACK)
                textSize = 16f
            }
            hourGrid.addView(hourView)
        }

        hourGrid.post {
            updateCurrentTimeLine()
            if (checkSelfPermission(android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                loadCalendarEvents()
            }
        }
    }

    private fun updateCurrentTimeLine() {
        val today = Calendar.getInstance()
        val sameDay = today.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)

        if (!sameDay) {
            currentTimeLine.visibility = View.GONE
            return
        }

        hourGrid.post {
            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)
            val minute = now.get(Calendar.MINUTE)

            if (hour >= hourGrid.childCount) {
                currentTimeLine.visibility = View.GONE
                return@post
            }

            val hourView = hourGrid.getChildAt(hour)
            val hourTop = hourView?.top ?: 0
            val hourHeight = hourView?.height ?: dpToPx(60)
            val minuteOffset = (minute / 60f) * hourHeight

            val lineY = hourTop + minuteOffset
            currentTimeLine.translationY = lineY
            currentTimeLine.visibility = View.VISIBLE
        }
    }

    private fun startCurrentTimeLineUpdater() {
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    updateCurrentTimeLine()
                }
            }
        }, 0, 60_000)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun loadCalendarEvents() {
        val events = mutableListOf<Pair<Int, Int>>() // Pair<startHour, endHour>
        val contentResolver = contentResolver

        val startMillis: Long = selectedDate.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        val endMillis: Long = selectedDate.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis

        val uri = android.provider.CalendarContract.Events.CONTENT_URI
        val selection = "(${android.provider.CalendarContract.Events.DTSTART} >= ?) AND (${android.provider.CalendarContract.Events.DTEND} <= ?)"
        val selectionArgs = arrayOf(startMillis.toString(), endMillis.toString())

        val cursor = contentResolver.query(
            uri,
            arrayOf(
                android.provider.CalendarContract.Events.TITLE,
                android.provider.CalendarContract.Events.DTSTART,
                android.provider.CalendarContract.Events.DTEND
            ),
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val start = it.getLong(1)
                val end = it.getLong(2)

                val calStart = Calendar.getInstance().apply { timeInMillis = start }
                val calEnd = Calendar.getInstance().apply { timeInMillis = end }

                val startHour = calStart.get(Calendar.HOUR_OF_DAY)
                val endHour = calEnd.get(Calendar.HOUR_OF_DAY)

                events.add(Pair(startHour, endHour))
            }
        }

        showEvents(events)
    }

    private fun showEvents(events: List<Pair<Int, Int>>) {
        for ((startHour, endHour) in events) {
            for (i in startHour until endHour) {
                val hourView = hourGrid.getChildAt(i) as? TextView ?: continue

                hourView.setBackgroundColor(Color.parseColor("#FFEB3B")) // Yellow highlight
                hourView.setTextColor(Color.BLACK)
            }
        }
    }
}
