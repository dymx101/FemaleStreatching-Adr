package com.stretching

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.stretching.adapter.HomePlansAdapter
import com.stretching.adapter.HomeWeekGoalAdapter
import com.stretching.databinding.ActivityHomeBinding
import com.stretching.interfaces.AdsCallback
import com.stretching.interfaces.CallbackListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.objects.HistoryDetailsClass
import com.stretching.objects.HomePlanTableClass
import com.stretching.utils.*
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Debug
import com.stretching.utils.ExitStrategy
import com.stretching.utils.Utils
import kotlin.math.roundToInt


class HomeActivity : BaseActivity(), CallbackListener {

    var binding: ActivityHomeBinding? = null
    var homeWeekGoalAdapter: HomeWeekGoalAdapter? = null
    var homePlansAdapter: HomePlansAdapter? = null
    var recentPlan: HomePlanTableClass? = null
    var lastWorkout: HistoryDetailsClass? = null
    var onClickAd = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        Log.e("TAG", "MainActivity:::::::::onCreate::::Main Activity:::  ")
        initTopBar(binding!!.topbar)
        initDrawerMenu(true)
        init()


        if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_GOOGLE) {
            AdUtils.loadGoogleBannerAd(this, binding!!.llAdView, Constant.BANNER_TYPE)
            binding!!.llAdViewFacebook.visibility = View.GONE
        } else if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_FACEBOOK) {
            AdUtils.loadFacebookBannerAd(this, binding!!.llAdViewFacebook)
        } else {
            binding!!.llAdViewFacebook.visibility = View.GONE
        }

        if (Utils.isPurchased(this)) {
            binding!!.llAdViewFacebook.visibility = View.GONE
        }
        subScribeToFirebaseTopic()

    }

    private fun subScribeToFirebaseTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("female_stretching")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Debug.e("subScribeFirebaseTopic", ": Fail")
                } else {
                    Debug.e("subScribeFirebaseTopic", ": Success")
                }
            }
    }

    private fun init() {
        binding!!.topbar.isMenuShow = true
        binding!!.topbar.isDiscoverShow = true
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        setupWeekTopData()

        homePlansAdapter = HomePlansAdapter(this)
        binding!!.rvPlans.layoutManager = LinearLayoutManager(this)
        binding!!.rvPlans.adapter = homePlansAdapter
        homePlansAdapter!!.setEventListener(object : HomePlansAdapter.EventListener {

            override fun onItemClick(position: Int, view: View) {
                val item = homePlansAdapter!!.getItem(position)
                if (!item.isPro) {
                    Log.e("TAG", "onItemClick:OnItemL:::: ${item.isPro}")
                    if (onClickAd == Constant.FIRST_CLICK_COUNT && Constant.FIRST_CLICK_COUNT != 0) {
                        if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_GOOGLE) {
                            AdUtils.loadGoogleFullAd(this@HomeActivity, object : AdsCallback {
                                override fun startNextScreenAfterAd() {
                                    startNextScreenMove(position)
                                }
                            })
                        } else if (Constant.AD_TYPE_FB_GOOGLE == Constant.AD_FACEBOOK) {
                            AdUtils.loadFacebookFullAd(this@HomeActivity, object : AdsCallback {
                                override fun startNextScreenAfterAd() {
                                    startNextScreenMove(position)
                                }
                            })
                        } else {
                            startNextScreenMove(position)
                        }
                        onClickAd = 1
                    } else {
                        startNextScreenMove(position)
                        onClickAd += 1
                    }
                }else{
                    startNextScreenMove(position)
                }


            }
        })

        homePlansAdapter!!.addAll(dbHelper.getHomePlanList(Constant.PlanTypeWorkout))


    }


    fun startNextScreenMove(position: Int) {
        val item = homePlansAdapter!!.getItem(position)
        if (item.hasSubPlan) {
            val i = Intent(this@HomeActivity, SubPlanActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(item))
            startActivity(i)
        } else if (item.planDays.equals("YES")) {
            val i = Intent(this@HomeActivity, DaysPlanDetailActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(item))
            startActivity(i)
        } else {
            val i = Intent(this@HomeActivity, ExercisesListActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(item))
            if (item.isPro) {
                i.putExtra(Constant.IS_PURCHASE, true)
            } else {
                i.putExtra(Constant.IS_PURCHASE, false)
            }
            startActivity(i)
        }
    }

    private fun setupWeekTopData() {

        try {
            if (Utils.getPref(this, Constant.PREF_IS_WEEK_GOAL_DAYS_SET, false)) {

                binding!!.llWeekGoal.visibility = View.VISIBLE
                binding!!.llSetGoal.visibility = View.GONE

                val arrCurrentWeek = Utils.getCurrentWeek()
                var completedWeekDay = 0
                for (pos in 0 until arrCurrentWeek.size) {
                    if (dbHelper.isHistoryAvailable(
                            Utils.parseTime(
                                arrCurrentWeek[pos],
                                Constant.DATE_TIME_24_FORMAT,
                                Constant.DATE_FORMAT
                            )
                        )
                    ) {
                        completedWeekDay++
                    }
                }

                val weekDayGoal = Utils.getPref(this, Constant.PREF_WEEK_GOAL_DAYS, 7)

                /* binding!!.tvCompletedGoalCount.text = HtmlCompat.fromHtml(
                "<font color='${ContextCompat.getColor(this, R.color.green_text)}'>$completedWeekDay</font>/$weekDayGoal",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )*/

                binding!!.tvCompletedGoalCount.text = completedWeekDay.toString()
                binding!!.tvTotalGoalCount.text = "/$weekDayGoal"

                homeWeekGoalAdapter = HomeWeekGoalAdapter(this)
                binding!!.rvWeekGoal.adapter = homeWeekGoalAdapter

                homeWeekGoalAdapter!!.setEventListener(object : HomeWeekGoalAdapter.EventListener {
                    override fun onItemClick(position: Int, view: View) {
                        val i = Intent(this@HomeActivity, HistoryActivity::class.java)
                        startActivity(i)
                    }
                })

            } else {
                binding!!.llWeekGoal.visibility = View.GONE
                binding!!.llSetGoal.visibility = View.VISIBLE
            }

            if (dbHelper.getHistoryList().isNullOrEmpty()) {
                binding!!.llRecent.visibility = View.GONE
                binding!!.llReport.visibility = View.GONE
            } else {
                binding!!.llRecent.visibility = View.VISIBLE
                binding!!.llReport.visibility = View.VISIBLE

                binding!!.tvWorkOuts.text = dbHelper.getHistoryTotalWorkout().toString()
                binding!!.tvCalorie.text = dbHelper.getHistoryTotalKCal().toInt().toString()
                binding!!.tvMinutes.text =
                    ((dbHelper.getHistoryTotalMinutes() / 60).toDouble()).roundToInt().toString()

                lastWorkout = dbHelper.getRecentHistory()
                if (lastWorkout != null) {
                    recentPlan = dbHelper.getPlanByPlanId(lastWorkout!!.PlanId.toInt())

                    if (recentPlan!!.planDays == Constant.PlanDaysYes) {
                        binding!!.tvRecentWorkOutName.text = recentPlan!!.planName
                        val item = dbHelper.getDaysPlanData(lastWorkout!!.DayId)
                        recentPlan!!.planMinutes = item!!.Minutes
                        recentPlan!!.planWorkouts = item!!.Workouts
                    } else {
                        binding!!.tvRecentWorkOutName.text = lastWorkout!!.PlanName
                    }

                    binding!!.tvRecentTime.text = Utils.parseTime(
                        lastWorkout!!.DateTime,
                        Constant.DATE_TIME_24_FORMAT,
                        "HH:mm"
                    )
                    binding!!.imgRecentWorkout.setImageResource(
                        Utils.getDrawableId(
                            recentPlan!!.planThumbnail,
                            this
                        )
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
        changeSelection(0)
        setupWeekTopData()
        homePlansAdapter?.notifyDataSetChanged()
        Log.e("TAG", "MainActivity:::::::::onResume:::Main Activity:::::: ")
    }


    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.menu_discover))) {
                val i = Intent(this@HomeActivity, DiscoverActivity::class.java)
                startActivity(i)
            }

        }
    }

    inner class ClickHandler {

        fun onSetGoalClick() {
            val i = Intent(this@HomeActivity, SetYourWeeklyGoalActivity::class.java)
            startActivityForResult(i, 8019)
        }

        fun onEditWeekGoalClick() {
            val i = Intent(this@HomeActivity, SetYourWeeklyGoalActivity::class.java)
            startActivity(i)
        }

        fun onBackToTopClick() {
            binding!!.nestedScrollView.isSmoothScrollingEnabled = true
            binding!!.nestedScrollView.fullScroll(View.FOCUS_UP)
        }

        fun onMyTrainingClick() {
            val i = Intent(this@HomeActivity, MyTrainingActivity::class.java)
            startActivity(i)
        }

        fun onRecentViewAllClick() {
            val i = Intent(this@HomeActivity, RecentActivity::class.java)
            startActivity(i)
        }

        fun onRecentViewClick() {

            if (recentPlan!!.planType.equals(Constant.PlanTypeMyTraining)) {
                val i = Intent(this@HomeActivity, MyTrainingExcListActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(recentPlan))
                startActivity(i)
            } else if (recentPlan!!.hasSubPlan) {
                val i = Intent(this@HomeActivity, SubPlanActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(recentPlan))
                startActivity(i)
            } else if (recentPlan!!.planDays.equals("YES")) {
                val i = Intent(this@HomeActivity, DaysPlanDetailActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(recentPlan))
                startActivity(i)
            } else {
                val i = Intent(this@HomeActivity, ExercisesListActivity::class.java)
                i.putExtra("workoutPlanData", Gson().toJson(recentPlan))
                i.putExtra(Constant.IS_PURCHASE, false)
                startActivity(i)
            }

            /*val i = Intent(this@HomeActivity, ExercisesListActivity::class.java)
            i.putExtra("workoutPlanData", Gson().toJson(recentPlan))
            i.putExtra(Constant.extra_day_id, lastWorkout!!.DayId)
            i.putExtra("day_name", lastWorkout!!.DayName)
            startActivity(i)*/
        }
    }

    override fun onBackPressed() {
        try {
            if (result != null && result!!.isDrawerOpen) {
                hideMenu(true)
            } else {
                if (ExitStrategy.canExit()) {
                    super.onBackPressed()
                } else {
                    ExitStrategy.startExitDelay(2000)
                    showToast(getString(R.string.exit_msg))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

    }


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 8019 && resultCode == Activity.RESULT_OK) {
            setupWeekTopData()
        }

    }*/

}
