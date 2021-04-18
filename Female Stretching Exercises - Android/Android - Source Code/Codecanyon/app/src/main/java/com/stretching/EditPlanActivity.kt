package com.stretching

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
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
import com.stretching.adapter.EditPlanAdapter
import com.stretching.databinding.ActivityEditPlanBinding
import com.stretching.interfaces.TopBarClickListener
import com.stretching.objects.HomeExTableClass
import com.stretching.objects.HomePlanTableClass
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import com.stretching.db.DataHelper
import com.stretching.interfaces.CallbackListener


class EditPlanActivity : BaseActivity(), CallbackListener {

    var binding: ActivityEditPlanBinding? = null
    var editPlanAdapter: EditPlanAdapter? = null
    var workoutPlanData: HomePlanTableClass? = null
    var isSaveDialogShow = false
    var touchHelper: RecyclerHelper<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_plan)

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
        binding!!.topbar.isBackShow = true
        binding!!.topbar.isMoreShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.edit_plan)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        editPlanAdapter = EditPlanAdapter(this)
        binding!!.rvWorkOuts.layoutManager = LinearLayoutManager(this)
        binding!!.rvWorkOuts.setAdapter(editPlanAdapter)

        editPlanAdapter!!.setEventListener(object : EditPlanAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                showChangeTimeDialog(editPlanAdapter!!.getItem(position), object : DialogInterface {
                    override fun dismiss() {
                        getExerciseData()
                        setResult(Activity.RESULT_OK)
                    }

                    override fun cancel() {

                    }

                })
            }

            override fun onReplaceClick(position: Int, view: View) {
                val i = Intent(this@EditPlanActivity, ReplaceExerciseActivity::class.java)
                i.putExtra("ExcData", Gson().toJson(editPlanAdapter!!.getItem(position)))
                startActivityForResult(i, 8989)
            }

        })


        fillData()
        getExerciseData()
    }

    private fun getExerciseData() {
        if (workoutPlanData!!.planDays == Constant.PlanDaysYes) {
            val dayId = intent.getStringExtra(Constant.extra_day_id)
            if (dayId != null) {
                //arrDayExTableClass = DataHelper(this).getDayExList(dayId)
            }
        } else {
            editPlanAdapter!!.addAll(DataHelper(this).getHomeDetailExList(workoutPlanData!!.planId!!))
        }

        touchHelper = RecyclerHelper<HomeExTableClass>(
            editPlanAdapter!!.data!!,
            editPlanAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )
        touchHelper!!.setRecyclerItemDragEnabled(true)
            .setOnDragItemListener(object : OnDragListener {
                override fun onDragItemListener(fromPosition: Int, toPosition: Int) {
                    editPlanAdapter!!.onChangePosition(fromPosition,toPosition)
                   isSaveDialogShow = true
                }
            })
        touchHelper!!.setRecyclerItemSwipeEnabled(true)
            .setOnSwipeItemListener(object : OnSwipeListener {
                override fun onSwipeItemListener() {

                }
            })
        val itemTouchHelper = ItemTouchHelper(touchHelper!!)
        itemTouchHelper.attachToRecyclerView(binding!!.rvWorkOuts)
    }

    private fun fillData() {
        if (workoutPlanData != null) {
            binding!!.tvWorkoutTime.text =
                workoutPlanData!!.planMinutes + " " + getString(R.string.mins)
            binding!!.tvTotalWorkout.text =
                workoutPlanData!!.planWorkouts + " " + getString(R.string.workouts)
        }
    }

    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
    }


    inner class ClickHandler {

        fun onSaveClick() {
            saveExercise()
        }

    }

    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
                if (isSaveDialogShow)
                    showSaveConfirmationDialog()
                else
                    finish()
            }

            if (value.equals(getString(R.string.more))) {
                val menu = PopupMenu(this@EditPlanActivity, view)

                menu.getMenu().add(getString(R.string.reset_plan))

                menu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {

                        if (item!!.title.equals(getString(R.string.reset_plan))) {
                            dbHelper.resetPlanExc(editPlanAdapter!!.data)
                            getExerciseData()
                            setResult(Activity.RESULT_OK)
                        }
                        return true
                    }

                })

                menu.gravity = Gravity.TOP
                menu.show()
            }

        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        if (isSaveDialogShow)
            showSaveConfirmationDialog()
        else
            super.onBackPressed()
    }

    private fun showSaveConfirmationDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.save_changes_que))
        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
            saveExercise()
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> finish() })
        builder.create().show()
    }

    private fun saveExercise() {
        for (i in 0..editPlanAdapter!!.data!!.size - 1) {
            val item = editPlanAdapter!!.data[i]
            item.planSort = (i + 1).toString()
            dbHelper.updatePlanEx(item)
        }
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 8989 && resultCode == Activity.RESULT_OK) {
            showToast("Exercise Replaced successfully")
            setResult(Activity.RESULT_OK)
            getExerciseData()
        }
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

    }

}
