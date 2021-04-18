package com.stretching

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.compactcalender.CompactCalendarView
import com.common.compactcalender.Event
import com.google.gson.Gson
import com.stretching.adapter.HistoryExpandableAdapter
import com.stretching.databinding.ActivityHistoryBinding
import com.stretching.interfaces.CallbackListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import java.text.SimpleDateFormat
import java.util.*


class HistoryActivity : BaseActivity(), CallbackListener {

    var binding: ActivityHistoryBinding? = null
    var historyExpandableAdapter: HistoryExpandableAdapter? = null
    private val dateFormatForMonth = SimpleDateFormat("MMM - yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)

//        AdUtils.loadBannerAd(binding!!.adView,this)
//        AdUtils.loadBannerGoogleAd(this,binding!!.llAdView,Constant.BANNER_TYPE)

        if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_GOOGLE) {
            AdUtils.loadGoogleBannerAd(this, binding!!.llAdView, Constant.BANNER_TYPE)
            binding!!.llAdViewFacebook.visibility=View.GONE
        }else if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_FACEBOOK) {
            AdUtils.loadFacebookBannerAd(this,binding!!.llAdViewFacebook)
        }else{
            binding!!.llAdViewFacebook.visibility=View.GONE
        }


        if (Utils.isPurchased(this)) {
            binding!!.llAdViewFacebook.visibility = View.GONE
        }
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
                    i.putExtra(Constant.extra_day_id, childItem!!.DayId)
                    i.putExtra("day_name", childItem!!.DayName)
                    i.putExtra(Constant.IS_PURCHASE,false)
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
        openInternetDialog(this)
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

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

    }

}
