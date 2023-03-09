package com.saaei12092021.office.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.saaei12092021.office.R
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant.CHANNEL_1_ID
import java.util.*

class FirebaseMessageReceiver : FirebaseMessagingService(){

    private var notificationSound1: Uri? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("info", remoteMessage.data["info"])

        //  intent.action = java.lang.Long.toString(System.currentTimeMillis())

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, Random().nextInt() /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //    createNotificationsChannels()

        try {
            notificationSound1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        NotificationManagerCompat.from(applicationContext).cancelAll()
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val notification: Notification =
            NotificationCompat.Builder(applicationContext, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.splash_logo)
                .setContentTitle(remoteMessage.notification!!.title)
                .setContentText(remoteMessage.notification!!.body)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(notificationSound1)
                .build()

        notificationManager.notify(1, notification)

        Log.d("remoteMessage_", remoteMessage.data.toString())

        Constant.editor(this).apply {
            putString(Constant.FCM_INFO, remoteMessage.data["info"])
        }

        Log.d("getFcmInfo_", remoteMessage.data["info"].toString())

    }

    override fun onNewToken(fcmToken: String) {
        super.onNewToken(fcmToken)
    }

    private fun createNotificationsChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                "channel1",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "this is channel 1"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
        }
    }
}