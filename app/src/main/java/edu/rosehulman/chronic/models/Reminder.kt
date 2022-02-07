package edu.rosehulman.chronic.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class Reminder(
    var title: String = "Default title",
    var content: String = "Default content",
    var hours: Int = 0,
    var minutes: Int = 0,
    var isActive: Boolean = true,
    var daysActive: List<Boolean> = listOf(true, true, true, true, true)
) {

    @get:Exclude
    var id = ""

    fun makeACopy(): Reminder {
        var clone = Reminder()
        clone.title = title
        clone.content = content
        clone.hours = hours
        clone.minutes = minutes
        clone.isActive = isActive
        clone.daysActive = ArrayList(daysActive)
        return clone
    }

    companion object {
        const val COLLECTION_PATH = "reminders"

        fun from(snapshot: DocumentSnapshot): Reminder {
            val reminder = snapshot.toObject(Reminder::class.java)!!
            reminder.id = snapshot.id
            return reminder
        }
    }
}