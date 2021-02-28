package com.stretching

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.stretching.adapter.ReminderAdapter
import com.stretching.databinding.ActivityHealthDataBinding
import com.stretching.databinding.ActivityMetricImperialUnitsBinding
import com.stretching.databinding.ActivityReminderBinding
import com.stretching.interfaces.DateEventListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import java.util.*


class MetricImperialUnitsActivity : BaseActivity() {

    var binding: ActivityMetricImperialUnitsBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_metric_imperial_units)

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
        binding!!.topbar.tvTitleText.text = getString(R.string.metric_and_imperial_units)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        binding!!.tvHeightUnit.text = Utils.getPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_CM)
        binding!!.tvWeightUnit.text = Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)

    }




    override fun onResume() {
        super.onResume()
    }


    inner class ClickHandler {

        fun onWeightUnitClick(){
            showWeightUnitDialog()
        }

        fun onHeightUnitClick(){
            showHeightUnitDialog()
        }
    }

    private fun showWeightUnitDialog() {

        val weightUnits = arrayOf<CharSequence>("Kg", "Lbs")

        var checkedItem = 0
        if ( Utils.getPref(this, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG).equals(Constant.DEF_LB)) {
            checkedItem = 1
        }

        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle(R.string.weight_unit)
        builder.setSingleChoiceItems(weightUnits,checkedItem,object :DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                if (which == 0) {
                    Utils.setPref(this@MetricImperialUnitsActivity, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)
                } else {
                    Utils.setPref(
                        this@MetricImperialUnitsActivity,
                        Constant.PREF_WEIGHT_UNIT,
                        Constant.DEF_LB
                    )
                }

                binding!!.tvWeightUnit.text = Utils.getPref(this@MetricImperialUnitsActivity, Constant.PREF_WEIGHT_UNIT, Constant.DEF_KG)
                if (dialog != null) {
                    dialog.dismiss()
                }
            }

        })

        builder.create().show()
    }

    private fun showHeightUnitDialog() {

        val heightUnits = arrayOf<CharSequence>("Cm", "In")

        var checkedItem = 0
        if ( Utils.getPref(this, Constant.PREF_HEIGHT_UNIT, Constant.DEF_CM).equals(Constant.DEF_IN)) {
            checkedItem = 1
        }

        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle(R.string.height_unit)
        builder.setSingleChoiceItems(heightUnits,checkedItem,object :DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (which == 0) {
                    Utils.setPref(this@MetricImperialUnitsActivity, Constant.PREF_HEIGHT_UNIT, Constant.DEF_CM)
                } else {
                    Utils.setPref(this@MetricImperialUnitsActivity, Constant.PREF_HEIGHT_UNIT, Constant.DEF_IN)
                }
                binding!!.tvHeightUnit.text = Utils.getPref(this@MetricImperialUnitsActivity, Constant.PREF_HEIGHT_UNIT, Constant.DEF_CM)
                if (dialog != null) {
                    dialog.dismiss()
                }
            }

        })

        builder.create().show()
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
