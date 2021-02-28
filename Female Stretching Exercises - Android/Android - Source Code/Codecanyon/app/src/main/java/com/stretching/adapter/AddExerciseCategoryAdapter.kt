package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stretching.R
import com.stretching.WhatsYourGoalActivity
import com.stretching.databinding.*
import com.stretching.objects.MyTrainingCategoryTableClass
import com.stretching.utils.Utils
import kotlin.collections.ArrayList


class AddExerciseCategoryAdapter(internal var context: Context) :
    RecyclerView.Adapter<AddExerciseCategoryAdapter.MyViewHolder>() {

    private val data = mutableListOf<MyTrainingCategoryTableClass>()
    internal var mEventListener: EventListener? = null
    var selectedPos = 0;

    fun getItem(pos: Int): MyTrainingCategoryTableClass {
        return data[pos]
    }

    fun addAll(mData: ArrayList<MyTrainingCategoryTableClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item:MyTrainingCategoryTableClass) {

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
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemAddExerciseCategoryBinding>(
            inflater,
            R.layout.item_add_exercise_category, parent, false
        )
        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                selectedPos = position
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.tvCategory.text =  getItem(position).cName

        if (position == selectedPos) {
            holder.rowSideMenuBinding.tvCategory.background = context.getDrawable(R.drawable.btn_bg_round_gradiant)
            holder.rowSideMenuBinding.tvCategory.setTextColor(ContextCompat.getColor(context,R.color.white))
        } else {
            holder.rowSideMenuBinding.tvCategory.background = context.getDrawable(R.color.primary)
            holder.rowSideMenuBinding.tvCategory.setTextColor(ContextCompat.getColor(context,R.color.gray_text))
        }


    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemAddExerciseCategoryBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

}
