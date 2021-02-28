package com.stretching

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stretching.adapter.DiscoverSubPlanAdapter
import com.stretching.adapter.WorkoutListAdapter
import com.stretching.databinding.ActivityDiscoverDetailBinding
import com.stretching.objects.HomePlanTableClass
import com.stretching.objects.HomeTrainingPlans
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils


class DiscoverDetailActivity : BaseActivity() {

    var binding: ActivityDiscoverDetailBinding? = null
    var discoverSubPlanAdapter: DiscoverSubPlanAdapter? = null
    var workoutPlanData: HomePlanTableClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_discover_detail)

        AdUtils.loadBannerAd(binding!!.adView,this)
        showUnlockTrainingDialog(this)
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("workoutPlanData")) {
                    val str = intent.getStringExtra("workoutPlanData")
                    workoutPlanData = Gson().fromJson(str, object :
                        TypeToken<HomePlanTableClass>() {}.type)!!
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.handler = ClickHandler()

        discoverSubPlanAdapter = DiscoverSubPlanAdapter(this)
        binding!!.rvWorkOuts.layoutManager = LinearLayoutManager(this)
        binding!!.rvWorkOuts.setAdapter(discoverSubPlanAdapter)

        discoverSubPlanAdapter!!.setEventListener(object : DiscoverSubPlanAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = discoverSubPlanAdapter!!.getItem(position)
                val i = Intent(this@DiscoverDetailActivity, ExercisesListActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(item))
                if (item.isPro){
                    i.putExtra(Constant.IS_PURCHASE, true)
                }else {
                    i.putExtra(Constant.IS_PURCHASE, false)
                }
                startActivity(i)
            }

        })

        var isShow = true
        var scrollRange = -1
        binding!!.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                binding!!.llTopTitle.visibility = View.VISIBLE
                isShow = true
            } else if (isShow) {
                binding!!.llTopTitle.visibility =
                    View.GONE //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })

        fillData()

    }


    private fun fillData() {
        if (workoutPlanData != null) {

            binding!!.tvTitle.text = workoutPlanData!!.planName
            binding!!.tvDes.text = workoutPlanData!!.shortDes
            binding!!.tvTitleText.text = workoutPlanData!!.planName

            binding!!.titleImage.setImageResource(
                Utils.getDrawableId(
                    workoutPlanData!!.planImage,
                    this
                )
            )

            discoverSubPlanAdapter!!.addAll(dbHelper.getHomeSubPlanList(workoutPlanData!!.planId!!))
        }
    }

    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onStartClick() {
            val i = Intent(this@DiscoverDetailActivity,PerformWorkOutActivity::class.java)
            startActivity(i)
        }

        fun onBackClick() {
            finish()
        }

    }


}
