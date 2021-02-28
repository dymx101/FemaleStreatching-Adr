package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stretching.R
import com.stretching.WhatsYourGoalActivity
import com.stretching.databinding.ItemMusicBinding
import com.stretching.objects.Music
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MusicListAdapter(internal var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = mutableListOf<Music>()
    internal var mEventListener: EventListener? = null

    fun getItem(pos: Int): Music {
        return data[pos]
    }

    fun addAll(mData: ArrayList<Music>) {
        try {
            data.clear()
            data.addAll(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }
        notifyDataSetChanged()
    }

    fun add(item: Music) {

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
        val rowSideMenuBinding = DataBindingUtil.inflate<ItemMusicBinding>(
            inflater,
            R.layout.item_music, parent, false
        )

        return MyViewHolder(rowSideMenuBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val holder = viewHolder as MyViewHolder
        val item = getItem(position)

        holder.rowSideMenuBinding.tvName.text = item.name
        //holder.rowSideMenuBinding.tvDuration.text = item.duration

        val d = Date(item.duration!!.toLong())
        val df = SimpleDateFormat("mm:ss")
        df.setTimeZone(TimeZone.getTimeZone("GMT"))
        holder.rowSideMenuBinding.tvDuration.text = df.format(d)

        holder.rowSideMenuBinding.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClick(position, holder.rowSideMenuBinding.root)
            }
        }

    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(internal var rowSideMenuBinding: ItemMusicBinding) :
        RecyclerView.ViewHolder(rowSideMenuBinding.root)

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }


}
