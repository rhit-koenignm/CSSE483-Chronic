package edu.rosehulman.chronic.models

import android.provider.SyncStateContract
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.Constants


// Author: Natalie Koenig
// Description: The view model class to be used in the ReminderAdapter that will show up in the reminders page
// Date: 2/6/2022
class ReminderViewModel : ViewModel() {

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

    fun updatePos(pos: Int) {
        currentPos = pos
    }

    fun clear() {
        reminders.clear()
    }

    fun size() = reminders.size

    fun toggleCurrentReminder() {
        reminders[currentPos].isActive = !reminders[currentPos].isActive
        ref.document(getCurrentReminder().id).set(reminders[currentPos])
    }


}