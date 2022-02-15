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
import java.time.DayOfWeek
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


// Author: Natalie Koenig
// Description: The view model class to be used in the ReminderAdapter that will show up in the reminders page
// Date: 2/6/2022
class ReminderViewModel(private val app: Application) : AndroidViewModel(app) {

    //Variables for System Alarms
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager


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



    fun enableReminder(currentReminder:Reminder){
        Log.d(Constants.TAG,"Enable Reminder ${currentReminder.hours}:${currentReminder.minutes}")
         setReoccuringAlarms(currentReminder)
    }

    private fun setReoccuringAlarms(currentReminder: Reminder) {
    //Set a reminder for all the days selected
        var dayOfWeek = 0
        for(index in 0 until 7){
            //Grab the day of the week, and if it is true, enable it
            if(currentReminder.daysActive[index]){
                if(index == 0){
                    dayOfWeek = 7
                    val calender = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK,dayOfWeek)
                        set(Calendar.HOUR_OF_DAY, currentReminder.hours)
                        set(Calendar.MINUTE, currentReminder.minutes)
                    }

                    Log.d(Constants.TAG,"Current Time in MS is ${System.currentTimeMillis()} and Alarm Time is ${calender.timeInMillis}")

                    //Set a weekly alarm for the time and date specified
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.timeInMillis,AlarmManager.INTERVAL_DAY*7,makePendingIntent(currentReminder.title, currentReminder.title.hashCode() + index))
                }
                if(index == 1){
                    dayOfWeek = 1
                    val calender = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK,dayOfWeek)
                        set(Calendar.HOUR_OF_DAY, currentReminder.hours)
                        set(Calendar.MINUTE, currentReminder.minutes)
                    }

                    Log.d(Constants.TAG,"Current Time in MS is ${System.currentTimeMillis()} and Alarm Time is ${calender.timeInMillis}")

                    //Set a weekly alarm for the time and date specified
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.timeInMillis,AlarmManager.INTERVAL_DAY*7,makePendingIntent(currentReminder.title,currentReminder.title.hashCode() + index))
                }
                if(index == 2){
                    dayOfWeek = 2
                    val calender = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK,dayOfWeek)
                        set(Calendar.HOUR_OF_DAY, currentReminder.hours)
                        set(Calendar.MINUTE, currentReminder.minutes)
                    }

                    Log.d(Constants.TAG,"Current Time in MS is ${System.currentTimeMillis()} and Alarm Time is ${calender.timeInMillis}")

                    //Set a weekly alarm for the time and date specified
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.timeInMillis,AlarmManager.INTERVAL_DAY*7,makePendingIntent(currentReminder.title,currentReminder.title.hashCode() + index))
                }
                if(index == 3){
                    dayOfWeek = 3
                    val calender = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK,dayOfWeek)
                        set(Calendar.HOUR_OF_DAY, currentReminder.hours)
                        set(Calendar.MINUTE, currentReminder.minutes)
                    }

                    Log.d(Constants.TAG,"Current Time in MS is ${System.currentTimeMillis()} and Alarm Time is ${calender.timeInMillis}")

                    //Set a weekly alarm for the time and date specified
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.timeInMillis,AlarmManager.INTERVAL_DAY*7,makePendingIntent(currentReminder.title,currentReminder.title.hashCode() + index))
                }
                if(index == 4){
                    dayOfWeek = 4
                    val calender = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK,dayOfWeek)
                        set(Calendar.HOUR_OF_DAY, currentReminder.hours)
                        set(Calendar.MINUTE, currentReminder.minutes)
                    }

                    Log.d(Constants.TAG,"Current Time in MS is ${System.currentTimeMillis()} and Alarm Time is ${calender.timeInMillis}")

                    //Set a weekly alarm for the time and date specified
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.timeInMillis,AlarmManager.INTERVAL_DAY*7,makePendingIntent(currentReminder.title,currentReminder.title.hashCode() + index))
                }
                if(index == 5){
                    dayOfWeek = 5
                    val calender = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK,dayOfWeek)
                        set(Calendar.HOUR_OF_DAY, currentReminder.hours)
                        set(Calendar.MINUTE, currentReminder.minutes)
                    }

                    Log.d(Constants.TAG,"Current Time in MS is ${System.currentTimeMillis()} and Alarm Time is ${calender.timeInMillis}")

                    //Set a weekly alarm for the time and date specified
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.timeInMillis,AlarmManager.INTERVAL_DAY*7,makePendingIntent(currentReminder.title,currentReminder.title.hashCode() + index))
                }
                if(index == 6){
                    dayOfWeek = 6
                    val calender = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK,dayOfWeek)
                        set(Calendar.HOUR_OF_DAY, currentReminder.hours)
                        set(Calendar.MINUTE, currentReminder.minutes)
                    }

                    Log.d(Constants.TAG,"Current Time in MS is ${System.currentTimeMillis()} and Alarm Time is ${calender.timeInMillis}")

                    //Set a weekly alarm for the time and date specified
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calender.timeInMillis,AlarmManager.INTERVAL_DAY*7,makePendingIntent(currentReminder.title,currentReminder.title.hashCode() + index))
                }

            }

        }
    }

    fun disableReminder(currentReminder:Reminder){
        Log.d(Constants.TAG,"Disable Reminder ${currentReminder.hours}:${currentReminder.minutes}")


        var dayOfWeek = 0
        for(index in 0 until 7) {
            //Grab the day of the week, and if it is true, enable it
            if (currentReminder.daysActive[index]) {
                if (index == 0) {
                    alarmManager.cancel(
                        makePendingIntent(
                            currentReminder.title,
                            currentReminder.title.hashCode() + index
                        )
                    )
                }
                if (index == 1) {
                    alarmManager.cancel(
                        makePendingIntent(
                            currentReminder.title,
                            currentReminder.title.hashCode() + index
                        )
                    )
                }
                if (index == 2) {
                    alarmManager.cancel(
                        makePendingIntent(
                            currentReminder.title,
                            currentReminder.title.hashCode() + index
                        )
                    )
                }
                if (index == 3) {
                    alarmManager.cancel(
                        makePendingIntent(
                            currentReminder.title,
                            currentReminder.title.hashCode() + index
                        )
                    )
                }
                if (index == 4) {
                    alarmManager.cancel(
                        makePendingIntent(
                            currentReminder.title,
                            currentReminder.title.hashCode() + index
                        )
                    )
                }
                if (index == 5) {
                    alarmManager.cancel(
                        makePendingIntent(
                            currentReminder.title,
                            currentReminder.title.hashCode() + index
                        )
                    )
                }
                if (index == 6) {
                    alarmManager.cancel(
                        makePendingIntent(
                            currentReminder.title,
                            currentReminder.title.hashCode() + index
                        )
                    )
                }
            }
        }
    }

    private fun makePendingIntent(message: String, REQUEST_CODE: Int): PendingIntent {
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

    fun setCurrentReminder(checked: Boolean) {
        reminders[currentPos].isActive = checked
        ref.document(getCurrentReminder().id).set(getCurrentReminder())

        if(reminders[currentPos].isActive){
            enableReminder(reminders[currentPos])
        }else{
            disableReminder(reminders[currentPos])
        }
    }

    fun disableReminderAt(adapterPosition: Int) {
        disableReminder(getReminderAt(adapterPosition))
    }
}