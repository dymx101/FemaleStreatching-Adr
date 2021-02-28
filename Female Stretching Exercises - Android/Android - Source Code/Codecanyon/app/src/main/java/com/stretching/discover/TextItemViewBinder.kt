package com.stretching.discover

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.stretching.R

class TextItemViewBinder : ItemViewBinder<TextItem, TextItemViewBinder.TextHolder>() {

    private var lastShownAnimationPosition: Int = 0

    class TextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.tvDiscoverTitle)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): TextHolder {
        return TextHolder(inflater.inflate(R.layout.item_discover_text_titile_view, parent, false))
    }

    override fun onBindViewHolder(holder: TextHolder, item: TextItem) {
        holder.text.text = item.text
        // should show animation, ref: https://github.com/drakeet/MultiType/issues/149
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