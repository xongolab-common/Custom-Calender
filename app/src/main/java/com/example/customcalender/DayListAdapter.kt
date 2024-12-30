package com.example.customcalender

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.customcalender.databinding.RawDayListBinding


@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
class DayListAdapter(var context: Context, var clickListener: View.OnClickListener) : RecyclerView.Adapter<DayListAdapter.Holder>() {


    var objList: ArrayList<OrderModel> = ArrayList()

    inner class Holder(val binding: RawDayListBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RawDayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return objList.size
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = objList[position]
        holder.binding.apply {

            holder.binding.tvDate.text = item.date

            holder.binding.tvDay.text = item.day

            llDateView.background = if (item.isSelect) ContextCompat.getDrawable(context, R.drawable.dr_primary_bg_30) else ContextCompat.getDrawable(context, R.drawable.dr_gray_border_30)
            tvDate.background = if (item.isSelect) ContextCompat.getDrawable(context, R.drawable.dr_white_bg_30) else null
            tvDay.setTextColor(if (item.isSelect) ContextCompat.getColor(context, R.color.colorWhite) else ContextCompat.getColor(context, R.color.colorGray))
            tvDate.setTextColor(if (item.isSelect) ContextCompat.getColor(context, R.color.colorPrimary3) else ContextCompat.getColor(context, R.color.colorGray))


            val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
            if (position == 0) {
                layoutParams.marginStart = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
            } else {
                layoutParams.marginStart = 0
            }
            holder.itemView.layoutParams = layoutParams


            llDateView.tag = position
            llDateView.setOnClickListener(clickListener)

        }
    }

    fun addData(mObj: List<OrderModel>) {
        objList = ArrayList()
        objList.addAll(mObj)
        this.notifyDataSetChanged()
    }
}

class OrderModel() {
    var date: String = ""
    var day: String = ""
    var isSelect: Boolean = false


}