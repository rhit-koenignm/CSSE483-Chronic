package edu.rosehulman.chronic.models

import android.app.usage.ConfigurationStats
import android.location.GnssAntennaInfo
import android.util.Log
import androidx.lifecycle.ViewModel
import coil.load
import com.google.firebase.auth.ktx.auth
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

    var uid = Firebase.auth.uid!!

    var ref = Firebase.firestore.collection(Tag.COLLECTION_PATH)
    var userRef = Firebase
        .firestore
        .collection(UserData.COLLECTION_PATH)
        .document(uid)

    val tagSubscriptions = HashMap<String, ListenerRegistration>()
    val userSubscriptions = HashMap<String, ListenerRegistration>()

    fun addUserListener(fragmentName: String, userID: String) {
        Log.d(Constants.TAG, "Adding user listener for user $userID for fragment $fragmentName")
        // Now to create the user and then add the current firebase reference
        val userSubscription = userRef
            .addSnapshotListener { snapshot: DocumentSnapshot?, error ->
                error?.let {
                    Log.d(Constants.TAG, "Error $error")
                    return@addSnapshotListener
                }
                Log.d(Constants.TAG, "In snapshot listener with user $uid")
                myTags.clear()
                var tempTags = snapshot!!.get("myTags") as List<String>
                tempTags.forEach {
                    Log.d(Constants.TAG, "Got tag ${it}")
                    myTags.add(it)
                }
            }
//            .addSnapshotListener { snapshot: QuerySnapshot?, error ->
//                error?.let {
//                    Log.d(Constants.TAG, "Error $error")
//                    return@addSnapshotListener
//                }
//                Log.d(Constants.TAG, "In snapshot listener with ${snapshot?.size()} docs")
//                myTags.clear()
//                snapshot?.documents?.forEach {
//                    var temp = UserData.from(it)
//                    if(temp.id.equals(Constants.USER_ID)) {
//                        currentUser = temp
//                        myTags.addAll(currentUser.myTags)
//                    }
//                }
//            }
        userSubscriptions[fragmentName]
        Log.d(Constants.TAG, "Successfully grabbed user with id of ${uid}")
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
                .whereIn("creator", listOf("admin", uid))
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
                        tag.isTracked = myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        } else if(type.equals("Symptoms")) {
            val subscription = ref
                .whereEqualTo("type", "Symptom")
                .whereIn("creator", listOf("admin", uid))
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
                        tag.isTracked = myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        } else if(type.equals("Triggers")) {
            val subscription = ref
                .whereEqualTo("type", "Trigger")
                .whereIn("creator", listOf("admin", uid))
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
                        tag.isTracked = myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        }else if(type.equals("Symptoms")) {
            val subscription = ref
                .whereEqualTo("type", "Symptom")
                .whereIn("creator", listOf("admin", uid))
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
                        tag.isTracked = myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        } else if(type.equals("Treatments")){
            val subscription = ref
                .whereEqualTo("type", "Treatment")
                .whereIn("creator", listOf("admin", uid))
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
                        tag.isTracked = myTags.contains(tag.id)
                        tags.add(tag)
                    }
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        } else {
            val subscription = ref
                .whereIn("creator", listOf("admin", uid))
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
                        tag.isTracked = myTags.contains(tag.id)
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
            tag.creator = uid
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
        if (getCurrentTag().creator.equals(uid)) {
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
        if(getCurrentTag().isTracked && !myTags.contains(getCurrentTag().id)) {
            myTags.add(getCurrentTag().id)
        } else {
            myTags.remove(getCurrentTag().id)
        }
        myTags = myTags
        userRef.update(mapOf(
            "myTags" to myTags
        ))
    }
}