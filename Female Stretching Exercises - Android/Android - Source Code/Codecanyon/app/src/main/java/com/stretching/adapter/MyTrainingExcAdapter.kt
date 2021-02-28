package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stretching.R
import com.stretching.WhatsYourGoalActivity
import com.stretching.databinding.ItemEditPlanBinding
import com.stretching.databinding.ItemHomeWeekGoalBinding
import com.stretching.databinding.ItemNewTrainingExerciseBinding
import com.stretching.databinding.ItemWorkoutListBinding
import com.stretching.objects.HomeExTableClass
import com.stretching.objects.MyTrainingCatExTableClass
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class MyTrainingExcAdapter(internal var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data = arrayListOf<HomeExTableClass>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): HomeExTableClass {
        return data[pos]
    }


    fun addAll(mData: ArrayList<HomeExTableClass>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        notifyDataSetChanged()
    }

    fun add(item: HomeExTableClass) {

        try {
            this.data.add(item)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        notifyItemInserted(data.size - 1)
        notifyDataSetChanged()
    }

    fun update(position: Int, item: HomeExTableClass) {
        try {
            data.removeAt(position)
            data.add(position, item)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        notifyDataSetChanged()
    }

    fun removeAt(pos: Int) {
        data.removeAt(pos)
        notifyDataSetChanged()
    }

    fun onChangePosition(fromPos: Int, toPos: Int) {
        Collections.swap(data, fromPos, toPos)
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemNewTrainingExerciseBinding>(
            inflater,
            R.layout.item_new_training_exercise, parent, false
        )

        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val holder = viewHolder as MyViewHolder

        val item = getItem(position)

        holder.rowSideMenuBinding.tvName.text = item.exName

        if (item.exUnit.equals(Constant.workout_type_step)) {
            if (item.updatedExTime.isNullOrEmpty())
                holder.rowSideMenuBinding.tvTime.text = "X ${item.exTime}"
            else
                holder.rowSideMenuBinding.tvTime.text = "X ${item.updatedExTime}"

        } else {
            if (item.updatedExTime.isNullOrEmpty())
                holder.rowSideMenuBinding.tvTime.text =
                    Utils.secToString(item.exTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
            else
                holder.rowSideMenuBinding.tvTime.text =
                    Utils.secToString(item.updatedExTime!!.toInt(), Constant.WORKOUT_TIME_FORMAT)
        }

        holder.rowSideMenuBinding.imgSelect.visibility = View.INVISIBLE
        holder.rowSideMenuBinding.imgClose.visibility = View.GONE

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }
        holder.rowSideMenuBinding.imgClose.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onCloseClick(position, holder.rowSideMenuBinding.root)
            }
        }

        holder.rowSideMenuBinding.viewFlipper.removeAllViews()
        val listImg: ArrayList<String>? =
            Utils.ReplaceSpacialCharacters(item.exPath!!)?.let { Utils.getAssetItems(context, it) }

        if (listImg != null) {
            for (i in 0 until listImg.size) {
                val imgview = ImageView(context)

                Glide.with(context).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                holder.rowSideMenuBinding.viewFlipper.addView(imgview)
            }
        }

        holder.rowSideMenuBinding.viewFlipper.isAutoStart = true
        holder.rowSideMenuBinding.viewFlipper.setFlipInterval(context.resources.getInteger(R.integer.viewfliper_animation))
        holder.rowSideMenuBinding.viewFlipper.startFlipping()

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemNewTrainingExerciseBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
        fun onCloseClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }


}
