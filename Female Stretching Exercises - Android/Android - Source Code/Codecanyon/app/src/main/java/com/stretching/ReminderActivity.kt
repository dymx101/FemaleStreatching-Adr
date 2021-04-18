package com.stretching

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.reminderNew.AlarmReceiver
import com.reminderNew.Reminder
import com.reminderNew.ReminderDatabase
import com.stretching.adapter.ReminderAdapter
import com.stretching.databinding.ActivityReminderBinding
import com.stretching.interfaces.CallbackListener
import com.stretching.interfaces.DateEventListener
import com.stretching.interfaces.TopBarClickListener
import com.stretching.objects.ReminderTableClass
import com.stretching.utils.AdUtils
import com.stretching.utils.Constant
import com.stretching.utils.Utils
import java.util.*


class ReminderActivity : BaseActivity(), CallbackListener {

    var binding: ActivityReminderBinding? = null
    var reminderAdapter: ReminderAdapter? = null
    private var arrReminder = ArrayList<ReminderTableClass>()


    private var listReminder: ArrayList<Reminder>? = null
    private val mTitle: String? = ""
    private var mTime: String? = ""
    private var mDate: String? = ""
    private var mRepeatNo: String? = ""
    private val mRepeatType: String? = "Day"
    private val mActive: String? = "true"
    private val mRepeat: String? = "false"
    private var mDateSplit: Array<String>? = null
    private var mTimeSplit: Array<String>? = null
    private var mReceivedID = 0
    private var mYear = 0
    private var mMonth: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var mDay: Int = 0
    private var mcalendarCurrent: Calendar? = null

    private var mRepeatTime: Long = 0
    private var mCalendar: Calendar? = null
    private val mReceivedReminder: Reminder? = null
    private var rb: ReminderDatabase? = null
    private var mAlarmReceiver: AlarmReceiver? = null

    // Constant values in milliseconds
    private val milMinute = 60000L
    private val milHour = 3600000L
    private val milDay = 86400000L
    private val milWeek = 604800000L
    private val milMonth = 2592000000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder)

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
        initReminder()
    }

    private fun initReminder() {
        // Obtain Date and Time details

        // Obtain Date and Time details
        mcalendarCurrent = Calendar.getInstance()
        rb = ReminderDatabase(this)
        mRepeatNo = 1.toString()
        mCalendar = Calendar.getInstance()
        mAlarmReceiver = AlarmReceiver()


        mHour = mCalendar!!.get(Calendar.HOUR_OF_DAY)
        mMinute = mCalendar!!.get(Calendar.MINUTE)
        mYear = mCalendar!!.get(Calendar.YEAR)
        mMonth = mCalendar!!.get(Calendar.MONTH) + 1
        mDay = mCalendar!!.get(Calendar.DATE)

        mDate = "$mDay/$mMonth/$mYear"
        mTime = "$mHour:$mMinute"
        mDateSplit = mDate!!.split("/".toRegex()).toTypedArray()
        mTimeSplit = mTime!!.split(":".toRegex()).toTypedArray()

        mDay = mDateSplit!!.get(0).toInt()
        mMonth = mDateSplit!!.get(1).toInt()
        mYear = mDateSplit!!.get(2).toInt()
        mHour = mTimeSplit!!.get(0).toInt()
        mMinute = mTimeSplit!!.get(1).toInt()

    }

    private fun initIntentParam() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("from")) {
                    val from = intent.getStringExtra("from")
                    if (from.equals(Constant.FROM_SETTING)) {
                        initDrawerMenu(false)
                        initBack()
                        binding!!.topbar.isBackShow = true
                        return
                    }
                }
            }
            initDrawerMenu(true)
            binding!!.topbar.isMenuShow = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding!!.topbar.tvTitleText.text = getString(R.string.menu_reminder)
        binding!!.topbar.topBarClickListener = TopClickListener()
        binding!!.handler = ClickHandler()

        reminderAdapter = ReminderAdapter(this)
        binding!!.rvRecent.layoutManager = LinearLayoutManager(this)
        binding!!.rvRecent.setAdapter(reminderAdapter)

        reminderAdapter!!.setEventListener(object : ReminderAdapter.EventListener {

            override fun onRepeatClick(position: Int, view: View) {
                val item = reminderAdapter!!.getItem(position)
                showDaySelectionDialog(item, true)
            }

            override fun onTimeClick(position: Int, view: View, hour: Int, minute: Int) {
                val time = Utils.parseTime(
                    reminderAdapter!!.getItem(position)!!.time!!.toString(),
                    "hh:mm"
                )
                showTimePickerDialog(this@ReminderActivity, time,
                    object : DateEventListener {
                        override fun onDateSelected(date: Date, hourOfDay: Int, minute: Int) {

                            rb!!.updateReminderTime(
                                reminderAdapter!!.getItem(position)!!.iD.toString(),
                                "$hourOfDay:$minute"
                            )
                            Log.e(
                                "TAG",
                                "onDateSelected::::Time and hour::::  $hourOfDay $minute  $mHour  $mMinute"
                            )
                            init()
                            val mcalender2 = Calendar.getInstance()
                            mcalender2[Calendar.MONTH] = --mMonth
                            mcalender2[Calendar.YEAR] = mYear
                            if (mHour <= mcalendarCurrent!!.get(Calendar.HOUR_OF_DAY) && mMinute < mcalendarCurrent!!.get(
                                    Calendar.MINUTE
                                )
                            ) {
                                mDay += 1
                            }
                            mcalender2[Calendar.DAY_OF_MONTH] = mDay
                            mcalender2[Calendar.HOUR_OF_DAY] = hourOfDay
                            mcalender2[Calendar.MINUTE] = minute
                            mcalender2[Calendar.SECOND] = 0
                            mAlarmReceiver!!.setAlarm(
                                this@ReminderActivity,
                                mcalender2!!,
                                reminderAdapter!!.getItem(position)!!.iD
                            )


//                        dbHelper.updateReminderTimes(reminderAdapter!!.getItem(position)!!.iD.toString(), Utils.parseTime(date.time, "HH:mm"))
                            fillData()
//                        setResult(Activity.RESULT_OK)
//                        Utils.startAlarm(reminderAdapter!!.getItem(position)!!.iD.toString().toInt(), hourOfDay, minute, this@ReminderActivity)
                        }
                    }
                    , hour, minute)
            }

            override fun onDeleteClick(position: Int, view: View) {
                confirmDeleteReminder(
                    this@ReminderActivity,
                    getString(R.string.tip),
                    getString(R.string.confirm_delete),
                    position
                )
            }

            override fun onSwitchChecked(position: Int, isChecked: Boolean, view: View) {
                /* if (isChecked) {
                     dbHelper.updateReminder(
                         reminderAdapter!!.getItem(position).iD.toString(),
                         "true"
                     )
                 } else {
                     dbHelper.updateReminder(
                         reminderAdapter!!.getItem(position).iD.toString(),
                         "false"
                     )
                 }*/

                try {
                    val rb = ReminderDatabase(this@ReminderActivity)
                    if (isChecked) {
                        rb.updateReminderActive(
                            reminderAdapter!!.getItem(position).iD.toString(),
                            "true"
                        )
                    } else {
                        rb.updateReminderActive(
                            reminderAdapter!!.getItem(position).iD.toString(),
                            "false"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })


        fillData()

    }


    private fun fillData() {
        /* try {
             arrReminder = dbHelper.getRemindersList()
             reminderAdapter!!.addAll(arrReminder)
         } catch (e: Exception) {
             e.printStackTrace()
         }*/

        try {
            val rb = ReminderDatabase(this)
            listReminder = rb.allReminders
            reminderAdapter!!.addAll(listReminder!!)
            Log.e("TAG", "init:All Reminder::::::  " + Gson().toJson(listReminder))
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun confirmDeleteReminder(
        content: Context,
        strTitle: String,
        strMsg: String,
        adapterPosition: Int
    ): Boolean {

        val builder = AlertDialog.Builder(content)
        builder.setTitle(strTitle)
        builder.setMessage(strMsg)
        builder.setCancelable(true)

        builder.setPositiveButton(R.string.btn_ok) { dialog, id ->
            try {
                val rb = ReminderDatabase(this)
                rb.deleteReminder(reminderAdapter!!.getItem(adapterPosition).iD.toString())
                reminderAdapter!!.removeAt(adapterPosition)
                mAlarmReceiver!!.cancelAlarm(this, reminderAdapter!!.getItem(adapterPosition).iD)
                dialog.cancel()
                setResult(Activity.RESULT_OK)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        builder.setNegativeButton(R.string.cancel) { dialog, id ->
            try {
                dialog.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()

        return false
    }

    override fun onResume() {
        openInternetDialog(this)
        super.onResume()
        changeSelection(4)
    }


    inner class ClickHandler {

        fun onAddReminderClick() {
            initReminder()
            showTimePickerDialog(this@ReminderActivity, Date(),
                object : DateEventListener {
                    override fun onDateSelected(date: Date, hourOfDay: Int, minute: Int) {
                        showDaySelectionDialog(null, false)
                        mHour = hourOfDay
                        mMinute = minute
                        mTime = if (minute < 10) {
                            "$hourOfDay:0$minute"
                        } else {
                            "$hourOfDay:$minute"
                        }
                    }
                }
                , mHour, mMinute)
        }
    }

    private fun showDaySelectionDialog(item: Reminder?, isFromEdit: Boolean) {

        val daysList = arrayOf<CharSequence>(
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
        )
        var booleanArray = booleanArrayOf(true, true, true, true, true, true, true)
        var arrayList = arrayListOf<String>("1", "2", "3", "4", "5", "6", "7")

        if (isFromEdit) {
            val strDays = item!!.days!!.split(",").sorted()
            val arrayListBoolean = arrayListOf<Boolean>()
            for (i in 1 until 8) {
                if (strDays.contains(i.toString())) {
                    arrayListBoolean.add(true)
                } else {
                    arrayList.remove(i.toString())
                    arrayListBoolean.add(false)
                }
            }

            booleanArray = arrayListBoolean.toBooleanArray()
        }

        val builder = AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
        builder.setTitle(R.string.repeat)
        builder.setMultiChoiceItems(daysList, booleanArray) { _, which, isChecked ->
            if (isChecked && arrayList.contains((which + 1).toString()).not()) {
                arrayList.add((which + 1).toString())
            } else {
                arrayList.remove((which + 1).toString())
            }
        }

        builder.setPositiveButton(R.string.btn_ok) { dialog, which ->

            if (isFromEdit) {
                rb!!.updateReminderDays(item!!.iD.toString(), getDayInString(arrayList))
                init()
            } else {

                if (arrayList.size == 0) {
                    Toast.makeText(this, "Please Select at least one day", Toast.LENGTH_LONG)
                        .show()
                } else {

                    val ID = rb!!.addReminder(
                        Reminder(
                            mTitle, mDate, mTime, mRepeat, mRepeatNo, mRepeatType,
                            mActive, getDayInString(arrayList)
                        )
                    )

                    mCalendar!![Calendar.MONTH] = --mMonth
                    mCalendar!![Calendar.YEAR] = mYear
                    if (mHour <= mcalendarCurrent!!.get(Calendar.HOUR_OF_DAY) && mMinute < mcalendarCurrent!!.get(
                            Calendar.MINUTE
                        )
                    ) {
                        mDay += 1
                    }
                    mCalendar!![Calendar.DAY_OF_MONTH] = mDay
                    mCalendar!![Calendar.HOUR_OF_DAY] = mHour
                    mCalendar!![Calendar.MINUTE] = mMinute
                    mCalendar!![Calendar.SECOND] = 0

                    when (mRepeatType) {
                        "Minute" -> {
                            mRepeatTime = mRepeatNo!!.toInt() * milMinute
                        }
                        "Hour" -> {
                            mRepeatTime = mRepeatNo!!.toInt() * milHour
                        }
                        "Day" -> {
                            mRepeatTime = mRepeatNo!!.toInt() * milDay
                        }
                        "Week" -> {
                            mRepeatTime = mRepeatNo!!.toInt() * milWeek
                        }
                        "Month" -> {
                            mRepeatTime = mRepeatNo!!.toInt() * milMonth
                        }
                    }

                    AlarmReceiver().setAlarm(applicationContext, mCalendar!!, ID)


                    Utils.setPref(this, Constant.PREF_IS_REMINDER_SET, true)
                }
                fillData()
                setResult(Activity.RESULT_OK)
                dialog.dismiss()
            }
        }
        builder.setNegativeButton(R.string.btn_cancel) { dialog, which -> dialog.dismiss() }
        builder.create().show()

    }

    private lateinit var stringBuilderDay: StringBuilder
    private fun getDayInString(arrOfDays: ArrayList<String>): String {

        stringBuilderDay = StringBuilder()
        for (i in 0 until arrOfDays.size) {
            if (stringBuilderDay.isEmpty()) {
                stringBuilderDay.append(arrOfDays[i])
            } else {
                stringBuilderDay.append(',')
                stringBuilderDay.append(arrOfDays[i])
            }
        }

        return stringBuilderDay.toString()
    }

    inner class TopClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getActivity(), view!!)

            if (value.equals(getString(R.string.back))) {
                finish()
                setResult(Activity.RESULT_OK)
            }

        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

    }

}
