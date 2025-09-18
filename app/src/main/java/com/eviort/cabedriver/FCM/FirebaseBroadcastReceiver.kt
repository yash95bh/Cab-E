package com.eviort.cabedriver.FCM


import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.legacy.content.WakefulBroadcastReceiver
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTUtilites.Utilities
import com.google.firebase.messaging.RemoteMessage
import com.eviort.cabedriver.R

class FirebaseBroadcastReceiver : WakefulBroadcastReceiver() {

    val TAG: String = FirebaseBroadcastReceiver::class.java.simpleName
    var utils = Utilities()
    var soundUri: Uri? = null
    var timer_intent: Intent? = null


    var mNotification: Notification? = null
    override fun onReceive(context: Context, intent: Intent) {

        val dataBundle = intent.extras
        if (dataBundle != null)
            for (key in dataBundle.keySet()) {
                Log.d(TAG, "dataBundle: " + key + " : " + dataBundle.get(key))
            }
        val remoteMessage = RemoteMessage(dataBundle!!)
        //Calling method to generate notification
//            sendNotification(remoteMessage.getData().get("body"));
        if (remoteMessage.data["sound"]!!.replace(" ", "") == "default") {
            Utilities.clearSound = true
            Utilities.print(TAG, "default " + remoteMessage.data)
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("push", true)

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, "channelid")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(remoteMessage.getData().get("title")) //
                    .setAutoCancel(true)//            .setSound(soundUri)
                    .setContentText(remoteMessage.getData().get("body"))
                    .setContentIntent(pendingIntent)
            Utilities.r = RingtoneManager.getRingtone(context.applicationContext, soundUri)
            Utilities.r.play()

            val mNotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel("channelid", "NOTIFICATION_CHANNEL_NAME", importance)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                mBuilder.setChannelId("channelid")
                assert(mNotificationManager != null)
                mNotificationManager.createNotificationChannel(notificationChannel)
            }
            assert(mNotificationManager != null)
            mNotificationManager.notify(System.currentTimeMillis().toInt(),
                    mBuilder.build())
            Utilities.clearSound = true
        } else {
            Utilities.clearSound = true
            Utilities.print(TAG, "vale " + remoteMessage.data)
            if (!isAppOnForeground(context.applicationContext, "com.eviort.cabedriver")) {
                //check notificationManager is available
                val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        ?: return

                //check api level for getActiveNotifications()
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    //if your Build version is less than android 6.0
                    //we can remove all notifications instead.
                    //notificationManager.cancelAll();
                    return
                } else {

                }

                //check there are notifications
                val activeNotifications = notificationManager.activeNotifications ?: return

                //remove all notification created by library(super.handleIntent(intent))
                for (tmp in activeNotifications) {
                    Utilities.print("FCM StatusBarNotification",
                            "tag/id: " + tmp.tag + " / " + tmp.id)
                    val tag = tmp.tag
                    val id = tmp.id

                    //trace the library source code, follow the rule to remove it.
                    if (tag != null && tag.contains("FCM-Notification")) notificationManager.cancel(tag, id)
                }

            } else {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra("push", true)

                val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + "com.eviort.cabedriver" + "/" + R.raw.alert_tonee)
                val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, "channelid")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(remoteMessage.data["title"].toString())
                        .setAutoCancel(true)
                        .setContentText(remoteMessage.data["body"].toString())
                        .setContentIntent(pendingIntent)

                Utilities.r = RingtoneManager.getRingtone(context.applicationContext, soundUri)
                Utilities.r.play()

                val mNotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val notificationChannel = NotificationChannel("channelid", "NOTIFICATION_CHANNEL_NAME", importance)
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.enableVibration(true)
                    val audioAttributes = AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    notificationChannel.setSound(soundUri, audioAttributes)

                    notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

                    mBuilder.setChannelId("channelid")
                    assert(mNotificationManager != null)
                    mNotificationManager.createNotificationChannel(notificationChannel)
                }

                assert(mNotificationManager != null)

                mNotificationManager.notify(System.currentTimeMillis().toInt(),
                        mBuilder.build())
                Utilities.clearSound = true
            }
        }
    }

    public fun isAppOnForeground(context: Context, appPackageName: String): Boolean {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == appPackageName) {
                //                Log.e("app",appPackageName);
                return true
            }
        }
        return false
    }

}