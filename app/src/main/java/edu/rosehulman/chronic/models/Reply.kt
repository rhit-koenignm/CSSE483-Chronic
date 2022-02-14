package edu.rosehulman.chronic.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.sql.Timestamp

class Reply(
    var authorID: String="",
    var author: String="",
    var content: String="",
) {
    @get:Exclude
    var id = ""

    @ServerTimestamp
    var created: Timestamp? = null

    companion object {
        const val COLLECTION_PATH = "replies"
        const val CREATED_KEY = "created"

        fun from(snapshot: DocumentSnapshot): Reply {
            val reply = snapshot.toObject(Reply::class.java)!!
            reply.id = snapshot.id
            return reply
        }
    }
}