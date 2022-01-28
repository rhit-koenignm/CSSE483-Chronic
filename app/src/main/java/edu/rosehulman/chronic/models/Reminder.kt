package edu.rosehulman.chronic.models

import com.google.firebase.firestore.Exclude

data class Reminder(
    var title: String,
    var content: String,
    var hour: Int,
    var minute: Int
) {
    var isActive: Boolean = true
    var daysActive: List<Boolean> = listOf(true, true, true, true, true)

    @get:Exclude
    var id = ""
}