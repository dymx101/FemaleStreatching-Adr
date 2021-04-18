package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.reminderNew.Reminder
import com.stretching.R
import com.stretching.databinding.ItemReminderBinding
import com.stretching.utils.Utils


class ReminderAdapter(internal var context: Context) :
        RecyclerView.Adapter<ReminderAdapter.MyViewHolder>() {

    private val data = mutableListOf<Reminder>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): Reminder {
        return data[pos]
    }

    fun addAll(mData: java.util.ArrayList<Reminder>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: Reminder) {

        try {
            this.data.add(item)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun removeAt(pos:Int)
    {
        data.removeAt(pos)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemReminderBinding>(
                inflater,
                R.layout.item_reminder, parent, false
        )
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)

        holder.rowSideMenuBinding.tvTime.text = item.time
        holder.rowSideMenuBinding.switchReminder.isChecked = item.active.equals("true")


        val strDays = item.days!!.split(",").sorted()
        holder.rowSideMenuBinding.tvDays.text = ""

        for (i in strDays.indices) {
            if (holder.rowSideMenuBinding.tvDays.text.isEmpty()) {
                holder.rowSideMenuBinding.tvDays.text =
                        Utils.getShortDayName(strDays[i])
            } else {
                holder.rowSideMenuBinding.tvDays.append(
                        (", ").plus(
                                Utils.getShortDayName(strDays[i])
                        )
                )
            }
        }

        holder.rowSideMenuBinding.llRepeat.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onRepeatClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.tvTime.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onTimeClick(position, holder.rowSideMenuBinding.root,data[position].time!!.split(":")[0].toInt(),
                    data[position].time!!.split(":")[1].toInt())
            }
        }

        holder.rowSideMenuBinding.imgDelete.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onDeleteClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.switchReminder.setOnCheckedChangeListener { buttonView, isChecked ->
            if (mEventListener != null) {
                mEventListener!!.onSwitchChecked(
                        position,
                        isChecked,
                        holder.rowSideMenuBinding.root
                )
            }
        }

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemReminderBinding) :
            RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onRepeatClick(position: Int, view: View)
        fun onTimeClick(position: Int, view: View,hour:Int,minute:Int)
        fun onDeleteClick(position: Int, view: View)
        fun onSwitchChecked(position: Int, isChecked: Boolean, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
