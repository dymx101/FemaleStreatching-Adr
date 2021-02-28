package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stretching.R
import com.stretching.databinding.ItemDaysPlanBinding
import com.stretching.objects.HomePlanTableClass
import com.stretching.objects.PWeeklyDayData
import com.stretching.utils.Utils
import kotlin.collections.ArrayList


class WeekPlanAdapter(
    internal var context: Context,
    internal var workoutPlanData: HomePlanTableClass
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = mutableListOf<PWeeklyDayData>()
    internal var mEventListener: EventListener? = null
    private var boolFlagWeekComplete = false

    var continueDataChildPos:Int? = null
    var continueDataParentPos:Int? = null

    fun getItem(pos: Int): PWeeklyDayData {
        return data[pos]
    }

    fun addAll(mData: ArrayList<PWeeklyDayData>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: PWeeklyDayData) {

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
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemDaysPlanBinding>(
            inflater,
            R.layout.item_days_plan, parent, false
        )

        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val holder = viewHolder as MyViewHolder
        val item = getItem(position)

        holder.rowSideMenuBinding.tvWeekName.text = "${context.resources.getString(R.string.week)} ${item.Week_name.replace("0", "")}"
        holder.rowSideMenuBinding.tvCurrentDay.text ="0"
        holder.rowSideMenuBinding.tvCurrentDay.visibility = View.VISIBLE
        holder.rowSideMenuBinding.tvWeekDays.visibility = View.VISIBLE
        if(!boolFlagWeekComplete && item.Is_completed == "0")
        {
            holder.rowSideMenuBinding.imgWeek.setColorFilter(ContextCompat.getColor(context, R.color.green_text), android.graphics.PorterDuff.Mode.SRC_IN)
            holder.rowSideMenuBinding.weekLine.background = ContextCompat.getDrawable(context, R.color.green_text)
            holder.rowSideMenuBinding.imgLight.visibility = View.VISIBLE
            boolFlagWeekComplete = true

            var count = 0
            for (i in 0 until item.arrWeekDayData.size) {
                if (item.arrWeekDayData[i].Is_completed == "1") {
                    count++
                }
            }

            holder.rowSideMenuBinding.tvCurrentDay.text ="$count"
        }else if(item.Is_completed == "1")
        {
            holder.rowSideMenuBinding.tvCurrentDay.visibility = View.GONE
            holder.rowSideMenuBinding.tvWeekDays.visibility = View.GONE
            holder.rowSideMenuBinding.imgWeek.setImageResource(R.drawable.ic_goal_complete)
            holder.rowSideMenuBinding.imgLight.visibility = View.GONE
            holder.rowSideMenuBinding.weekLine.background = ContextCompat.getDrawable(context, R.color.green_text)
        }else{
            holder.rowSideMenuBinding.imgWeek.setImageResource(R.drawable.circle_primary)
            holder.rowSideMenuBinding.imgLight.visibility = View.VISIBLE
            holder.rowSideMenuBinding.imgWeek.setColorFilter(ContextCompat.getColor(context, R.color.gray_light_), android.graphics.PorterDuff.Mode.SRC_IN)
            holder.rowSideMenuBinding.weekLine.background = ContextCompat.getDrawable(context, R.color.green_text)
        }

        if (item.Week_name == "02") {
            holder.rowSideMenuBinding.weekLine.visibility = View.INVISIBLE
        } else {
            holder.rowSideMenuBinding.weekLine.visibility = View.VISIBLE
        }

        holder.rowSideMenuBinding.rvDays.layoutManager = GridLayoutManager(context, 4)
        val adapter = WeekDaysAdapter(context, data as ArrayList<PWeeklyDayData>, position,workoutPlanData)
        holder.rowSideMenuBinding.rvDays.adapter = adapter
        adapter.addAll(item.arrWeekDayData)

        adapter.setEventListener(object : WeekDaysAdapter.EventListener {
            override fun onItemClick(child: Int, view: View) {

                if (mEventListener != null) {
                    mEventListener!!.onItemClick(position,child, view)
                }

            }

            override fun onSetContinueItemPos(pos: Int) {
                continueDataChildPos = pos
                continueDataParentPos = position
            }
        })

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemDaysPlanBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view1: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }


}
