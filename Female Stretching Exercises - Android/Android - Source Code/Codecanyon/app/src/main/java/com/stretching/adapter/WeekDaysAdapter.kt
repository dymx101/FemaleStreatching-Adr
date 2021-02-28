package com.stretching.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.stretching.R
import com.stretching.WhatsYourGoalActivity
import com.stretching.databinding.ItemHomeWeekGoalBinding
import com.stretching.databinding.ItemWeekDayBinding
import com.stretching.databinding.ItemWhatsYourGoalBinding
import com.stretching.objects.HomePlanTableClass
import com.stretching.objects.PWeekDayData
import com.stretching.objects.PWeeklyDayData
import com.stretching.utils.Utils
import kotlin.collections.ArrayList


class WeekDaysAdapter(internal var context: Context,
                      val arrWeeklyDayStatus: ArrayList<PWeeklyDayData>,
                      val pos1: Int,
                      val workoutPlanData: HomePlanTableClass) :
    RecyclerView.Adapter<WeekDaysAdapter.MyViewHolder>() {

    private val data = mutableListOf<PWeekDayData>()
    internal var mEventListener: EventListener? = null
    var continueDataPos:Int? = null

    fun getItem(pos: Int): PWeekDayData {
        return data[pos]
    }

    fun addAll(mData: ArrayList<PWeekDayData>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: PWeekDayData) {

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
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemWeekDayBinding>(
            inflater,
            R.layout.item_week_day, parent, false
        )
        rowSideMenuBinding.root.layoutParams.width = parent.measuredWidth / 4

        return MyViewHolder(rowSideMenuBinding)
    }

    private var flagPrevDay = true
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = getItem(position)
        holder.rowSideMenuBinding.tvDate.text = item.Day_name.replace("0", "")

        if(item.Day_name == "Cup")
        {
            holder.rowSideMenuBinding.tvDate.tag = "n"

            holder.rowSideMenuBinding.imgCup.visibility = View.VISIBLE
            holder.rowSideMenuBinding.llDay.visibility = View.GONE

            if (item.Is_completed == "1") {
                holder.rowSideMenuBinding.imgCup.setImageResource(R.drawable.ic_week_winner_cup)
            } else {
                holder.rowSideMenuBinding.imgCup.setImageResource(R.drawable.ic_week_winner_cup_gray)
            }
        }else{
            holder.rowSideMenuBinding.tvDate.tag = "y"
            if (flagPrevDay && pos1 != 0) {
                flagPrevDay = arrWeeklyDayStatus[pos1 - 1].Is_completed == "1"
            }

            if (arrWeeklyDayStatus[pos1].Is_completed == "1") {
                holder.rowSideMenuBinding.imgCompleted.visibility = View.VISIBLE
            holder.rowSideMenuBinding.llDay.visibility = View.GONE

            } else {
                when {
                    item.Is_completed == "1" -> {
                        holder.rowSideMenuBinding.imgCompleted.visibility = View.VISIBLE
                        holder.rowSideMenuBinding.llDay.visibility = View.GONE
                        holder.rowSideMenuBinding.imgCup.visibility = View.GONE

                        flagPrevDay = true
                    }
                    flagPrevDay -> {
//                            holder.imgIndicator.setImageResource(R.drawable.ic_week_arrow_next_gray)
                        holder.rowSideMenuBinding.imgCup.visibility = View.GONE
                        holder.rowSideMenuBinding.llDay.visibility = View.VISIBLE
                        holder.rowSideMenuBinding.imgCompleted.visibility = View.GONE

                        holder.rowSideMenuBinding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.green_text))
                        holder.rowSideMenuBinding.llDay.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_dotted_border_green)
                        if (mEventListener != null) {
                            mEventListener!!.onSetContinueItemPos(position)
                        }
                        flagPrevDay = false
                    }
                    else -> {
//                            holder.imgIndicator.setImageResource(R.drawable.ic_week_arrow_next_gray)
                        holder.rowSideMenuBinding.imgCup.visibility = View.GONE
                        holder.rowSideMenuBinding.llDay.visibility = View.VISIBLE
                        holder.rowSideMenuBinding.imgCompleted.visibility = View.GONE

                        holder.rowSideMenuBinding.tvDate.setTextColor(ContextCompat.getColor(context, R.color.col_999))
                        holder.rowSideMenuBinding.llDay.background =
                            ContextCompat.getDrawable(context, R.drawable.bg_circle_border)
                        holder.rowSideMenuBinding.tvDate.tag = "n"

                        flagPrevDay = false
                    }
                }
            }

        }

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                try {
                    if (item.Day_name != "Cup") {
                        if (holder.rowSideMenuBinding.tvDate.tag == "y") {
                            mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
                        } else {
                            Toast.makeText(context, "Please finish the previous challenge date first.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }



        if (position == 3 || position == 7) {
            holder.rowSideMenuBinding.imgArrow.visibility = View.GONE
        } else {
            holder.rowSideMenuBinding.imgArrow.visibility = View.VISIBLE
        }

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemWeekDayBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
        fun onSetContinueItemPos(position: Int)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
