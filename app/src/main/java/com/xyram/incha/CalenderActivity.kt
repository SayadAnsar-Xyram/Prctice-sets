package com.xyram.incha

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.xyram.incha.databinding.ActivityCalenderBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins

class CalenderActivity : AppCompatActivity() {

/* lateinit var binding: ActivityCalenderBinding

    private val events = mutableListOf<CalendarEvent>()
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_calender)


        setupHourLabels()
        setupDayLabels()
        setupScrollSync()
        setupNavigation()
        setupCalendarGrid()
        showCurrentTimeLine()
    }

    private fun setupHourLabels() {
        binding.hourLabels.removeAllViews()
        for (i in 0 until 24) {
            val tv = TextView(this).apply {
                text = String.format("%02d:00", i)
                height = 200
                gravity = Gravity.TOP
                setPadding(8, 8, 8, 8)
            }
            binding.hourLabels.addView(tv)
        }
    }

    private fun setupDayLabels() {
        binding.dayLabels.removeAllViews()
        val formatter = SimpleDateFormat("EEE dd", Locale.getDefault())
        val tempCal = calendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0 until 7) {
            val dayView = TextView(this).apply {
                val day = formatter.format(tempCal.time)
                text = day
                setPadding(32, 16, 32, 16)
                gravity = Gravity.CENTER
                setBackgroundColor(
                    if (isToday(tempCal)) Color.LTGRAY else Color.TRANSPARENT
                )
            }
            binding.dayLabels.addView(dayView)
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun isToday(cal: Calendar): Boolean {
        val now = Calendar.getInstance()
        return now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
    }

    private fun setupNavigation() {
        findViewById<ImageButton>(R.id.btnPrevious).setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            reloadCalendar()
        }

        findViewById<ImageButton>(R.id.btnNext).setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            reloadCalendar()
        }
    }

    private fun reloadCalendar() {
        setupDayLabels()
        renderEvents()
    }

    private fun setupCalendarGrid() {
        binding.calendarGrid.removeAllViews()
        for (i in 0 until 24 * 7) {
            val cell = View(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 200
                    height = 200
                    setMargins(1, 1, 1, 1)
                }
                setBackgroundColor(Color.WHITE)
                setOnClickListener { v ->
                    val day = i % 7
                    val hour = i / 7
                    showAddEventDialog(day, hour)
                }
            }
            binding.calendarGrid.addView(cell)
        }
    }

    private fun showAddEventDialog(day: Int, hour: Int) {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Add Event")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val title = editText.text.toString()
                events.add(CalendarEvent(title = title, dayIndex = day, startHour = hour, endHour = hour + 1))
                renderEvents()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun renderEvents() {
        val container = binding.calendarGrid.parent as ViewGroup
        container.children.filter { it.tag == "event" }.forEach { container.removeView(it) }

        for (event in events) {
            val view = TextView(this).apply {
                text = event.title
                tag = "event"
                setBackgroundColor(Color.CYAN)
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(200, 200 * (event.endHour - event.startHour)).apply {
                    leftMargin = event.dayIndex * 200
                    topMargin = event.startHour * 200
                }
                setOnClickListener {
                    showEditEventDialog(event)
                }
            }
            (binding.calendarGrid.parent as ViewGroup).addView(view)
        }
    }

    private fun showEditEventDialog(event: CalendarEvent) {
        val editText = EditText(this)
        editText.setText(event.title)
        AlertDialog.Builder(this)
            .setTitle("Edit Event")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                event.title = editText.text.toString()
                renderEvents()
            }
            .setNegativeButton("Delete") { _, _ ->
                events.remove(event)
                renderEvents()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun setupScrollSync() {
        val hourScroll = findViewById<ScrollView>(R.id.hourScrollView)
        val calendarScroll = findViewById<ScrollView>(R.id.calendarScrollView)
        val dayScroll = findViewById<HorizontalScrollView>(R.id.dayScrollView)
        val horizontalScroll = findViewById<HorizontalScrollView>(R.id.horizontalScrollView)

        calendarScroll.viewTreeObserver.addOnScrollChangedListener {
            hourScroll.scrollTo(0, calendarScroll.scrollY)
        }

        horizontalScroll.viewTreeObserver.addOnScrollChangedListener {
            dayScroll.scrollTo(horizontalScroll.scrollX, 0)
        }
    }

    private fun showCurrentTimeLine() {
        val now = Calendar.getInstance()
        if (now.get(Calendar.DAY_OF_WEEK) !in Calendar.MONDAY..Calendar.SUNDAY) return

        binding.redLine.visibility = View.VISIBLE
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)

        binding.redLine.post {
            val y = hour * 200 + (minute * 200) / 60
            binding.redLine.translationY = y.toFloat()
        }
    }*/

    lateinit var binding: ActivityCalenderBinding
    private val events = mutableListOf<CalendarEvent>()
    private val calendar = Calendar.getInstance()

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_calender)

        binding.btnAddEventManually.setOnClickListener {
            showAddEventFullDialog()
        }
        // Request permissions, or load events if already granted
        if (hasCalendarPermissions()) {
            initCalendar()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddEventFullDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val tvDate = dialogView.findViewById<TextView>(R.id.tvDate)
        val tvStartTime = dialogView.findViewById<TextView>(R.id.tvStartTime)
        val tvEndTime = dialogView.findViewById<TextView>(R.id.tvEndTime)

        val selectedDate = Calendar.getInstance()
        val startTime = Calendar.getInstance()
        val endTime = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) }

        // Date Picker
        tvDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate.set(year, month, day)
                tvDate.text = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(selectedDate.time)
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Start Time Picker
        tvStartTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                startTime.set(Calendar.HOUR_OF_DAY, hour)
                startTime.set(Calendar.MINUTE, minute)
                tvStartTime.text = String.format("%02d:%02d", hour, minute)
            }, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true).show()
        }

        // End Time Picker
        tvEndTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                endTime.set(Calendar.HOUR_OF_DAY, hour)
                endTime.set(Calendar.MINUTE, minute)
                tvEndTime.text = String.format("%02d:%02d", hour, minute)
            }, endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE), true).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Add Event")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = etTitle.text.toString()
                if (title.isBlank()) return@setPositiveButton

                val startHour = startTime.get(Calendar.HOUR_OF_DAY)
                val endHour = endTime.get(Calendar.HOUR_OF_DAY)
                val dayIndex = (selectedDate.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Make Monday=0

                events.add(CalendarEvent(title, dayIndex, startHour, endHour))
                renderEvents()
                addEventToDeviceCalendar(title, dayIndex, startHour, endHour)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun hasCalendarPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
    }

    private fun initCalendar() {
        setupHourLabels()
        setupDayLabels()
        setupScrollSync()
        setupNavigation()
        setupCalendarGrid()
        fetchDeviceCalendarEventsForWeek()
        showCurrentTimeLine()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initCalendar()
            } else {
                Toast.makeText(this, "Calendar permissions are required to load events.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupHourLabels() {
        binding.hourLabels.removeAllViews()
        for (i in 0 until 24) {
            val tv = TextView(this).apply {
                text = String.format("%02d:00", i)
                height = 200
                width= 200
                gravity = Gravity.TOP
                setPadding(8, 8, 8, 8)
            }
            binding.hourLabels.addView(tv)
        }
    }

    private fun setupDayLabels() {
        binding.dayLabels.removeAllViews()
        val formatter = SimpleDateFormat("EEE dd", Locale.getDefault())
        val tempCal = calendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0 until 7) {
            val dayView = TextView(this).apply {
                val day = formatter.format(tempCal.time)
                text = day
                height = 200
                width=200
                setPadding(32, 16, 32, 16)
                gravity = Gravity.CENTER
                setBackgroundColor(
                    if (isToday(tempCal)) Color.LTGRAY else Color.TRANSPARENT
                )
            }
            binding.dayLabels.addView(dayView)
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun isToday(cal: Calendar): Boolean {
        val now = Calendar.getInstance()
        return now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
    }

    private fun setupNavigation() {
        findViewById<ImageButton>(R.id.btnPrevious).setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            reloadCalendar()
        }

        findViewById<ImageButton>(R.id.btnNext).setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            reloadCalendar()
        }
    }

    private fun reloadCalendar() {
        setupDayLabels()
        fetchDeviceCalendarEventsForWeek()
    }

    private fun setupCalendarGrid() {
        binding.calendarGrid.removeAllViews()
        binding.calendarGrid.columnCount = 7
        binding.calendarGrid.rowCount = 24

        for (hour in 0 until 24) {
            for (day in 0 until 7) {
                val cell = View(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 200
                        height = 200
                        columnSpec = GridLayout.spec(day)
                        rowSpec = GridLayout.spec(hour)
                        setMargins(1, 1, 1, 1)
                    }
                    setBackgroundColor(Color.WHITE)
                    setOnClickListener {
                        showAddEventDialog(day, hour)
                    }
                }
                binding.calendarGrid.addView(cell)
            }
        }
    }

    private fun showAddEventDialog(day: Int, hour: Int) {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Add Event")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val title = editText.text.toString()
                events.add(CalendarEvent(title = title, dayIndex = day, startHour = hour, endHour = hour + 1))
                renderEvents()
                addEventToDeviceCalendar(title, day, hour, hour + 1)
                reloadCalendar()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun renderEvents() {
        // Remove previous event views
        val container = binding.calendarGrid.parent as ViewGroup
        container.children.filter { it.tag == "event" }.forEach { container.removeView(it) }

        for (event in events) {
            val eventView = TextView(this).apply {
                text = event.title
                tag = "event"
                setBackgroundColor(Color.CYAN)
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(200, 200 * (event.endHour - event.startHour)).apply {
                    leftMargin = event.dayIndex * 200
                    topMargin = event.startHour * 200
                }
                setOnClickListener {
                    showEditEventDialog(event)
                }
            }
            container.addView(eventView)
        }
    }

    private fun showEditEventDialog(event: CalendarEvent) {
        val editText = EditText(this)
        editText.setText(event.title)
        AlertDialog.Builder(this)
            .setTitle("Edit Event")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                event.title = editText.text.toString()
                renderEvents()
            }
            .setNegativeButton("Delete") { _, _ ->
                events.remove(event)
                renderEvents()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun setupScrollSync() {
        binding.calendarScrollView.viewTreeObserver.addOnScrollChangedListener {
            binding.hourScrollView.scrollTo(0, binding.calendarScrollView.scrollY)
        }

        binding.horizontalScrollView.viewTreeObserver.addOnScrollChangedListener {
            binding.dayScrollView.scrollTo(binding.horizontalScrollView.scrollX, 0)
        }
    }

    private fun showCurrentTimeLine() {
        val now = Calendar.getInstance()
        if (now.get(Calendar.DAY_OF_WEEK) !in Calendar.MONDAY..Calendar.SUNDAY) return

        binding.redLine.visibility = View.VISIBLE
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)

        binding.redLine.post {
            val y = hour * 200 + (minute * 200) / 60
            binding.redLine.translationY = y.toFloat()
        }
    }

    private fun addEventToDeviceCalendar(title: String, dayIndex: Int, startHour: Int, endHour: Int) {
        val startCal = Calendar.getInstance()
        startCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        startCal.add(Calendar.DAY_OF_WEEK, dayIndex)
        startCal.set(Calendar.HOUR_OF_DAY, startHour)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.SECOND, 0)
        startCal.set(Calendar.MILLISECOND, 0)

        val endCal = startCal.clone() as Calendar
        endCal.set(Calendar.HOUR_OF_DAY, endHour)

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startCal.timeInMillis)
            put(CalendarContract.Events.DTEND, endCal.timeInMillis)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.CALENDAR_ID, getCalendarId())
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }

    private fun getCalendarId(): Long {
        val projection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
        val uri = CalendarContract.Calendars.CONTENT_URI
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                val name = it.getString(1)
                if (name.contains("google", ignoreCase = true)) {
                    return id
                }
            }
        }
        return 1 // fallback id
    }

    private fun fetchDeviceCalendarEventsForWeek() {
        val startOfWeek = Calendar.getInstance().apply {
            time = calendar.time
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endOfWeek = (startOfWeek.clone() as Calendar).apply {
            add(Calendar.DAY_OF_WEEK, 7)
        }

        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startOfWeek.timeInMillis)
        ContentUris.appendId(builder, endOfWeek.timeInMillis)

        val cursor = contentResolver.query(
            builder.build(),
            arrayOf(
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.DTSTART,
                CalendarContract.Instances.DTEND
            ),
            null,
            null,
            CalendarContract.Instances.DTSTART + " ASC"
        )

        val list = mutableListOf<CalendarEvent>()

        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(0)
                val startMillis = it.getLong(1)
                val endMillis = it.getLong(2)

                val startCal = Calendar.getInstance().apply { timeInMillis = startMillis }
                val endCal = Calendar.getInstance().apply { timeInMillis = endMillis }

                val dayIndex = ((startCal.get(Calendar.DAY_OF_WEEK) + 5) % 7)
                val startHour = startCal.get(Calendar.HOUR_OF_DAY)
                val endHour = maxOf(startHour + 1, endCal.get(Calendar.HOUR_OF_DAY))

                list.add(CalendarEvent(title, dayIndex, startHour, endHour))
            }
        }

        events.clear()
        events.addAll(list)
        renderEvents()
    }
}

data class CalendarEvent(
    var title: String,
    val dayIndex: Int,  // 0 = Monday, 6 = Sunday (based on your code)
    val startHour: Int, // 0 to 23
    val endHour: Int    // exclusive end hour
)


