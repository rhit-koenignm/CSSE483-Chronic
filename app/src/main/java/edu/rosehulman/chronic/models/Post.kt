package edu.rosehulman.chronic.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class Post(
    var authorID: String = "",
    var author: String = "",
    var title: String = "",
    var content: String = "",
    var photoUrl: String = ""
){
    @get:Exclude
    var id = ""

    @ServerTimestamp
    var created: Timestamp? = null

    companion object {
        const val COLLECTION_PATH = "posts"
        const val CREATED_KEY = "created"

        fun from(snapshot:DocumentSnapshot): Post {
            val post = snapshot.toObject(Post::class.java)!!
            post.id = snapshot.id
            return post
        }
    }

    fun getDate():String{
        if(this.created != null){
            val objectDate:LocalDateTime = convertToLocalDateViaInstant(this.created!!.toDate())
            return "${objectDate.month}/${objectDate.dayOfMonth}/${objectDate.year}"
        }else{
            return ""
        }
    }

    fun getTime():String{
        if(this.created != null){
            val objectDate:LocalDateTime = convertToLocalDateViaInstant(this.created!!.toDate())
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