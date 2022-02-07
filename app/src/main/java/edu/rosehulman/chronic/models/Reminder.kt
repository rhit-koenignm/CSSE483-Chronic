package edu.rosehulman.chronic.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class Reminder(
    var title: String = "Default title",
    var content: String = "Default content",
    var hours: Int = 0,
    var minutes: Int = 0,
    var isActive: Boolean = true,
    var daysActive: List<Boolean> = listOf(true, true, true, true, true, true, true)
) {

    @get:Exclude
    var id = ""

    fun toggleIsActive() {
        isActive = !isActive
    }

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

    fun getTimeString(): String {
        var tempHour: Int = 0
        var hourString: String = ""
        var minutesString: String = ""
        var ampmString: String = ""

        if(hours < 12) {
            tempHour = hours
            ampmString = "AM"
        } else {
            tempHour = hours - 12
            ampmString = "PM"
        }

        if(tempHour < 10){
            hourString = "0" + tempHour.toString()
        } else {
            hourString = tempHour.toString()
        }

        if(minutes < 10){
            minutesString = "0" + minutes.toString()
        } else {
            minutesString = minutes.toString()
        }

        return hourString + ":" + minutesString + " " + ampmString
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