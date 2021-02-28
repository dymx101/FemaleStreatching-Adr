package com.stretching

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.stretching.adapter.RecentAdapter
import com.stretching.databinding.ActivityRecentBinding
import com.stretching.interfaces.TopBarClickListener
import com.stretching.objects.HistoryDetailsClass
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils


class RecentActivity : BaseActivity() {

    var binding: ActivityRecentBinding? = null
    var recentAdapter: RecentAdapter? = null
    var listRecentPlan = arrayListOf<HistoryDetailsClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recent)

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
        binding!!.topbar.tvTitleText.text = getString(R.string.recent)
        binding!!.topbar.topBarClickListener = TopClickListener()

        recentAdapter = RecentAdapter(this)
        binding!!.rvRecent.layoutManager = LinearLayoutManager(this)
        binding!!.rvRecent.setAdapter(recentAdapter)

        recentAdapter!!.setEventListener(object : RecentAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = recentAdapter!!.getItem(position)
                if (item!!.planDetail!!.planType.equals(Constant.PlanTypeMyTraining)) {
                    val i = Intent(this@RecentActivity, MyTrainingExcListActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(item.planDetail))
                    startActivity(i)
                }else if (item!!.planDetail!!.planDays.equals("YES")) {
                    val i = Intent(this@RecentActivity, DaysPlanDetailActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(item.planDetail))
                    startActivity(i)
                } else {
                    val i = Intent(this@RecentActivity, ExercisesListActivity::class.java)
                    i.putExtra("workoutPlanData", Gson().toJson(item.planDetail))
                    i.putExtra(Constant.IS_PURCHASE,false)
                    startActivity(i)
                }
            }

        })

        fillData()

    }


    private fun fillData() {
        listRecentPlan = dbHelper.getRecentHistoryList()
        recentAdapter!!.addAll(listRecentPlan)
    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {


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
