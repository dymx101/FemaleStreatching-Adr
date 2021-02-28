package com.stretching

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.compactcalender.CompactCalendarView
import com.common.compactcalender.Event
import com.common.swipedragrecyclerview.OnDragListener
import com.common.swipedragrecyclerview.OnSwipeListener
import com.common.swipedragrecyclerview.RecyclerHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stretching.adapter.EditPlanAdapter
import com.stretching.adapter.HistoryExpandableAdapter
import com.stretching.adapter.RecentAdapter
import com.stretching.databinding.ActivityEditPlanBinding
import com.stretching.databinding.ActivityHistoryBinding
import com.stretching.databinding.ActivityRecentBinding
import com.stretching.databinding.ActivityRestBinding
import com.stretching.interfaces.AdsCallback
import com.stretching.interfaces.TopBarClickListener
import com.stretching.objects.HistoryData
import com.stretching.objects.HomeTrainingPlans
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.ErrorAlertDialog
import com.stretching.utils.Utils
import kotlinx.android.synthetic.main.activity_history.*
import java.text.SimpleDateFormat
import java.util.*


class HistoryActivity : BaseActivity() {

    var binding: ActivityHistoryBinding? = null
    var historyExpandableAdapter: HistoryExpandableAdapter? = null
    private val dateFormatForMonth = SimpleDateFormat("MMM - yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)

        AdUtils.loadBannerAd(binding!!.adView,this)
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.isBackShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.history)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        historyExpandableAdapter = HistoryExpandableAdapter(this,arrayListOf())
        binding!!.rvHistory.layoutManager = LinearLayoutManager(this)
        binding!!.rvHistory.setAdapter(historyExpandableAdapter)

        historyExpandableAdapter!!.setEventListener(object : HistoryExpandableAdapter.EventListener {
            override fun OnMenuClick(parentPosition: Int, childPosition: Int) {

                val childItem = historyExpandableAdapter!!.getMenuSubItem(parentPosition,childPosition)

                if (childItem!!.planDetail!!.planType.equals(Constant.PlanTypeMyTraining)) {
                    val i = Intent(this@HistoryActivity, MyTrainingExcListActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(childItem!!.planDetail))
                    i.putExtra(Constant.extra_day_id, childItem!!.DayId)
                    i.putExtra("day_name", childItem!!.DayName)
                    startActivity(i)
                } else if (childItem.planDetail!!.hasSubPlan) {
                    val i = Intent(this@HistoryActivity, SubPlanActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(childItem!!.planDetail))
                    i.putExtra(Constant.extra_day_id, childItem!!.DayId)
                    i.putExtra("day_name", childItem!!.DayName)
                    startActivity(i)
                } else if (childItem.planDetail!!.planDays.equals("YES")) {
                    val i = Intent(this@HistoryActivity, DaysPlanDetailActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(childItem!!.planDetail))
                    i.putExtra(Constant.extra_day_id, childItem!!.DayId)
                    i.putExtra("day_name", childItem!!.DayName)
                    startActivity(i)
                } else {
                    val i = Intent(this@HistoryActivity, ExercisesListActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(childItem!!.planDetail))
                    i.putExtra(Constant.IS_PURCHASE,false)
                    i.putExtra(Constant.extra_day_id, childItem!!.DayId)
                    i.putExtra("day_name", childItem!!.DayName)
                    startActivity(i)
                }

            }

        })

        val arrHistoryData = dbHelper.getWeekDayOfHistory()
        historyExpandableAdapter!!.addAll(arrHistoryData)
        historyExpandableAdapter!!.expandAllParents()

        compactCalendarSetup()
    }

    private fun compactCalendarSetup() {

        binding!!.compatCalenderView.removeAllEvents()
        binding!!.compatCalenderView.shouldScrollMonth(false)

        binding!!.tvMonthYear.text = dateFormatForMonth.format(Calendar.getInstance().time)

        val arrCompleteExerciseDt: ArrayList<String> = dbHelper.getCompleteExerciseDate()

        for (i in 0 until arrCompleteExerciseDt.size) {
            addEvents(Utils.parseTime(arrCompleteExerciseDt[i],Constant.DATE_TIME_24_FORMAT).time)
        }

        binding!!.compatCalenderView.setCurrentDate(Date())
        binding!!.compatCalenderView.setListener(object : CompactCalendarView.CompactCalendarViewListener {

            override fun onDayClick(dateClicked: Date?) {

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                binding!!.tvMonthYear.text = dateFormatForMonth.format(firstDayOfNewMonth!!)
            }
        })

    }

    /* Add Events */
    private fun addEvents(timeInMillis: Long) {
        val currentCalender = Calendar.getInstance(Locale.ENGLISH)
        currentCalender.time = Date()

        binding!!.compatCalenderView.addEvent(Event(Color.argb(255, 237, 55, 221), timeInMillis, "Event at " + Date(timeInMillis)))

    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onNextClick()
        {
            binding!!.compatCalenderView.scrollRight()
        }

        fun onPrevClick()
        {
            binding!!.compatCalenderView.scrollLeft()
        }

    }

    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
               finish()
            }

        }
    }

}
