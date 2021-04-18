package com.stretching.firebase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.StrictMode
import android.text.format.DateUtils
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.stretching.HomeActivity
import com.stretching.R
import com.stretching.utils.Constant
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var bitmap: Bitmap? = null

    override fun onNewToken(token: String) {
        Log.e("Token", "Refreshed token::::::::: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("TAG", "Dikirim dari: ${remoteMessage.from}")
        Log.e("===> notification recieved...${remoteMessage.data}","")
        Log.e("===> notification recieved...data ${remoteMessage.data}","")
        Log.e("===> notification recieved...notification ${remoteMessage.notification.toString()}","")
        Log.e("===> notification recieved...body ${remoteMessage.notification?.title}  ${remoteMessage.notification?.body}","")

//        addNotification(remoteMessage)

        generateNotification(remoteMessage)
    }

    private fun addNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notifyID = 1
        val channelId = "my_channel_01"// The id of the channel.
        val name: CharSequence = "my_channel_01"// The user-visible name of the channel.
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification: Notification = NotificationCompat.Builder(this)
                    .setContentTitle(remoteMessage.notification?.title)
                    .setContentText(remoteMessage.notification?.body)
                    .setSmallIcon(R.mipmap.ic_luncher_app_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setChannelId(channelId).build()
            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mChannel = NotificationChannel(channelId, name, importance)
            mNotificationManager.createNotificationChannel(mChannel)
            mNotificationManager.notify(notifyID, notification)
        } else {
            val notification =
                    NotificationCompat.Builder(this).setContentTitle(remoteMessage.notification?.title)
                            .setContentText(remoteMessage.notification?.body)
                            .setSmallIcon(R.mipmap.ic_luncher_app_icon)
                            .setContentIntent(pendingIntent).setAutoCancel(true).setChannelId(channelId).build()
            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(notifyID, notification)
        }
    }


    private fun generateNotification(p0: RemoteMessage) = try {

        println("===> notification recieved...data ${p0.data}")
        println("===> notification recieved...notification ${p0.notification.toString()}")
        println("===> notification recieved...body ${p0.notification?.body}")
        println("===> BOdy ${p0.notification?.title}")

        val context = this.applicationContext
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        mIntent = Intent(context, MainActivity::class.java)
        val mIntent: Intent

//        mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP


        val channelId = "11111"
        try {

            val channelName = context.resources.getString(R.string.app_name)
            val channelDescription = "Application_name Alert"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(channelId, channelName, importance)
                mChannel.description = channelDescription

                mChannel.setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                        mChannel.audioAttributes
                )
                notificationManager.createNotificationChannel(mChannel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val builder = NotificationCompat.Builder(this, channelId)

        val expandedView = RemoteViews(packageName, R.layout.item_notification_expand)
        val collapsedView = RemoteViews(packageName, R.layout.item_notification_coll)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_notifications_active)
            builder.color = ContextCompat.getColor(context, R.color.notification_theme)

            expandedView.setImageViewResource(R.id.big_icon, R.mipmap.ic_launcher)
            expandedView.setTextViewText(
                    R.id.timestamp,
                    DateUtils.formatDateTime(
                            this,
                            System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME
                    )
            )

            collapsedView.setImageViewResource(R.id.big_icon, R.mipmap.ic_launcher)
            collapsedView.setTextViewText(
                    R.id.timestamp,
                    DateUtils.formatDateTime(
                            this,
                            System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME
                    )
            )
        } else {
            builder.setSmallIcon(R.drawable.ic_notifications_active)

            expandedView.setImageViewResource(R.id.big_icon, R.mipmap.ic_launcher)
            expandedView.setTextViewText(
                    R.id.timestamp,
                    DateUtils.formatDateTime(
                            this,
                            System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME
                    )
            )

            collapsedView.setImageViewResource(R.id.big_icon, R.mipmap.ic_launcher)
            collapsedView.setTextViewText(
                    R.id.timestamp,
                    DateUtils.formatDateTime(
                            this,
                            System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME
                    )
            )

        }

        val title = context.resources.getString(R.string.app_name)

        val data = p0.notification
        if (data != null) {

            mIntent = Intent(context, HomeActivity::class.java)
            mIntent.putExtra("data", Gson().toJson(data))
            mIntent.putExtra("notificationData", Gson().toJson(p0.data))
            Log.e("TAG", "generateNotification:::INTENT:::::  ${Gson().toJson(p0.data)}  ")
            mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                expandedView.setTextViewText(R.id.title_text, data.title)

                expandedView.setTextViewText(R.id.notification_message, data.body)

                collapsedView.setTextViewText(R.id.content_text, data.body)

                collapsedView.setTextViewText(R.id.title_text, data.title)

                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)

                val image1Url = URL(data.imageUrl.toString())

                val bmp1 = BitmapFactory.decodeStream(image1Url.openConnection().getInputStream())
                if (bmp1 != null) {
                    builder.setStyle(
                            NotificationCompat.BigPictureStyle().bigPicture(bmp1)
                                    .setSummaryText(data.body)
                    )

                    expandedView.setBitmap(R.id.notification_img, "setImageBitmap", bmp1)
                }


            } catch (e: Exception) {
                e.printStackTrace()
                builder.setContentTitle(title)
            }
        } else {
            mIntent = Intent(context, HomeActivity::class.java)
            mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            builder.setContentText(title)
        }

        builder.setCustomContentView(collapsedView)
        builder.setCustomBigContentView(expandedView)
        builder.setShowWhen(false)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setAutoCancel(true)
        builder.setVisibility(NotificationCompat.VISIBILITY_SECRET)
        val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                mIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )


        builder.setContentIntent(pendingIntent)

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    } catch (e: Exception) {
        e.printStackTrace()
    }


}