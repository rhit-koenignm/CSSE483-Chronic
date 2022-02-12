package edu.rosehulman.chronic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import edu.rosehulman.chronic.utilities.Constants
import edu.rosehulman.chronic.utilities.NotificationUtilities

class RemindersReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(Constants.TAG,"Received Reminders Message to Broadcast Receiver, sending notificaiton with the message")
        NotificationUtilities.createAndLaunch(context,intent?.getStringExtra(NotificationUtilities.MESSAGE_KEY) ?:"")
    }

}
