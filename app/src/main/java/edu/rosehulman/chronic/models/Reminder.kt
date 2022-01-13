package edu.rosehulman.chronic.models

import java.sql.Time

data class Reminder(var title: String, var content: String, var time: Time, var isActive: Boolean = true) {
    
}