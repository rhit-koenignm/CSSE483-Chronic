package edu.rosehulman.chronic.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.time.LocalDateTime

data class PainData(
    var painLevel: Int = -1,
    var title: String = "Null",
    var startTime:LocalDateTime = LocalDateTime.now(),
    var endTime:LocalDateTime = LocalDateTime.now(),
){
    @get:Exclude
    var id = ""

    companion object {
        const val COLLECTION_PATH = "users"
        const val ENTRY_COLLECTION_PATH = "entries"
        const val ENTRYTAGS_COLLECTION_PATH = "entryTags"


        fun from(snapshot: DocumentSnapshot): PainData {
            val pd = snapshot.toObject(PainData::class.java)!!
            pd.id = snapshot.id
            return pd
        }
    }
}