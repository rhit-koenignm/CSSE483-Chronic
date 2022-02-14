package edu.rosehulman.chronic.models

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase

class ReplyViewModel : ViewModel() {
    var uid = Firebase.auth.uid!!

    lateinit var ref: CollectionReference

    var replies = ArrayList<Reply>()
    var currentPos = 0

    fun getReplyAt(pos: Int) = replies[pos]
    fun getCurrentReply() = getReplyAt(currentPos)

    val subscriptions = HashMap<String, ListenerRegistration>()

    fun addListener(fragmentName: String, postID: String, observer: () -> Unit) {

    }
}