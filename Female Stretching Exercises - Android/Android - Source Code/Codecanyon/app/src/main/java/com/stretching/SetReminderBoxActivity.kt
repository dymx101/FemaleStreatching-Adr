package com.stretching

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.stretching.adapter.WhatsYourGoalAdapter
import com.stretching.databinding.ActivitySetReminderBoxBinding
import com.stretching.objects.ReminderTableClass
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import com.utillity.db.DataHelper


class SetReminderBoxActivity : BaseActivity() {
    val TAG = SetReminderBoxActivity::class.java.name + Constant.ARROW

    var binding: ActivitySetReminderBoxBinding? = null
    var whatsYourGoalAdapter: WhatsYourGoalAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_reminder_box)
        init()
    }


    private fun init() {

        /*binding!!.npHour.setFadingEdgeEnabled(true)
        binding!!.npMinute.setFadingEdgeEnabled(true)*/

// Set scroller enabled
        binding!!.npHour.setScrollerEnabled(true)
        binding!!.npMinute.setScrollerEnabled(true)

// Set wrap selector wheel
        binding!!.npHour.setWrapSelectorWheel(true)
        binding!!.npMinute.setWrapSelectorWheel(true)

        binding!!.npHour.setTypeface(
            Typeface.createFromAsset(
            assets,
            "roboto-black.ttf"
        ))
        binding!!.npMinute.setTypeface(Typeface.createFromAsset(
            assets,
            "roboto-black.ttf"
        ))

        binding!!.btnFinished.setOnClickListener {

            showDaySelectionDialog(binding!!.npHour.value,binding!!.npMinute.value)
        }

    }

    private fun showDaySelectionDialog(hourOfDay: Int, minute: Int) {

        val daysList = arrayOf<CharSequence>("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        var booleanArray = booleanArrayOf(true,true,true,true,true,true,true)
        var arrayList = arrayListOf<String>("1", "2", "3", "4", "5", "6", "7")

        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setTitle(R.string.repeat)
        builder.setMultiChoiceItems(daysList, booleanArray,object : DialogInterface.OnMultiChoiceClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {
                if(isChecked && arrayList.contains((which+1).toString()).not()){
                    arrayList.add((which+1).toString())
                }else{
                    arrayList.remove((which+1).toString())
                }
            }
        })

        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->
            val reminderClass = ReminderTableClass()
            reminderClass.remindTime = "${hourOfDay}:${minute}"

            reminderClass.days = arrayList.joinToString(",")
            reminderClass.isActive = "true"

            val mCount = DataHelper(this).addReminder(reminderClass)

            Utils.startAlarm(mCount,hourOfDay,minute,this)
            dialog.dismiss()
            Utils.setPref(this,Constant.PREF_IS_REMINDER_SET,true)
            setResult(Activity.RESULT_OK)
            finish()
        }
        builder.setNegativeButton(R.string.btn_cancel, { dialog, which -> dialog.dismiss() })
        builder.create().show()

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
