package com.stretching.discover

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.drakeet.multitype.ItemViewBinder
import com.stretching.R
import com.stretching.adapter.PainReliefPagerAdapter

class ViewPagerViewBinder(private val context: Context) : ItemViewBinder<ViewPagerItem, ViewPagerViewBinder.TextHolder>() {

    private var lastShownAnimationPosition: Int = 0
    var painReliefPagerAdapter: PainReliefPagerAdapter? = null

    class TextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewPager: ViewPager = itemView.findViewById(R.id.viewPager)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): TextHolder {
        return TextHolder(inflater.inflate(R.layout.item_discover_view_pager_view, parent, false))
    }

    override fun onBindViewHolder(holder: TextHolder, item: ViewPagerItem) {

        painReliefPagerAdapter = PainReliefPagerAdapter(context,2)
     holder.viewPager.offscreenPageLimit = painReliefPagerAdapter!!.count
        holder.viewPager.adapter = painReliefPagerAdapter

        holder.viewPager.setClipToPadding(false)
        holder.viewPager.setPadding(0,0,110,0)

        setAnimation(holder.itemView, holder.adapterPosition)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastShownAnimationPosition) {
            viewToAnimate.startAnimation(AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left))
            lastShownAnimationPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: TextHolder) {
        holder.itemView.clearAnimation()
    }
}