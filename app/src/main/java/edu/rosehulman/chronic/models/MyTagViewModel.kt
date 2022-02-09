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

// Author: Natalie Koenig
// Description: The view model class to be used in the MyTagAdapter that will show up in the profile page and my tags page
// Date: 1/31/2022
class MyTagViewModel : ViewModel() {
    // To make life easier, we are storing the
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

    fun addUserListener(fragmentName: String, observer: () -> Unit) {
        Log.d(Constants.TAG, "Adding user listener for user $uid for fragment $fragmentName")
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
                myTags.addAll(tempTags)
                Log.d(Constants.TAG,"There are a total of ${myTags.size} tags for user $uid")

                //Now call then next function to update the adapter data
                observer()
            }
        userSubscriptions[fragmentName]
        Log.d(Constants.TAG, "Successfully grabbed user with id of ${uid}")
    }

    fun removeUserListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing user listener for $fragmentName")
        userSubscriptions[fragmentName]?.remove()
        userSubscriptions.remove(fragmentName)
    }

    // This function adds the listener for the tags collection overall
    // We grab the type so we can know what type we are filtering by, if it is myTags then it is ONLY the ones that have been added
    fun addListener(fragmentName: String, type: String, observer: () -> Unit) {
        Log.d(Constants.TAG, "Adding listener for $fragmentName")

        // Checking what the type is. Since MyTags is not a type, we are going to check that first
        if(type.equals("MyTags")) {
            if(myTags.isEmpty()){
                Log.d(Constants.TAG, "My tags is empty so no listener to add")
            } else {
                val subscription = ref
                    .whereIn(FieldPath.documentId(), myTags)
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
        } else if(type.equals("All")){
            // This case will handle the default filtering on the MyTagsFragment which displays all of the tags
            val subscription = ref
                .whereIn("creator", listOf("admin", uid))
                .orderBy("type")
                .orderBy("title")
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
            // This case will handle the filtering for a Symptom, Trigger, or Treatment
            val subscription = ref
                .whereIn("creator", listOf("admin", uid))
                .whereEqualTo("type", type)
                .orderBy("type")
                .orderBy("title")
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

    // Removing our tag listener
    fun removeListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing listener for $fragmentName")
        tagSubscriptions[fragmentName]?.remove()
        tagSubscriptions.remove(fragmentName)
    }

    fun createTag(tag: Tag?) : Boolean {
        //So this is where we create new tags, however we won't allow them to add a null tag or empty tag
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

    // This is a very important function that checks if the creator of a tag is the current user
    // We use this before doing updates and deletions
    fun creatorIsUser(): Boolean {
        return getCurrentTag().creator.equals(uid)
    }

    // Grabbing the size of the tags overall
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