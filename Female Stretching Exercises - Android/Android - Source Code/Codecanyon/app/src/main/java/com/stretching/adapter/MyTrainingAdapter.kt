package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stretching.R
import com.stretching.WhatsYourGoalActivity
import com.stretching.databinding.*
import com.stretching.objects.HomePlanTableClass
import com.stretching.utils.Utils
import kotlin.collections.ArrayList


class MyTrainingAdapter(internal var context: Context) :
    RecyclerView.Adapter<MyTrainingAdapter.MyViewHolder>(){

    private val data = mutableListOf<HomePlanTableClass>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): HomePlanTableClass {
        return data[pos]
    }

    fun addAll(mData: ArrayList<HomePlanTableClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: HomePlanTableClass) {

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
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemMyTrainingBinding>(
            inflater,
            R.layout.item_my_training, parent, false)
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = getItem(position)

        holder.rowSideMenuBinding.tvName.text = item.planName
        holder.rowSideMenuBinding.tvTotalWorkout.text = item.planWorkouts.plus(" "+context.getString(R.string.exercises))

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.imgMore.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onMoreClick(position, it)
            }
        }

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemMyTrainingBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
        fun onMoreClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
