package edu.rosehulman.chronic.models

import android.app.usage.ConfigurationStats
import android.location.GnssAntennaInfo
import android.util.Log
import androidx.lifecycle.ViewModel
import coil.load
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.Constants

class MyTagViewModel : ViewModel() {
    var tags = ArrayList<Tag>()
    var myTags = ArrayList<String>()
    var currentPos = 0

    fun getTagAt(pos: Int) = tags[pos]
    fun getCurrentTag() = tags[currentPos]

    var ref = Firebase.firestore.collection(Tag.COLLECTION_PATH)
    var currentUser: UserData = UserData()
    var userRef = Firebase.firestore.collection(UserData.COLLECTION_PATH)

    val tagSubscriptions = HashMap<String, ListenerRegistration>()
    val userSubscriptions = HashMap<String, ListenerRegistration>()

    fun addUserListener(fragmentName: String, userID: String) {
        Log.d(Constants.TAG, "Adding user listener for user $userID for fragment $fragmentName")
        // Now to create the user and then add the current firebase reference
        val userSubscription = Firebase.firestore.collection(UserData.COLLECTION_PATH)
            .addSnapshotListener { snapshot: QuerySnapshot?, error ->
                error?.let {
                    Log.d(Constants.TAG, "Error $error")
                    return@addSnapshotListener
                }
                Log.d(Constants.TAG, "In snapshot listener with ${snapshot?.size()} docs")
                myTags.clear()
                snapshot?.documents?.forEach {
                    var temp = UserData.from(it)
                    if(temp.id.equals(Constants.USER_ID)) {
                        currentUser = temp
                        myTags.addAll(currentUser.myTags)
                    }
                }
            }
        userSubscriptions[fragmentName]
        Log.d(Constants.TAG, "Successfully grabbed user with id of ${currentUser.id}")
    }

    fun removeUserListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing user listener for $fragmentName")
        userSubscriptions[fragmentName]?.remove()
        userSubscriptions.remove(fragmentName)
    }

    fun addListener(fragmentName: String, userID: String, type: String, observer: () -> Unit) {
        //populate later
        Log.d(Constants.TAG, "Adding listener for $fragmentName")

        if(type.equals("MyTags")) {
            val subscription = ref
                .whereIn("id", myTags)
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
                        tag.isTracked = currentUser.myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        } else if(type.equals("Symptoms")) {
            val subscription = ref
                .whereEqualTo("type", "Symptom")
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
                        tag.isTracked = currentUser.myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        } else if(type.equals("Triggers")) {
            val subscription = ref
                .whereEqualTo("type", "Trigger")
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
                        tag.isTracked = currentUser.myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        }else if(type.equals("Symptoms")) {
            val subscription = ref
                .whereEqualTo("type", "Symptom")
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
                        tag.isTracked = currentUser.myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        } else if(type.equals("Treatments")){
            val subscription = ref
                .whereEqualTo("type", "Treatment")
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
                        tag.isTracked = currentUser.myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        } else {
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
                        tag.isTracked = currentUser.myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        }
        //This is where we'll handle everything else
    }

    fun removeListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing listener for $fragmentName")
        tagSubscriptions[fragmentName]?.remove()
        tagSubscriptions.remove(fragmentName)
    }

    fun createTag(tag: Tag?) : Boolean {
        //So this is where we create new tags, however we won't allow them to add a null tag
        if (tag == null) {
            return false
        } else if (tag.type.equals("") || tag.title.equals("")){
            return false
        }else {
            tag.creator = currentUser.id
            tag.isTracked
            ref.add(tag)
            return true
        }
    }

    // They also cannot update tags unless they are the creator
    fun updateTag(tag: Tag) : Boolean{
        if(creatorIsUser()) {
            if (tag.title.equals("") || tag.type.equals("")){
                return false
            }
            tags[currentPos].title = tag.title
            tags[currentPos].type = tag.type
            ref.document(getCurrentTag().id).set(getCurrentTag())
            return true
        }
        return false
    }

    // They cannot remove tags unless they created them
    fun removeCurrentTag() : Boolean {
        var tag = getCurrentTag()
        if(creatorIsUser()) {
            myTags.remove(tag.id)
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

    fun creatorIsUser(): Boolean {
        if (getCurrentTag().creator.equals(currentUser.id)) {
            return true
        } else {
            return false
        }
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
        currentUser.myTags = myTags
        userRef.document(currentUser.id).set(currentUser)
    }
}