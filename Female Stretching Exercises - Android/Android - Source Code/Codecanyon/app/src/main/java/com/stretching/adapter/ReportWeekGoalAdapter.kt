package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stretching.R
import com.stretching.databinding.ItemReportWeekDayBinding
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import com.stretching.db.DataHelper
import java.util.*
import kotlin.collections.ArrayList


class ReportWeekGoalAdapter(internal var context: Context) :
    RecyclerView.Adapter<ReportWeekGoalAdapter.MyViewHolder>(){

    private val data = Utils.getCurrentWeekByFirstDay(context)
    val dbHelper = DataHelper(context)
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): String {
        return data[pos]
    }

    fun addAll(mData: ArrayList<String>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: String) {

        try {
            this.data.add(item)
        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemReportWeekDayBinding>(
            inflater,
            R.layout.item_report_week_day, parent, false)
        rowSideMenuBinding.root.layoutParams.width = parent.measuredWidth / 7
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = getItem(position)
        val date = Utils.parseTime(item, Constant.CapDateFormatDisplay)
        val currDate = Utils.parseTime(Date(), Constant.CapDateFormatDisplay)
        holder.rowSideMenuBinding.tvDate.text =
            Utils.parseTime(item, Constant.CapDateFormatDisplay, "d")
        holder.rowSideMenuBinding.tvDay.text =
            Utils.parseTime(item, Constant.CapDateFormatDisplay, "E")[0].toString()

        if (date == currDate) {
            holder.rowSideMenuBinding.tvDate.setTextColor(ContextCompat.getColor(context,R.color.green_text))
        } else {
            holder.rowSideMenuBinding.tvDate.setTextColor(ContextCompat.getColor(context,R.color.col_999))
        }

        when {
            dbHelper.isHistoryAvailable(Utils.parseTime(item, Constant.CapDateFormatDisplay,Constant.DATE_FORMAT)) -> {
                holder.rowSideMenuBinding.imgCompleted.setImageResource(R.drawable.ic_goal_complete)
            }
            Calendar.getInstance(Locale.ENGLISH).time.after(date) -> {
                holder.rowSideMenuBinding.imgCompleted.setImageResource(R.drawable.ic_cal_round_fill)
            }
            else -> {
                holder.rowSideMenuBinding.imgCompleted.setImageResource(R.drawable.bg_circle_border)
            }
        }

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }


    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemReportWeekDayBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
