package edu.rosehulman.chronic.models

import android.location.GnssAntennaInfo
import android.util.Log
import androidx.lifecycle.ViewModel
import coil.load
import com.google.firebase.firestore.*
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
    lateinit var currentUser: UserData
    lateinit var userTagsRef: CollectionReference

    val tagSubscriptions = HashMap<String, ListenerRegistration>()
    val userSubscriptions = HashMap<String, ListenerRegistration>()

    fun addUserListener(fragmentName: String, userID: String) {
        Log.d(Constants.TAG, "Adding user listener for user $userID for fragment $fragmentName")
        // Now to create the user and then add the current firebase reference
//        currentUser = UserData.from(Firebase.firestore.collection(UserData.COLLECTION_PATH).document(userID))
        Log.d(Constants.TAG, "Successfully grabbed user ref with a id of ${currentUser.id}")
    }


    fun addListener(fragmentName: String, userID: String, type: String, observer: () -> Unit) {
        //populate later
        Log.d(Constants.TAG, "Adding listener for $fragmentName")

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
            tagSubscriptions[fragmentName + "tags"] = subscription
        }
        //This is where we'll handle everything else
    }

    fun removeListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing listener for $fragmentName")
        tagSubscriptions[fragmentName + "tags"]?.remove()
        tagSubscriptions.remove(fragmentName + "tags")
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
    fun updateTag(tag: Tag) : Boolean{
        // FIX THIS TO MATCH THE CURRENT USER'S ID
        if(tag.creator.equals("")) {
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