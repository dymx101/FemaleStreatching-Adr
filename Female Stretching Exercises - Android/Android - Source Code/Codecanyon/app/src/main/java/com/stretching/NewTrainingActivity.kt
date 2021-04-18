package com.stretching

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.swipedragrecyclerview.OnDragListener
import com.common.swipedragrecyclerview.OnSwipeListener
import com.common.swipedragrecyclerview.RecyclerHelper
import com.common.view.CEditTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stretching.adapter.NewTrainingAdapter
import com.stretching.databinding.ActivityNewTrainingBinding
import com.stretching.interfaces.CallbackListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.objects.HomePlanTableClass
import com.stretching.objects.MyTrainingCatExTableClass
import com.stretching.objects.MyTrainingCategoryTableClass
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils


class NewTrainingActivity : BaseActivity(), CallbackListener {

    var binding: ActivityNewTrainingBinding? = null
    var newTrainingAdapter: NewTrainingAdapter? = null
    var workoutPlanData: MyTrainingCatExTableClass? = null
    var categoryData: MyTrainingCategoryTableClass? = null
    lateinit var touchHelper: RecyclerHelper<*>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_training)

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
                    workoutPlanData = Gson().fromJson(
                        str,
                        object : TypeToken<MyTrainingCatExTableClass>() {}.type
                    )!!
                }
                if (intent.extras!!.containsKey("categoryDetail")) {
                    val str = intent.getStringExtra("categoryDetail")
                    categoryData = Gson().fromJson(
                        str,
                        object : TypeToken<MyTrainingCategoryTableClass>() {}.type
                    )!!
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.isBackShow = true
        binding!!.topbar.isSaveShow = true
        binding!!.topbar.tvTitleText.text = getString(R.string.new_training)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        newTrainingAdapter = NewTrainingAdapter(this)
        binding!!.rvExercise.layoutManager = LinearLayoutManager(this)
        binding!!.rvExercise.setAdapter(newTrainingAdapter)

        newTrainingAdapter!!.setEventListener(object : NewTrainingAdapter.EventListener {
            override fun onItemClick(position: Int, view: View) {
                val item = newTrainingAdapter!!.getItem(position)
                val i = Intent(this@NewTrainingActivity, AddExerciseDetailActivity::class.java)
                i.putExtra("ExDetail", Gson().toJson(item))
                i.putExtra("pos", position)
                i.putExtra("categoryDetail", Gson().toJson(categoryData))
                startActivityForResult(i, 7475)

            }

            override fun onCloseClick(position: Int, view: View) {
                newTrainingAdapter!!.removeAt(position)
            }
        })

        newTrainingAdapter!!.add(workoutPlanData!!)

        fillData()

    }


    private fun fillData() {
        if (workoutPlanData != null) {
            touchHelper = RecyclerHelper<MyTrainingCatExTableClass>(
                newTrainingAdapter!!.data as java.util.ArrayList<MyTrainingCatExTableClass>,
                newTrainingAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )
            touchHelper.setRecyclerItemDragEnabled(true)
                .setOnDragItemListener(object : OnDragListener {
                    override fun onDragItemListener(fromPosition: Int, toPosition: Int) {
                        newTrainingAdapter!!.onChangePosition(fromPosition, toPosition)
                    }
                })

            touchHelper.setRecyclerItemSwipeEnabled(true)
                .setOnSwipeItemListener(object : OnSwipeListener {
                    override fun onSwipeItemListener() {

                    }
                })
            val itemTouchHelper = ItemTouchHelper(touchHelper)
            itemTouchHelper.attachToRecyclerView(binding!!.rvExercise)
        }
    }

    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
    }


    inner class ClickHandler {

        fun onAddNewClick() {
            val i = Intent(this@NewTrainingActivity, AddExerciseActivity::class.java)
            i.putExtra("from_new_training", true)
            startActivityForResult(i, 7489)
        }

    }

    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
                onBackPressed()
            }

            if (value.equals(getString(R.string.save))) {
                showTrainingPlanNameDialog()
            }

        }
    }

    private fun showTrainingPlanNameDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setTitle(R.string.please_name_your_plan)

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText = dialogLayout.findViewById<CEditTextView>(R.id.editText)
        builder.setView(dialogLayout)

        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
            if (editText.text.toString().isNullOrEmpty().not()) {
                addPlan(editText.text.toString())
                finish()
            }
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which ->
            dialog.dismiss()
        })
        builder.create().show()
    }

    override fun onBackPressed() {
        if (newTrainingAdapter?.itemCount?:0 > 0)
            showSaveConfirmationDialog()
        else
            finish()
    }

    private fun showSaveConfirmationDialog() {
        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.save_changes_que))
        builder.setPositiveButton(R.string.save) { dialog, which ->
            showTrainingPlanNameDialog()
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which ->
            dialog.dismiss()
            finish()
        })
        builder.create().show()
    }

    fun addPlan(planName: String) {
        val plan = HomePlanTableClass()

        plan.planName = planName
        plan.planProgress = "0"
        plan.planDays = "NO"
        plan.planType = Constant.PlanTypeMyTraining
        plan.planWorkouts = newTrainingAdapter!!.data.size.toString()
        plan.planMinutes = "0"
        plan.isPro = false
        plan.hasSubPlan = false
        plan.planThumbnail = Constant.MyTrainingThumbnail
        plan.parentPlanId = "0"
        plan.planTypeImage = Constant.MyTrainingTypeImage

        dbHelper.addMyTrainingPlan(
            plan,
            newTrainingAdapter!!.data as ArrayList<MyTrainingCatExTableClass>
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 7489 || requestCode == 7475) && resultCode == Activity.RESULT_OK) {
            try {
                if (data != null) {
                    if (data.extras!!.containsKey("workoutPlanData")) {
                        val str = data.getStringExtra("workoutPlanData")
                        workoutPlanData = Gson().fromJson(
                            str,
                            object : TypeToken<MyTrainingCatExTableClass>() {}.type
                        )!!
                    }
                    if (data.extras!!.containsKey("categoryDetail")) {
                        val str = data.getStringExtra("categoryDetail")
                        categoryData = Gson().fromJson(
                            str,
                            object : TypeToken<MyTrainingCategoryTableClass>() {}.type
                        )!!
                    }

                    if (data.extras!!.containsKey("pos")) {
                        val pos = data.getIntExtra("pos", -1)
                        newTrainingAdapter!!.update(pos, workoutPlanData!!)
                    } else {
                        newTrainingAdapter!!.add(workoutPlanData!!)
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
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
