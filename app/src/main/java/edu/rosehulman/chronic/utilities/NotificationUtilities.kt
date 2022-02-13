package edu.rosehulman.chronic.utilities

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import edu.rosehulman.chronic.MainActivity
import edu.rosehulman.chronic.R


object NotificationUtilities {
    private const val NOTIFICATION_ID = 1
    private const val channelId = "AlarmNotifierChannelId"
    private const val channelName = "AlarmNotifierChannel"
    const val MESSAGE_KEY = "message"


    fun createChannel(context: Context) {
        // Required
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            // Options
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_SECRET
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            vibrationPattern = longArrayOf(50, 25, 100, 50)
            description = "Alarm notification channel"
        }

        // Required
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    fun createAndLaunch(context: Context, data: String) {
        Log.d(Constants.TAG,"Creating and Launching Notification with $data")
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        // DONE 4 Create an intent and pending intent and set it in the notification so that it can launch the app.
        val contentIntent = Intent(context, MainActivity::class.java).also {
            it.putExtra(NotificationUtilities.MESSAGE_KEY, data)
        }//This tells the intent where to go, and adds an additional payload from the broadcast receiver to send to main activity
        val contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID,contentIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        // DONE 2 Create a basic notification with at least title, text,
        //  small icon, and high priority. Use the data passed in as part of the text.
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Chronic:")
            .setContentText("$data Alarm")
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)



        // DONE 3 Actually send the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun getLongText() =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
}