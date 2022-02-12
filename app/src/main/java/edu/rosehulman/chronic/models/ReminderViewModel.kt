package edu.rosehulman.chronic.models

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.receivers.RemindersReceiver
import edu.rosehulman.chronic.utilities.Constants
import edu.rosehulman.chronic.utilities.NotificationUtilities
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


// Author: Natalie Koenig
// Description: The view model class to be used in the ReminderAdapter that will show up in the reminders page
// Date: 2/6/2022
class ReminderViewModel(private val app: Application) : AndroidViewModel(app) {

    //Variables for System Alarms
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val REQUEST_CODE = 1

    //The reminders will be stored in an arraylist
    var reminders = ArrayList<Reminder>()
    var currentPos = 0

    fun getReminderAt(pos: Int) = reminders[pos]
    fun getCurrentReminder() = reminders[currentPos]

    var uid = Firebase.auth.uid!!
    var ref = Firebase
        .firestore
        .collection(UserData.COLLECTION_PATH)
        .document(uid)
        .collection(Reminder.COLLECTION_PATH)

    var subscriptions = HashMap<String, ListenerRegistration>()

    fun addListener(fragmentName: String, observer: () -> Unit) {
        Log.d(Constants.TAG, "Adding reminder listener for fragment $fragmentName")

        val subscription = ref
            .orderBy("hours", Query.Direction.ASCENDING)
            .orderBy("minutes", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                error?.let {
                    Log.d(Constants.TAG, "Error: $error")
                    return@addSnapshotListener
                }
                Log.d(Constants.TAG, "In snaphot listener with ${snapshot?.size()}")
                reminders.clear()
                snapshot?.documents?.forEach {
                    reminders.add(Reminder.from(it))
                }
                observer()
            }
        subscriptions[fragmentName] = subscription
    }

    fun removeListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing listener for $fragmentName")
        subscriptions[fragmentName]?.remove()
        subscriptions.remove(fragmentName)
    }

    fun addReminder(reminder: Reminder?) {
        if (reminder == null) {
            var newReminder = Reminder()
            ref.add(newReminder)
        }
        else {
            ref.add(reminder.makeACopy())
        }
    }

    fun updateCurrentReminder(reminder: Reminder?) {
        if (reminder != null) {
            reminders[currentPos] = reminder
            ref.document(getCurrentReminder().id).set(getCurrentReminder())
        }
    }

    fun removeCurrentReminder() {
        ref.document(getCurrentReminder().id).delete()
        currentPos = 0
    }
    fun removeReminderAt(adapterPosition: Int) {
        ref.document(getReminderAt(adapterPosition).id).delete()
    }


    fun updatePos(pos: Int) {
        currentPos = pos
    }

    fun clear() {
        reminders.clear()
    }

    fun size() = reminders.size

    fun toggleCurrentReminder() {
        reminders[currentPos].toggleIsActive()
        ref.document(getCurrentReminder().id).set(getCurrentReminder())

        if(reminders[currentPos].isActive){
            enableReminder(reminders[currentPos])
        }else{
            disableReminder(reminders[currentPos])
        }

    }

    enum class AlarmType {
        NOW,
        SOON,
        SCHEDULED,
        RECURRING,
    }

    fun enableReminder(currentReminder:Reminder){
        Log.d(Constants.TAG,"Enable Reminder ${currentReminder.hours}:${currentReminder.minutes}")

        val selectedInterval = 5 * 1000
        val triggerTime = SystemClock.elapsedRealtime() + selectedInterval
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,makePendingIntent(AlarmType.SOON.toString().toLowerCase()))


//        val calender = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, currentReminder.hours)
//            set(Calendar.MINUTE, currentReminder.minutes)
//          }
//
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,calender.timeInMillis,makePendingIntent(currentReminder.id))
    }

    fun disableReminder(currentReminder:Reminder){
        Log.d(Constants.TAG,"Disable Reminder ${currentReminder.hours}:${currentReminder.minutes}")
        alarmManager.cancel(makePendingIntent(currentReminder.id))
    }

    private fun makePendingIntent(message: String): PendingIntent {

        //Pass in the message of the alarm title to send back
        val notifyIntent = Intent(app, RemindersReceiver::class.java).also {
            it.putExtra(NotificationUtilities.MESSAGE_KEY, message)
        }
        return PendingIntent.getBroadcast(
            app,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


}