package com.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.stretching.HomeActivity
import com.stretching.R
import com.stretching.utils.Constant
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    var mAlarmManager: AlarmManager? = null
    var mPendingIntent: PendingIntent? = null
    lateinit var dataBaseHelper: ReminderDatabase
    lateinit var reminderClass: Reminder

    override fun onReceive(context: Context, intent: Intent) {
        dataBaseHelper = ReminderDatabase(context)
        val id = intent.getStringExtra(Constant.EXTRA_REMINDER_ID)

        try {
            reminderClass = dataBaseHelper.getReminder(id!!.toInt())!!
            if (reminderClass.active == "true") {

                var arrOfDays = ArrayList<String>()
                if (reminderClass.days!!.contains(",")) {
                    arrOfDays = (reminderClass.days!!.split(",")) as ArrayList<String>
                } else {
                    arrOfDays.add(reminderClass.days!!)
                }

                for (i in 0 until arrOfDays.size) {
                    arrOfDays[i] = arrOfDays[i].replace("'", "")
                    Log.e("TAG", "onReceive:Arraydays:::::  "+arrOfDays[i] )
                }

                val dayNumber = getDayNumber(getCurrentDayName().toUpperCase())
                Log.e("TAG", "onReceive::::Day Number::::: $dayNumber")
                if (arrOfDays.contains(dayNumber)) {
                    fireNotification(context, reminderClass)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setAlarm(context: Context, calendar: Calendar, ID: Int) {
        mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Put Reminder ID in Intent Extra
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constant.EXTRA_REMINDER_ID, Integer.toString(ID))
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        Log.e("TAG", "setAlarm::::::::::SET $ID")
        // Calculate notification time
        val c = Calendar.getInstance()
        val currentTime = c.timeInMillis
        val diffTime = calendar.timeInMillis - currentTime

        // Start alarm using notification time
        mAlarmManager!![AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + diffTime] = mPendingIntent

        // Restart alarm if device is rebooted
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun setRepeatAlarm(context: Context, calendar: Calendar, ID: Int, RepeatTime: Long) {
        mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Put Reminder ID in Intent Extra
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constant.EXTRA_REMINDER_ID, Integer.toString(ID))
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        // Calculate notification timein
        val c = Calendar.getInstance()
        val currentTime = c.timeInMillis
        val diffTime = calendar.timeInMillis - currentTime

        // Start alarm using initial notification time and repeat interval time
        mAlarmManager!!.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + diffTime,
            RepeatTime, mPendingIntent
        )

        // Restart alarm if device is rebooted
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun cancelAlarm(context: Context, ID: Int) {
        mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, ID, Intent(context, AlarmReceiver::class.java), 0)
        mAlarmManager!!.cancel(mPendingIntent)

        // Disable alarm
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }




    private fun fireNotification(context: Context, reminderClass: Reminder) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = reminderClass.iD
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelName = context.resources.getString(R.string.app_name)
        val channelDescription = "Application_name Alert"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channelId.toString(), channelName, importance)
            mChannel.description = channelDescription
            mChannel.enableVibration(true)
            notificationManager.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(context, channelId.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_alert_sound)
            builder.color = ContextCompat.getColor(context,R.color.accent)
        } else {
            builder.setSmallIcon(R.drawable.ic_alert_sound)
        }

        builder.setStyle(NotificationCompat.BigTextStyle().bigText("Your body needs energy! You haven't exercised in ${getCurrentFullDayName()}!"))
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        builder.setContentTitle(context.resources.getString(R.string.app_name))
        builder.setAutoCancel(true)
        builder.setOngoing(false)

        val notificationIntent = Intent(context, HomeActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(context, 0,notificationIntent, 0)
        builder.setContentIntent(intent)

        notificationManager.notify(reminderClass.iD.toInt(), builder.build())

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(date: String): Date {
        val simpleDateFormat = SimpleDateFormat("dd MMM, yyyy")
        val resultDate = simpleDateFormat.parse(date);
        return resultDate
    }

    private fun isDateBetweenStartEndDate(max: Date, date: Date): Boolean {
        var isDateBetweenToDate = false;
        var currentDate = getCurrentDate()
        var maxDate = getEndDate(max)

        if (currentDate == maxDate) {
            isDateBetweenToDate = true
        } else if (date <= max) {
            isDateBetweenToDate = true
        }
        return isDateBetweenToDate

    }

    private fun getCurrentFullDayName(): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getCurrentDayName(): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getCurrentDate(): String {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getEndDate(date: Date): String {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun getDayNumber(dayName: String): String {
        var dayNumber = ""
        when (dayName) {
            "SUN" -> dayNumber = "1"
            "MON" -> dayNumber = "2"
            "TUE" -> dayNumber = "3"
            "WED" -> dayNumber = "4"
            "THU" -> dayNumber = "5"
            "FRI" -> dayNumber = "6"
            "SAT" -> dayNumber = "7"
        }
        return dayNumber
    }
}