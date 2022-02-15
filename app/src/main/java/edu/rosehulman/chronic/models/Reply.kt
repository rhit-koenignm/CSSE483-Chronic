package edu.rosehulman.chronic.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class Reply(
    var authorID: String="",
    var author: String="",
    var content: String="",
) {
    @get:Exclude
    var id = ""

    @ServerTimestamp
    var created: com.google.firebase.Timestamp? = null

    companion object {
        const val COLLECTION_PATH = "replies"
        const val CREATED_KEY = "created"

        fun from(snapshot: DocumentSnapshot): Reply {
            val reply = snapshot.toObject(Reply::class.java)!!
            reply.id = snapshot.id
            return reply
        }
    }

    fun setAuthorData(authID: String, userName: String) {
        if(authID.isNotEmpty() && userName.isNotEmpty()) {
            this.authorID = authID
            this.author = userName
        }
    }

    fun getDate():String{
        if(this.created != null){
            val objectDate: LocalDateTime = convertToLocalDateViaInstant(this.created!!.toDate())
            return "${objectDate.month}/${objectDate.dayOfMonth}/${objectDate.year}"
        }else{
            return ""
        }
    }

    fun getTime():String{
        if(this.created != null){
            val objectDate: LocalDateTime = convertToLocalDateViaInstant(this.created!!.toDate())
            return "${objectDate.hour}:${objectDate.minute}"
        }else{
            return ""
        }
    }

    private fun convertToLocalDateViaInstant(dateToConvert: Date): LocalDateTime {
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
}