package com.stretching

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.stretching.databinding.ActivitySetYourWeeklyGoalBinding
import com.stretching.databinding.DialogGoalDayPickerBinding
import com.stretching.interfaces.CallbackListener
import com.stretching.utils.Constant
import com.stretching.utils.Utils


class SetYourWeeklyGoalActivity : BaseActivity(), CallbackListener {
    val TAG = SetYourWeeklyGoalActivity::class.java.name + Constant.ARROW

    var binding: ActivitySetYourWeeklyGoalBinding? = null

    var firstDayOfWeek = 1
    var weeklyTrainingDays = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_your_weekly_goal)
        init()
    }


    private fun init() {

        if (Utils.getPref(this, Constant.PREF_IS_WEEK_GOAL_DAYS_SET, false)) {
            firstDayOfWeek = Utils.getPref(this, Constant.PREF_FIRST_DAY_OF_WEEK, 1)
            weeklyTrainingDays = Utils.getPref(this, Constant.PREF_WEEK_GOAL_DAYS, 7)

            binding!!.tvTrainingDay.setText("$weeklyTrainingDays Days")
            binding!!.tvFirstDayOfWeek.setText(Utils.getFirstWeekDayNameByDayNo(firstDayOfWeek))
        }

        binding!!.llFirstDayOfWeek.setOnClickListener {
            showFirstDayOfPicker()
        }

        binding!!.llTrainingDay.setOnClickListener {
            showGoalDayPicker()
        }

        binding!!.btnSave.setOnClickListener {

            Utils.setPref(this, Constant.PREF_FIRST_DAY_OF_WEEK, firstDayOfWeek)
            Utils.setPref(this, Constant.PREF_WEEK_GOAL_DAYS, weeklyTrainingDays)
            Utils.setPref(this, Constant.PREF_IS_WEEK_GOAL_DAYS_SET, true)
            setResult(Activity.RESULT_OK)
            /*if (Utils.getPref(this,Constant.PREF_IS_REMINDER_SET,false)) {
                val i = Intent(this, SetReminderBoxActivity::class.java)
                startActivity(i)
            }*/
            finish()
        }

        binding!!.imgBack.setOnClickListener {
            finish()
        }

    }

    var goalDayPickerDialog: AlertDialog? = null
    private fun showGoalDayPicker() {
        try {
            var dialogGoalDayPickerBinding =
                DataBindingUtil.inflate<DialogGoalDayPickerBinding>(
                    getLayoutInflater(),
                    R.layout.dialog_goal_day_picker, null, false
                )
            val pd: AlertDialog.Builder =
                AlertDialog.Builder((this), R.style.MyAlertDialogStyle)
            pd.setView(dialogGoalDayPickerBinding.root)
            pd.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                goalDayPickerDialog!!.dismiss()
            }
            pd.setPositiveButton(getString(R.string.btn_ok)) { dialog, which ->
                weeklyTrainingDays = dialogGoalDayPickerBinding.npDay.value
                binding!!.tvTrainingDay.setText("${dialogGoalDayPickerBinding.npDay.value} Days")
                goalDayPickerDialog!!.dismiss()
            }

            dialogGoalDayPickerBinding.npDay.setScrollerEnabled(true)
            dialogGoalDayPickerBinding.npDay.value = weeklyTrainingDays
// Set wrap selector wheel
            dialogGoalDayPickerBinding.npDay.setWrapSelectorWheel(false)

            goalDayPickerDialog = pd.create()

            goalDayPickerDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var firstDayOfWeekPickerDialog: AlertDialog? = null
    private fun showFirstDayOfPicker() {
        try {
            val data = arrayOf("Sunday", "Monday", "Saturday")

            var dialogGoalDayPickerBinding =
                DataBindingUtil.inflate<DialogGoalDayPickerBinding>(
                    getLayoutInflater(),
                    R.layout.dialog_goal_day_picker, null, false
                )
            val pd: AlertDialog.Builder =
                AlertDialog.Builder((this), R.style.MyAlertDialogStyle)
            pd.setView(dialogGoalDayPickerBinding.root)
            pd.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                firstDayOfWeekPickerDialog!!.dismiss()
            }
            pd.setPositiveButton(getString(R.string.btn_ok)) { dialog, which ->
                binding!!.tvFirstDayOfWeek.setText(data[dialogGoalDayPickerBinding.npDay.value])
                firstDayOfWeek =
                    Utils.getFirstWeekDayNoByDayName(data[dialogGoalDayPickerBinding.npDay.value])
                firstDayOfWeekPickerDialog!!.dismiss()
            }

            dialogGoalDayPickerBinding.npDay.wrapSelectorWheel = false
            dialogGoalDayPickerBinding.npDay.setMinValue(0)
            dialogGoalDayPickerBinding.npDay.setMaxValue(data.size - 1)
            dialogGoalDayPickerBinding.npDay.setDisplayedValues(data)
            dialogGoalDayPickerBinding.npDay.setValue(firstDayOfWeek-1)

            // Set scroller enabled
            dialogGoalDayPickerBinding.npDay.setScrollerEnabled(true)

// Set wrap selector wheel
            dialogGoalDayPickerBinding.npDay.setWrapSelectorWheel(false)

            firstDayOfWeekPickerDialog = pd.create()
            firstDayOfWeekPickerDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        openInternetDialog(this)
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

    }

}
