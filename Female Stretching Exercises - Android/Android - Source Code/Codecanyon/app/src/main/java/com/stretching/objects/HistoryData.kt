package com.stretching.objects

import android.content.Context
import com.bignerdranch.expandablerecyclerview.model.Parent
import com.stretching.R
import com.stretching.utils.Constant
import java.util.*
import kotlin.collections.ArrayList

class HistoryData {

    class ParentData() : Parent<ChildExercise> {
        var planName: String? = null
        var planId: String? = null
        var data: ArrayList<ChildExercise> = ArrayList<ChildExercise>()

        override fun getChildList(): List<ChildExercise> {
            return data
        }

        override fun isInitiallyExpanded(): Boolean {
            return false
        }
    }

    class ChildExercise() {

        var title: String? = null
        var workoutTime: Int = 0
        var totalWorkOut: Int = 0
        var type:String? = null

    }

    fun getHistory(c:Context): ArrayList<ParentData> {
        val plans = arrayListOf<ParentData>()

        var mainPlan = ParentData()

        var subPlan = ChildExercise()
        mainPlan.data.add(subPlan)
        subPlan = ChildExercise()
        mainPlan.data.add(subPlan)
        subPlan = ChildExercise()
        mainPlan.data.add(subPlan)


        plans.add(mainPlan)

        return plans
    }

}