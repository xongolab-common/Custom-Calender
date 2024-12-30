package com.example.customcalender

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.customcalender.databinding.RawCalendar1Binding


class CalendarAdapter(private var arrayList: MutableList<CalenderModel>) :
    RecyclerView.Adapter<CalendarAdapter.Holder>() {

    private var mContext: Context? = null
    var onDateClick: ((position: Int) -> Unit)? = null


    inner class Holder(val binding: RawCalendar1Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mContext = parent.context
        val binding = RawCalendar1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = arrayList[position]
        holder.binding.apply {
            tvDayName.text = item.day
            tvDate.text = item.date

            rlCalendarView.background =
                mContext?.let { ContextCompat.getDrawable(it,if(item.isClicked) R.drawable.ic_calendar_bg else android.R.color.white) }

            rlCalendarView.setOnClickListener {
                onDateClick?.invoke(position)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(calendarList: ArrayList<CalenderModel>) {
        arrayList.clear()
        arrayList.addAll(calendarList)
        this.notifyDataSetChanged()
    }
}




data class CalenderModel(
    var day: String,
    var date: String,
    var formattedDate: String,
    var isClicked: Boolean,
)