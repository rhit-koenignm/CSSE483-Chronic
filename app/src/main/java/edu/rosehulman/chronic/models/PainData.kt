package edu.rosehulman.chronic.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.sql.Time
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

data class PainData(
    var painLevel: Int = -1,
    var title: String = "Null",
    var startTime:Timestamp = Timestamp.now(),
    var endTime:Timestamp = Timestamp.now(),
){
    @get:Exclude
    var id = ""

    companion object {
        const val COLLECTION_PATH = "users"
        const val ENTRY_COLLECTION_PATH = "entries"
        const val ENTRYTAGS_COLLECTION_PATH = "entryTags"
        const val SORTTIME = "startTime"

        fun from(snapshot: DocumentSnapshot): PainData {
            val pd:PainData = snapshot.toObject(PainData::class.java)!!
            pd.id = snapshot.id
            return pd
        }
    }


    fun getFormattedStartTime():String{
        var startDate:LocalDateTime = convertToLocalDateViaInstant(startTime.toDate())
        return "${startDate.monthValue}/${startDate.dayOfMonth}"

    }

    private fun convertToLocalDateViaInstant(dateToConvert: Date): LocalDateTime {
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
}