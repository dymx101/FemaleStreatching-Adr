package com.stretching.adapter

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.stretching.R
import com.stretching.databinding.ItemDiscoverViewPagerItemBinding
import com.stretching.objects.HomePlanTableClass
import com.stretching.utils.Utils
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class PainReliefPagerAdapter(private val context: Context, val verticalItemCount: Int) :
    PagerAdapter() {


    private val data = ArrayList<HomePlanTableClass>()
    private val inflater: LayoutInflater
    internal var mEventListener: EventListener? = null

    init {
        //        this.data.addAll(data);
        inflater = LayoutInflater.from(context)
    }

    interface EventListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setEventListener(eventListener: EventListener) {
        this.mEventListener = eventListener
    }

    fun getItem(position: Int): HomePlanTableClass {
        return data[position]
    }


    fun addAll(mData: ArrayList<HomePlanTableClass>) {
        data.clear()
        data.addAll(mData)
        notifyDataSetChanged()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return data.size.div(verticalItemCount.toDouble()).roundToInt()
    }

    override fun instantiateItem(view: ViewGroup, pos: Int): Any {
        val mLinearLayout = LinearLayout(context)
        var currPos = pos.times(verticalItemCount)

        mLinearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        mLinearLayout.orientation = LinearLayout.VERTICAL

        for (i in 0 until verticalItemCount) {
            if (currPos+i <= data.lastIndex) {
                val itemView: View = (context as Activity).getLayoutInflater()
                    .inflate(R.layout.item_discover_view_pager_item, null)
                val viewBinding: ItemDiscoverViewPagerItemBinding? = DataBindingUtil.bind(itemView)
                val item = data[currPos+i]

                viewBinding!!.imgCover.setImageResource(
                    Utils.getDrawableId(
                        item.discoverIcon,
                        context
                    )
                )
                viewBinding!!.tvTitle.text = item.planName
                viewBinding!!.tvDesc.text = item.shortDes

                viewBinding!!.container.setOnClickListener {
                    if (mEventListener != null) {
                        mEventListener!!.onItemClick(currPos+i, it)
                    }
                }
                mLinearLayout.addView(itemView)
            }
        }

        view.addView(mLinearLayout, 0)

        return mLinearLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}