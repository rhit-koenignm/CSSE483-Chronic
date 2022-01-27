package edu.rosehulman.chronic.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ktx.toObject

class Tag(
    var title: String = "",
    var type: String = "",
    var creator: String = "",
    var isTracked: Boolean = false
) {
    @get:Exclude
    var id = ""

    fun toggleTracked() {
        isTracked = !isTracked
    }

    override fun toString(): String {
        return (title + ": " + type)
    }

    companion object {
        const val COLLECTION_PATH = "tags"

        fun from(snapshot: DocumentSnapshot): Tag {
            val tag = snapshot.toObject(Tag::class.java)!!
            tag.id = snapshot.id
            return tag
        }
    }
}