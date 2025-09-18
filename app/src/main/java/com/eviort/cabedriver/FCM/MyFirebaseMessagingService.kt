package com.eviort.cabedriver.FCM

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var utils = Utilities()
    var soundUri: Uri? = null
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        SharedHelper.putKey(applicationContext, "device_token", "" + s)

        System.out.println("Token generated:" + s)

        Utilities.printAPI_Response(s)
    }

    //    Called when a message is received.
    //    This is also called when a notification message is received while the app is in the foreground.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Utilities.print(TAG, "MyFirebaseMsgService From: " + remoteMessage.from)
        //        if (remoteMessage.getData() != null && !Utilities.onMap) {
/*        if (remoteMessage.getData() != null) {
            utils.print(TAG, " MyFirebaseMsgService From: " + remoteMessage.getFrom());
            utils.print(TAG, "MyFirebaseMsgService Notification Message Body: " + remoteMessage.getData());
            System.out.println("MyFirebaseMsgService Notification Message Body: " + remoteMessage.getData());
            //Calling method to generate notification
//            sendNotification(remoteMessage.getData().get("body"));
            if (remoteMessage.getData().get("sound").replace(" ", "").equals("default")) {
                Utilities.clearSound = true;
                utils.print(TAG, "default " + remoteMessage.getData());
             //   senddefaultNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"), remoteMessage.getData().get("sound"));
            } else {
                Utilities.clearSound = true;
                utils.print(TAG, "vale " + remoteMessage.getData());
              //  sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"), remoteMessage.getData().get("sound"));
            }
        } else {
            Utilities.clearSound = false;
            utils.print(TAG, "MyFirebaseMsgService FCM Notification failed");
        }*/
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private fun sendNotification(messageBody: String, title: String, sound: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("push", true)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.alert_tonee)
        val mBuilder = NotificationCompat.Builder(this, "channelid")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setSound(null)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
        /*   Utilities.r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
        Utilities.r.play();*/
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel("channelid", "NOTIFICATION_CHANNEL_NAME", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()
            notificationChannel.setSound(soundUri, audioAttributes)
            //            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setUsage(AudioAttributes.USAGE_ALARM)
//                    .build();
//            notificationChannel.setSound(soundUri, audioAttributes);
            mBuilder.setChannelId("channelid")
            assert(mNotificationManager != null)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager.notify(System.currentTimeMillis().toInt(),
                mBuilder.build())
        Utilities.clearSound = true
    }

    private fun senddefaultNotification(messageBody: String, title: String, sound: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("push", true)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder = NotificationCompat.Builder(this, "channelid")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title) //                .setSound(soundUri)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
        Utilities.r = RingtoneManager.getRingtone(applicationContext, soundUri)
        Utilities.r.play()
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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
    }

    private fun getNotificationIcon(notificationBuilder: NotificationCompat.Builder): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = ContextCompat.getColor(applicationContext, R.color.red)
            R.drawable.call
        } else {
            R.drawable.call
        }
    } //FOR FUTURE REFERENCE

    //    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    //
    //    private NotificationUtils notificationUtils;
    //
    //    @Override
    //    public void onMessageReceived(RemoteMessage remoteMessage) {
    //        utils.print(TAG, "From: " + remoteMessage.getFrom());
    //
    //        if (remoteMessage == null)
    //            return;
    //
    //        // Check if message contains a notification payload.
    //        if (remoteMessage.getNotification() != null) {
    //            utils.print(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
    //            handleNotification(remoteMessage.getNotification().getBody());
    //        }
    //
    //        // Check if message contains a data payload.
    //        if (remoteMessage.getData().size() > 0) {
    //            utils.print(TAG, "Data Payload: " + remoteMessage.getData().toString());
    //
    //            try {
    //                JSONObject json = new JSONObject(remoteMessage.getData().toString());
    //                handleDataMessage(json);
    //            } catch (Exception e) {
    //                utils.print(TAG, "Exception: " + e.getMessage());
    //            }
    //        }
    //    }
    //
    //    private void handleNotification(String message) {
    //        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
    //            // app is in foreground, broadcast the push message
    //            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
    //            pushNotification.putExtra("message", message);
    //            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    //
    //            // play notification sound
    //            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
    //            notificationUtils.playNotificationSound();
    //        }else{
    //            // If the app is in background, firebase itself handles the notification
    //        }
    //    }
    //
    //    private void handleDataMessage(JSONObject json) {
    //        utils.print(TAG, "push json: " + json.toString());
    //
    //        try {
    //            JSONObject data = json.getJSONObject("data");
    //
    //            String title = data.getString("title");
    //            String message = data.getString("message");
    //            boolean isBackground = data.getBoolean("is_background");
    //            String imageUrl = data.getString("image");
    //            String timestamp = data.getString("timestamp");
    //            JSONObject payload = data.getJSONObject("payload");
    //
    //            utils.print(TAG, "title: " + title);
    //            utils.print(TAG, "message: " + message);
    //            utils.print(TAG, "isBackground: " + isBackground);
    //            utils.print(TAG, "payload: " + payload.toString());
    //            utils.print(TAG, "imageUrl: " + imageUrl);
    //            utils.print(TAG, "timestamp: " + timestamp);
    //
    //
    //            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
    //                // app is in foreground, broadcast the push message
    //                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
    //                pushNotification.putExtra("message", message);
    //                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    //
    //                // play notification sound
    //                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
    //                notificationUtils.playNotificationSound();
    //            } else {
    //                // app is in background, show the notification in notification tray
    //                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
    //                resultIntent.putExtra("message", message);
    //
    //                // check for image attachment
    //                if (TextUtils.isEmpty(imageUrl)) {
    //                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
    //                } else {
    //                    // image is present, show notification with image
    //                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
    //                }
    //            }
    //        } catch (JSONException e) {
    //            utils.print(TAG, "Json Exception: " + e.getMessage());
    //        } catch (Exception e) {
    //            utils.print(TAG, "Exception: " + e.getMessage());
    //        }
    //    }
    //
    //    /**
    //     * Showing notification with text only
    //     */
    //    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
    //        notificationUtils = new NotificationUtils(context);
    //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    //    }
    //
    //    /**
    //     * Showing notification with text and image
    //     */
    //    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
    //        notificationUtils = new NotificationUtils(context);
    //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    //    }
    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}