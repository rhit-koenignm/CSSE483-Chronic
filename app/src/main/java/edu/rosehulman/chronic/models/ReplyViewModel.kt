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

    var postID: String = ""

    lateinit var ref: CollectionReference

    var userRef = Firebase
        .firestore
        .collection(UserData.COLLECTION_PATH)
        .document(uid)

    var replies = ArrayList<Reply>()
    var currentPos = 0
    var currentUser: UserData? = null

    fun getReplyAt(pos: Int) = replies[pos]
    fun getCurrentReply() = getReplyAt(currentPos)

    val subscriptions = HashMap<String, ListenerRegistration>()
    var userSubscriptions = HashMap<String, ListenerRegistration>()
    //TODO: Set up userdata for use when creating and updating replies

    fun addListener(fragmentName: String, postID: String, observer: () -> Unit) {
        this.postID = postID

        var userSubscription = userRef
            .addSnapshotListener { snapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                error?.let {
                    Log.d(Constants.TAG, "In snapshot listener with user $uid")
                    return@addSnapshotListener
                }
                Log.d(Constants.TAG, "In snapshot listener with user $uid")
                if(snapshot != null){
                    currentUser = UserData.from(snapshot)
                    if(currentUser != null) {
                        Log.d(Constants.TAG, "Successfully grabbed user with username ${currentUser!!.userName}")
                    }
                }
            }
        userSubscriptions[fragmentName] = userSubscription

        this.ref = Firebase
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
        userSubscriptions[fragmentName]?.remove()
        userSubscriptions.remove(fragmentName)
    }

    fun addReply(reply: Reply?) {
        var replyRef = Firebase
            .firestore
            .collection(Post.COLLECTION_PATH)
            .document(postID)
            .collection(Reply.COLLECTION_PATH)

        if (reply != null) {
            reply.author = currentUser!!.userName
            reply.authorID = uid
            replyRef.add(reply)
        }
    }

    fun updateCurrentReply(reply: Reply?) {
        if(reply != null && isUserCurrentReplyAuthor()) {
            reply.setAuthorData(uid, currentUser!!.userName)
            reply.id = getCurrentReply().id
            replies[currentPos] = reply

            var replyRef = Firebase
                .firestore
                .collection(Post.COLLECTION_PATH)
                .document(postID)
                .collection(Reply.COLLECTION_PATH)
                .document(getCurrentReply().id)
            Log.d(Constants.TAG, "Current reply ref: " + replyRef.toString())

            replyRef.set(getCurrentReply())
        }
    }

    fun removeCurrentReply() {
        var replyRef = Firebase
            .firestore
            .collection(Post.COLLECTION_PATH)
            .document(postID)
            .collection(Reply.COLLECTION_PATH)

        replyRef.document(getCurrentReply().id).delete()
        currentPos = 0
    }

    fun isUserCurrentReplyAuthor() : Boolean {
        var reply = getCurrentReply()
        if(reply.authorID.equals(uid)) {
            return true
        } else {
            return false
        }
    }

    fun updatePos(pos: Int) {
        currentPos = pos
    }

    fun clear() {
        replies.clear()
    }

    fun size() = replies.size

}