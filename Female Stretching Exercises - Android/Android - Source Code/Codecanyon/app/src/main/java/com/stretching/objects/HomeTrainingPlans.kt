package com.stretching.objects

import android.content.Context
import com.bignerdranch.expandablerecyclerview.model.Parent
import com.stretching.R
import com.stretching.utils.Constant
import java.util.*
import kotlin.collections.ArrayList

class HomeTrainingPlans {

    class MainPlan() : Parent<Plan> {
        var planName: String? = null
        var planId: String? = null
        var data: ArrayList<Plan> = ArrayList<Plan>()

        override fun getChildList(): List<Plan> {
            return data
        }

        override fun isInitiallyExpanded(): Boolean {
            return false
        }
    }

    class Plan() {

        var title: String? = null
        var des: String? = null
        var workoutTime: Int = 0
        var totalWorkOut: Int = 0
        var isPro: Boolean = false
        var isGoShow: Boolean = false
        var type:String? = null
        var coverImg:Int? = null

    }

    fun getPlans(c:Context): ArrayList<MainPlan> {
        val plans = arrayListOf<MainPlan>()

        var mainPlan = MainPlan()
        mainPlan.planId = "1"
        mainPlan.planName = "Routine"

        var subPlan = Plan()
        subPlan.title = "Morning Warmup"
        subPlan.workoutTime = 5
        subPlan.totalWorkOut = 10
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_morning
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Sleepy Time Stretching"
        subPlan.workoutTime = 8
        subPlan.totalWorkOut = 13
        subPlan.coverImg = R.drawable.cover_sleepy
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        mainPlan = MainPlan()
        mainPlan.planId = "2"
        mainPlan.planName = "Flexibility"

        subPlan = Plan()
        subPlan.title = "Splits training"
        subPlan.type =  Constant.HOME_PLAN_TYPE_TRAINING
        subPlan.des = "3 levels"
        subPlan.isPro = true
        subPlan.isGoShow = true
        subPlan.coverImg = R.drawable.cover_split_advanced
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "14 Days Plan"
        subPlan.des = "Get full splits step by step"
        subPlan.type =  Constant.HOME_PLAN_TYPE_PLAN
        subPlan.coverImg = R.drawable.cover_split_14
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        mainPlan = MainPlan()
        mainPlan.planId = "3"
        mainPlan.planName = "Full body"

        subPlan = Plan()
        subPlan.title = "Full boady stretching"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_full_body
        subPlan.workoutTime = 7
        subPlan.totalWorkOut = 14
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        mainPlan = MainPlan()
        mainPlan.planId = "4"
        mainPlan.planName = "Pain Relief"

        subPlan = Plan()
        subPlan.title = "Neck & Shoulder stretching"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_neck
        subPlan.workoutTime = 11
        subPlan.totalWorkOut = 17
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Lower back Pain Relief"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.workoutTime = 11
        subPlan.coverImg = R.drawable.cover_back
        subPlan.totalWorkOut = 18
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Knee Pain Relief"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.des = "Relieve pain and strengthen weak knees"
        subPlan.coverImg = R.drawable.cover_knee_pain_relief_1
        subPlan.isPro = true
        subPlan.isGoShow = true
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Foam Roller Knee Pain Relief"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.des = "Roll away knee pain effectively with just a foam roller"
        subPlan.coverImg = R.drawable.cover_knee_pain_relief_2
        subPlan.isPro = true
        subPlan.isGoShow = true
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        mainPlan = MainPlan()
        mainPlan.planId = "5"
        mainPlan.planName = "For runners"

        subPlan = Plan()
        subPlan.title = "Pre-Run warm up"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_run_pre
        subPlan.workoutTime = 5
        subPlan.totalWorkOut = 10
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Post-Run cool down"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
            subPlan.coverImg = R.drawable.cover_run_post
        subPlan.workoutTime = 6
        subPlan.totalWorkOut = 12
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        mainPlan = MainPlan()
        mainPlan.planId = "6"
        mainPlan.planName = "Lower body"

        subPlan = Plan()
        subPlan.title = "Lower body stretching 7 min"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_lower_body_7min
        subPlan.workoutTime = 7
        subPlan.totalWorkOut = 18
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Lower body stretching 15 min"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_lower_body_15min
        subPlan.workoutTime = 15
        subPlan.totalWorkOut = 22
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        mainPlan = MainPlan()
        mainPlan.planId = "7"
        mainPlan.planName = "Posture correction"

        subPlan = Plan()
        subPlan.title = "Bow Legs correction"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_legs_correction
        subPlan.isGoShow = true
        subPlan.isPro = true
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Knock knee correction"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_knee_correction
        subPlan.isGoShow = true
        subPlan.isPro = true
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        mainPlan = MainPlan()
        mainPlan.planId = "8"
        mainPlan.planName = "Back"

        subPlan = Plan()
        subPlan.title = "Back stretching 7 min"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.workoutTime = 7
        subPlan.coverImg = R.drawable.cover_back_7min
        subPlan.totalWorkOut = 15
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Back stretching 12 min"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_back_12min
        subPlan.workoutTime = 12
        subPlan.totalWorkOut = 21
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        mainPlan = MainPlan()
        mainPlan.planId = "9"
        mainPlan.planName = "Upper body"

        subPlan = Plan()
        subPlan.title = "Shoulder Tension Relief"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.des = "Fast relieve shoulder tension snd prevent tightness"
        subPlan.coverImg = R.drawable.cover_shoulder_tension_relief
        subPlan.isPro = true
        subPlan.isGoShow = true
        mainPlan.data.add(subPlan)

        subPlan = Plan()
        subPlan.title = "Upper body stretching"
        subPlan.type =  Constant.HOME_PLAN_TYPE_WORK_OUT
        subPlan.coverImg = R.drawable.cover_upper_body
        subPlan.workoutTime = 8
        subPlan.totalWorkOut = 16
        mainPlan.data.add(subPlan)

        plans.add(mainPlan)

        return plans
    }

}