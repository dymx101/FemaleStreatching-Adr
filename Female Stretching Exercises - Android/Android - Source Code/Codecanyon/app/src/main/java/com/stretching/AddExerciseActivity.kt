package com.stretching

import android.app.Activity
import android.content.Intent
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
import com.common.swipedragrecyclerview.OnDragListener
import com.common.swipedragrecyclerview.OnSwipeListener
import com.common.swipedragrecyclerview.RecyclerHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stretching.adapter.AddExerciseAdapter
import com.stretching.adapter.AddExerciseCategoryAdapter
import com.stretching.adapter.EditPlanAdapter
import com.stretching.adapter.RecentAdapter
import com.stretching.databinding.ActivityAddExerciesBinding
import com.stretching.databinding.ActivityEditPlanBinding
import com.stretching.databinding.ActivityRecentBinding
import com.stretching.databinding.ActivityRestBinding
import com.stretching.interfaces.TopBarClickListener
import com.stretching.objects.HomeTrainingPlans
import com.stretching.objects.MyTrainingCatExTableClass
import com.stretching.objects.MyTrainingCategoryTableClass
import com.stretching.utils.AdUtils
import com.stretching.utils.ErrorAlertDialog
import com.stretching.utils.Utils


class AddExerciseActivity : BaseActivity() {

    var binding: ActivityAddExerciesBinding? = null
    var addExerciseCategoryAdapter: AddExerciseCategoryAdapter? = null
    var addExerciseAdapter: AddExerciseAdapter? = null
    var currCategory:MyTrainingCategoryTableClass?= null
    var fromNewTraining = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_exercies)

        AdUtils.loadBannerAd(binding!!.adView,this)
        initIntentParam()
        init()
    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("from_new_training")) {
                    fromNewTraining = intent.getBooleanExtra("from_new_training",false)

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.isBackShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.add_exercise)
        binding!!.topbar.topBarClickListener = TopClickListener()

        addExerciseCategoryAdapter = AddExerciseCategoryAdapter(this)
        binding!!.rvCategory.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        binding!!.rvCategory.setAdapter(addExerciseCategoryAdapter)

        addExerciseCategoryAdapter!!.setEventListener(object : AddExerciseCategoryAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                currCategory = addExerciseCategoryAdapter!!.getItem(position)
                addExerciseAdapter!!.addAll(dbHelper.getMyTrainingCategoryExList(currCategory!!.cId))
                binding!!.rvExercise.scheduleLayoutAnimation()
                addExerciseCategoryAdapter!!.notifyDataSetChanged()
            }
        })

        addExerciseCategoryAdapter!!.addAll(dbHelper.getMyTrainingCategoryList())
        currCategory = addExerciseCategoryAdapter!!.getItem(0)

        addExerciseAdapter = AddExerciseAdapter(this)
        binding!!.rvExercise.layoutManager = LinearLayoutManager(this)
        binding!!.rvExercise.setAdapter(addExerciseAdapter)

        addExerciseAdapter!!.setEventListener(object : AddExerciseAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item =  addExerciseAdapter!!.getItem(position)
                val i = Intent(this@AddExerciseActivity,AddExerciseDetailActivity::class.java)
                i.putExtra("ExDetail",Gson().toJson(item))
                i.putExtra("categoryDetail",Gson().toJson(currCategory))
                startActivityForResult(i,7484)
            }
        })

        addExerciseAdapter!!.addAll(dbHelper.getMyTrainingCategoryExList(addExerciseCategoryAdapter!!.getItem(0).cId))
        binding!!.rvExercise.scheduleLayoutAnimation()

        fillData()

    }


    private fun fillData() {

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 7484 && resultCode == Activity.RESULT_OK)
        {
            if(fromNewTraining)
            {
                setResult(Activity.RESULT_OK,data)
            }else{

                val i = Intent(this,NewTrainingActivity::class.java)
                i.putExtra("workoutPlanData",data!!.getStringExtra("workoutPlanData"))
                i.putExtra("categoryDetail",data!!.getStringExtra("categoryDetail"))
                startActivity(i)
            }
            finish()
        }
    }

}
