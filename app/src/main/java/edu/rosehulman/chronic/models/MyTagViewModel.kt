package edu.rosehulman.chronic.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.Constants

class MyTagViewModel : ViewModel() {
    var tags = ArrayList<Tag>()
    var myTags = ArrayList<String>()
    var currentPos = 0

    fun getTagAt(pos: Int) = tags[pos]
    fun getCurrentTag() = tags[currentPos]

    var ref = Firebase.firestore.collection(Tag.COLLECTION_PATH)
    lateinit var userTagRef: CollectionReference

    val subscriptions = HashMap<String, ListenerRegistration>()

    fun addListener(fragmentName: String, userID: String, type: String, observer: () -> Unit) {
        //populate later
        Log.d(Constants.TAG, "Adding listener for $fragmentName")
        //userTagRef = Firebase.firestore.collection("users").document(userID).collection(UserData.MYTAGS_COLLECTION_PATH)

        if(type.equals("All")) {
            val subscription = ref
                .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    error?.let {
                        Log.d(Constants.TAG, "Error $error")
                        return@addSnapshotListener
                    }
                    Log.d(Constants.TAG, "In snapshot listener with ${snapshot?.size()} docs")
                    tags.clear()
                    snapshot?.documents?.forEach {
                        var tag = Tag.from(it)
                        //Here is where we'll track our tags and see if it is already tracked
                        //For now we will just set the value to false
                        tag.isTracked = false
                        tags.add(tag)
                    }
                    observer()
                }
            subscriptions[fragmentName + "tags"] = subscription
        }
        //This is where we'll handle everything else
    }

    fun removeListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing listener for $fragmentName")
        subscriptions[fragmentName + "tags"]?.remove()
        subscriptions.remove(fragmentName + "tags")
    }

    fun createTag(tag: Tag?) : Boolean {
        //So this is where we create new tags, however we won't allow them to add a null tag
        if(tag == null) {
            return false
        } else {
            ref.add(tag)
            return true
        }
    }

    // They also cannot update tags unless they are the creator
    fun updateTag(tag: Tag, userName: String) : Boolean{
        if(tag.creator == userName) {
            tags[currentPos].title = tag.title
            tags[currentPos].type = tag.type
            ref.document(getCurrentTag().id).set(getCurrentTag())
            return true
        }
        return false
    }

    // They cannot remove tags unless they created them
    fun removeCurrentTag(userName: String) : Boolean {
        var tag = getCurrentTag()
        if(tag.creator == userName) {
            ref.document(getCurrentTag().id).delete()
            currentPos = 0
            return true
        }
        else {
            return false
        }
    }

    fun updatePos(pos: Int) {
        currentPos = pos
    }

    fun size() = tags.size

    fun toggleTracked() {
        // So isTracked will be used locally, but not in firestore.
        // When I load them in I'll set them according to the user's tag collection
        tags[currentPos].isTracked = !tags[currentPos].isTracked
        if(getCurrentTag().isTracked) {
            myTags.add(getCurrentTag().id)
        } else {
            myTags.remove(getCurrentTag().id)
        }
    }
}