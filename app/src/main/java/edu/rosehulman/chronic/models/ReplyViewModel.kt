package edu.rosehulman.chronic.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.utilities.Constants

class ReplyViewModel : ViewModel() {
    var uid = Firebase.auth.uid!!

    lateinit var ref: CollectionReference

    var replies = ArrayList<Reply>()
    var currentPos = 0

    fun getReplyAt(pos: Int) = replies[pos]
    fun getCurrentReply() = getReplyAt(currentPos)

    val subscriptions = HashMap<String, ListenerRegistration>()

    fun addListener(fragmentName: String, postID: String, observer: () -> Unit) {
        ref = Firebase
            .firestore
            .collection(Post.COLLECTION_PATH)
            .document(postID)
            .collection(Reply.COLLECTION_PATH)
        Log.d(Constants.TAG, "Adding listener for $fragmentName")
        val subscription = ref
            .orderBy(Reply.CREATED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, error:FirebaseFirestoreException? ->
                error?.let {
                    Log.d(Constants.TAG, "Error: $error")
                    return@addSnapshotListener
                }
                Log.d(Constants.TAG, "In snapshot listener with ${snapshot?.size()} docs")
                replies.clear()
                snapshot?.documents?.forEach {
                    replies.add(Reply.from(it))
                }
                observer()
            }
        subscriptions[fragmentName] = subscription
    }

    fun removeListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing listener for $fragmentName")
        subscriptions[fragmentName]?.remove()
        subscriptions.remove(fragmentName)
    }

    fun addReply(reply: Reply?) {
    }
}