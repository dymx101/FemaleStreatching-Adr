package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stretching.R
import com.stretching.objects.SideMenuItem
import com.stretching.utils.Utils
import kotlinx.android.synthetic.main.item_side_menu.view.*

class SideMenuAdapter() : RecyclerView.Adapter<SideMenuAdapter.ViewHolder>() {

    private var data: MutableList<SideMenuItem> = ArrayList<SideMenuItem>()
    private lateinit var mEventListener: EventListener
    internal var isSelected = false


    interface EventListener {
        fun onItemClick(pos: Int)
    }

    lateinit var context : Context

    constructor(context: Context) : this(){
        this.context = context
    }

    fun addAll(mData: List<SideMenuItem>) {
        try {

            this.data.clear()
            this.data.addAll(mData)


        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }


        notifyDataSetChanged()
    }

    fun add(mData: SideMenuItem) {
        try {
            this.data.add(mData)

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }


        notifyDataSetChanged()
    }

    fun getItem(pos: Int): SideMenuItem {
        return data[pos]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun changeSelection(position: Int) {
        for (i in data.indices) {
            data[i].selected = position == i
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var inflater = LayoutInflater.from(context)
        var view = inflater.inflate(R.layout.item_side_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = data[position]
        holder.tvMenuName.text = item.name;
        holder.imgMenuIcon.setImageResource(item.icon)

        if(item.selected)
        {
            holder.container.setBackgroundColor(ContextCompat.getColor(context,R.color.side_menu_selected_bg_color))
        }else{
            holder.container.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }

        holder.container.setOnClickListener {
          if (mEventListener != null){
              mEventListener.onItemClick(position)
          }
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view : View)  : RecyclerView.ViewHolder(view) {
        var tvMenuName:TextView = view.tvMenuName as TextView
        var imgMenuIcon = view.imgMenuIcon
        var container = view.container

    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }

}