package com.example.customcalender

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.example.customcalender.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private var calendarAdapter = CalendarAdapter(mutableListOf())
    private lateinit var centerSmoothScroll: LinearSmoothScroller
    private var calendarList = ArrayList<CalenderModel>()
    private var selectedCalDate: String = ""
    private var selectedDate: String = ""
    private var selectedPosition = 0

    val cal = Calendar.getInstance()
    val tz = cal.timeZone

    // TODO :: Calender 2

    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
    val dateFormatMonth = SimpleDateFormat("MMM", Locale.getDefault())
    val dateFormatDayOfWeek = SimpleDateFormat("EEE", Locale.getDefault())
    val dateFormatDayOfMonth = SimpleDateFormat("dd", Locale.getDefault())

    var checkInDate: String = ""
    var checkOutDate: String = ""

    // TODO :: Calender 3

    private lateinit var dayListAdapter: DayListAdapter
    var monthAdapter: ArrayAdapter<String>? = null
    var yearAdapter: ArrayAdapter<String>? = null
    private var lastPos: Int = -1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView(){

        // TODO :: Calender 1
        binding.btnPrevious.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.btnNext.isEnabled = false

        calendarAdapter = CalendarAdapter(mutableListOf())
        binding.rvCalendar1.adapter = calendarAdapter

        calenderDate()

        // TODO :: Calender 2

        binding.btnBookForToNight.setOnClickListener(this)

        val startMonth = Calendar.getInstance()
        val endMonth = startMonth.clone() as Calendar
        endMonth.add(Calendar.YEAR, 5)
        binding.calendarDateRangePicker.setVisibleMonthRange(startMonth, endMonth)
        binding.calendarDateRangePicker.setSelectableDateRange(startMonth, endMonth)
        binding.calendarDateRangePicker.setCurrentMonth(startMonth)

        val typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(R.font.helvetica_neue_regular)
        } else {
            Typeface.createFromAsset(assets, "font/helvetica_neue_regular.otf")
        }

        binding.calendarDateRangePicker.setFonts(typeface)

        binding.calendarDateRangePicker.setCalendarListener(calendarListener)

        // TODO :: Calender 3

        binding.rlMonthView.setOnClickListener(this)
        binding.rlYearView.setOnClickListener(this)

        setMonthListAdapter()
        setYearListAdapter()
    }

    override fun onClick(view: View) {
        when (view.id) {
            // TODO :: Calender 1
            R.id.btnPrevious -> {
                selectedPosition--

                if (selectedPosition <= 0) {
                    binding.btnNext.isEnabled = true
                }

                setCalenderData()

            }

            R.id.btnNext -> {
                selectedPosition++
                if (selectedPosition > 0) {
                    selectedPosition = 0
                    binding.btnNext.isEnabled = false
                }
                setCalenderData()
            }

            // TODO :: Calender 2

            R.id.btnBookForToNight -> {
                if (checkInDate.isEmpty() && checkOutDate.isEmpty()) {
                    Toast.makeText(this, "Please select check in & check out date", Toast.LENGTH_SHORT).show()
                } else if (checkInDate.isEmpty()) {
                    Toast.makeText(this, "Please select check in date", Toast.LENGTH_SHORT).show()
                } else if (checkOutDate.isEmpty()) {
                    Toast.makeText(this, "Please select check out date", Toast.LENGTH_SHORT).show()
                } else {
                    // Start New activity
                }
            }

            // TODO :: Calender 3

            R.id.rlMonthView -> {
                binding.spMonth.performClick()
            }

            R.id.rlYearView -> {
                binding.spYear.performClick()
            }

            R.id.llDateView -> {
                lastPos = Integer.parseInt(view.tag.toString())
                for (i in 0 until dayListAdapter.objList.size) {
                    dayListAdapter.objList[i].isSelect = false
                }
                dayListAdapter.objList[lastPos].isSelect = true

                dayListAdapter.notifyDataSetChanged()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun calenderDate() {
        centerSmoothScroll = object : CenterSmoothScroller(this) {}

        val todayCalendar = Calendar.getInstance()
        val todayDateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(todayCalendar.time)

        selectedCalDate = todayCalendar.get(Calendar.DATE).toString().padStart(2, '0')
        selectedDate = todayDateFormatted

        calendarAdapter.onDateClick = { position ->
            val selectedModel = calendarList[position]
            selectedCalDate = selectedModel.date

            val specificDate = try {
                LocalDate.parse(selectedModel.formattedDate.substring(0, 10))
            } catch (e: Exception) {
                null
            }

            if (specificDate != null) {
                val currentDate = LocalDate.now()
                val previousDate = currentDate.minusDays(1)

              /*  binding.btnLogMedicines.visibility =
                    if (specificDate == currentDate /*|| specificDate == previousDate*/) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }*/
            }

            calendarList.forEachIndexed { index, model ->
                model.isClicked = index == position
            }

            calendarAdapter.notifyDataSetChanged()
            selectedDate = selectedModel.formattedDate

            Handler(Looper.getMainLooper()).postDelayed({
                centerSmoothScroll.targetPosition = position
                binding.rvCalendar1.layoutManager?.startSmoothScroll(centerSmoothScroll)
               // getLogMedicineListApi(selectedDate)
            }, 200)
        }

        setCalenderData()
     //   getLogMedicineListApi(selectedDate)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setCalenderData() {
        calendarList.clear()

        println("selectedPosition...$selectedPosition")
        // Start with the current date
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, selectedPosition) // Adjust month based on selected position
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month

        // Get the month and year for display
        val currentMonthYear = SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(calendar.time)
        binding.lblMonthYear.text = currentMonthYear

        // Get the number of days in the current month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Populate the calendar list with days of the month
        for (i in 1..daysInMonth) {
            val date = calendar.time
            val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(date)

            // Check if the current day matches the date and set it as clicked
            val isClicked = if (selectedPosition == 0) {
                val today = Calendar.getInstance()
                val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(today.time)
                formattedDate.startsWith(todayDate) // Compare year-month-day for today
            } else {
                false
            }

            calendarList.add(
                CalenderModel(
                    day = SimpleDateFormat("EEE", Locale.ENGLISH).format(date), // Day of the week (e.g., Mon)
                    date = SimpleDateFormat("dd", Locale.ENGLISH).format(date), // Day of the month (e.g., 01)
                    formattedDate = formattedDate,
                    isClicked = isClicked
                )
            )

            calendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
        }

        // Find the position of the selected day (today) if available
        if (selectedPosition == 0) {
            val today = Calendar.getInstance()
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(today.time)
            val currentDayIndex = calendarList.indexOfFirst {
                it.formattedDate.startsWith(todayDate)
            }

            if (currentDayIndex != -1) {
                // Set selectedDate to today's date
                selectedDate = calendarList[currentDayIndex].formattedDate
                selectedCalDate = calendarList[currentDayIndex].date
                calendarList[currentDayIndex].isClicked = true // Set today's date as clicked

                Handler(Looper.getMainLooper()).postDelayed({
                    centerSmoothScroll.targetPosition = currentDayIndex // Smooth scroll to the current date
                    binding.rvCalendar1.layoutManager?.startSmoothScroll(centerSmoothScroll)

                }, 1000)
            }
        }

        // Update RecyclerView adapter
        calendarAdapter.addData(calendarList)
        calendarAdapter.notifyDataSetChanged()

        // Smooth scroll to the current day if required
        /* if (::centerSmoothScroll.isInitialized && centerSmoothScroll.targetPosition in calendarList.indices) {
             binding.llCalendar.rvCalendar1.layoutManager?.startSmoothScroll(centerSmoothScroll)
         }*/
    }


    open inner class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {

        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
        }
    }


    // TODO :: Calender 2

    private val calendarListener: CalendarListener = object : CalendarListener {
        @SuppressLint("SetTextI18n")
        override fun onFirstDateSelected(startDate: Calendar) {
            checkInDate = startDate.time.toString()
            checkOutDate = ""

            val newStartDate: Date = inputFormat.parse(startDate.time.toString()) ?: Date()

            binding.tvCheckInDate.text = "Check in date : " + dateFormatDayOfMonth.format(newStartDate)
            binding.tvCheckInWeek.text = "Check in week : " + dateFormatDayOfWeek.format(newStartDate)
            binding.tvCheckInMonth.text = "Check in month : " + dateFormatMonth.format(newStartDate)

            binding.tvCheckOutDate.text = ""
            binding.tvCheckOutWeek.text = ""
            binding.tvCheckOutMonth.text = ""

            binding.btnBookForToNight.text = "Book for tonight"

        }

        @SuppressLint("SetTextI18n")
        override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
            checkInDate = startDate.time.toString()
            checkOutDate = endDate.time.toString()

            val newStartDate: Date = inputFormat.parse(startDate.time.toString()) ?: Date()

            binding.tvCheckInDate.text = "Check in date : " + dateFormatDayOfMonth.format(newStartDate)
            binding.tvCheckInWeek.text = "Check in week : " + dateFormatDayOfWeek.format(newStartDate)
            binding.tvCheckInMonth.text = "Check in month : " + dateFormatMonth.format(newStartDate)

            val newEndDate: Date = inputFormat.parse(endDate.time.toString()) ?: Date()

            binding.tvCheckOutDate.text = "Check out date : " + dateFormatDayOfMonth.format(newEndDate)
            binding.tvCheckOutWeek.text = "Check out week : " + dateFormatDayOfWeek.format(newEndDate)
            binding.tvCheckOutMonth.text = "Check out month : " + dateFormatMonth.format(newEndDate)

            // Calculate the number of days selected
            val differenceInMillis = endDate.timeInMillis - startDate.timeInMillis
            val daysSelected = (differenceInMillis / (1000 * 60 * 60 * 24)).toInt()

            binding.btnBookForToNight.text = "Continue with $daysSelected nights"
            binding.tvNoOfNights.text = "$daysSelected Night"
        }
    }


    // TODO :: Calender 3

    private val monthsList = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private fun setMonthListAdapter() {
        monthAdapter = object : ArrayAdapter<String>(
            this,
            R.layout.custom_spinner_list_view, // Use your custom layout
            monthsList
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                return view
            }
        }

        monthAdapter!!.setDropDownViewResource(R.layout.custom_spinner_list_view)
        binding.spMonth.adapter = monthAdapter

        binding.spMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.tvMonth.text = this@MainActivity.monthsList[position]
                loadDaysForMonth(binding.spYear.selectedItem.toString().toInt(), position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private val yearList = (2023..Calendar.getInstance().get(Calendar.YEAR)).toList().reversed().map { it.toString() }.toTypedArray()

    private fun setYearListAdapter() {
        yearAdapter = object : ArrayAdapter<String>(
            this,
            R.layout.custom_spinner_list_view,
            yearList
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                return super.getView(position, convertView, parent)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                return super.getDropDownView(position, convertView, parent)
            }
        }

        yearAdapter!!.setDropDownViewResource(R.layout.custom_spinner_list_view)
        binding.spYear.adapter = yearAdapter

        binding.spYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.tvYear.text = yearList[position]
                loadDaysForMonth(yearList[position].toInt(), binding.spMonth.selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadDaysForMonth(year: Int, month: Int) {
        val daysInMonth = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)

        val daysList = (1..daysInMonth).map { _day ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, _day)
            }
            val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val dayOfWeek = dayOfWeekFormat.format(calendar.time)

            OrderModel().apply {
                day = dayOfWeek
                date = _day.toString()
            }
        }

        dayListAdapter = DayListAdapter(this, this)
        binding.rvDayList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvDayList.adapter = dayListAdapter

        dayListAdapter.addData(daysList)
    }


}
