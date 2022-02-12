package edu.rosehulman.chronic.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.utilities.Constants

class PainDataEntryTagViewModel : ViewModel() {
    val tagSubscriptions = HashMap<String, ListenerRegistration>()
    val userSubscriptions = HashMap<String, ListenerRegistration>()
    var myTags = ArrayList<String>()
    var attachedTag = ArrayList<String>()

    //Special Variables for Entry Tag Tracking
    var myTriggers = ArrayList<Tag>()
    var triggersPosition = 0;
    var myTreatments = ArrayList<Tag>()
    var treatmentsPosition = 0;
    var mySymptoms = ArrayList<Tag>()
    var symptomsPosition = 0;

    //Firebase stuff
    var uid = Firebase.auth.uid!!
    var tagsRef = Firebase.firestore.collection(Tag.COLLECTION_PATH)
    var userRef = Firebase.firestore.collection(UserData.COLLECTION_PATH).document(uid)



    fun updateAttachedTags(attachedTags: ArrayList<String>) {
        attachedTag = attachedTags
        Log.d(Constants.TAG,"Tags From Data Entry Tied to Viewmodel")

    }


    fun getTypeSize(dataType: String): Int {
        if(dataType == "Treatments"){
            return myTreatments.size
        }else if(dataType == "Symptoms"){
            return mySymptoms.size
        }else if(dataType == "Triggers"){
            return myTriggers.size
        }else{
            Log.d(Constants.TAG,"Failed to Bind Non-existant Data Type")
            return 0
        }

    }

    fun getTypeTagAt(position: Int, dataType: String): Tag {
        if(dataType == "Treatments"){
            return myTreatments[position]
        }else if(dataType == "Symptoms"){
            return mySymptoms[position]
        }else{
            return myTriggers[position]
        }
    }

    fun updateTypePos(adapterPosition: Int, dataType: String) {
        if(dataType == "Treatments"){
            treatmentsPosition = adapterPosition
        }else if(dataType == "Symptoms"){
            symptomsPosition = adapterPosition
        }else{
            triggersPosition = adapterPosition
        }
    }


    fun addMyTagsByTypeListener(fragmentName: String, observer: () -> Unit) {
        Log.d(Constants.TAG, "Adding listener for $fragmentName")

        // Checking what the type is. Since MyTags is not a type, we are going to check that first

        if (myTags.isEmpty()) {
            Log.d(Constants.TAG, "My tags is empty so no listener to add")
        } else {
            val subscription = tagsRef
                .whereIn("creator", listOf("admin", uid))
                .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    error?.let {
                        Log.d(Constants.TAG, "Error $error")
                        return@addSnapshotListener
                    }
                    Log.d(Constants.TAG, "In snapshot listener with ${snapshot?.size()} docs")
                    myTriggers.clear()
                    mySymptoms.clear()
                    myTreatments.clear()
                    snapshot?.documents?.forEach {
                        var tag = Tag.from(it)

                        if(myTags.contains(tag.id)){
                            //If the tag is attached to the entry, set it as tracked
                            if(attachedTag.contains(tag.id)){
                                tag.isTracked = true;
                            }

                            if(tag.type == "Symptom"){
                                mySymptoms.add(tag)
                            }else if(tag.type == "Trigger"){
                                myTriggers.add(tag)
                            }else if(tag.type == "Treatment"){
                                myTreatments.add(tag)
                            }else{
                                Log.d(Constants.TAG,"Tag Has No Type")
                            }
                        }
                    }
                    Log.d(Constants.TAG,"Grabbed a total of ${snapshot?.size()} docs, with ${myTreatments.size} Treatments, ${myTriggers.size} Triggers and ${mySymptoms.size} Symptoms")
                    observer()
                }
            tagSubscriptions[fragmentName] = subscription
        }
    }


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

    // Removing our tag listener
    fun removeListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing listener for $fragmentName")
        tagSubscriptions[fragmentName]?.remove()
        tagSubscriptions.remove(fragmentName)
    }

    fun toggleTypeTracked(adapterPosition: Int, dataType: String) {
        if(dataType == "Treatments"){
            if( myTreatments[adapterPosition].isTracked == true){
                myTreatments[adapterPosition].isTracked = false
                attachedTag.remove(myTreatments[adapterPosition].id)
            }else{
                myTreatments[adapterPosition].isTracked = true
                attachedTag.add(myTreatments[adapterPosition].id)
            }
        }else if(dataType == "Symptoms"){
            if( mySymptoms[adapterPosition].isTracked == true){
                mySymptoms[adapterPosition].isTracked = false
                attachedTag.remove(mySymptoms[adapterPosition].id)
            }else{
                mySymptoms[adapterPosition].isTracked = true
                attachedTag.add(mySymptoms[adapterPosition].id)
            }
        }else{
            if( myTriggers[adapterPosition].isTracked == true){
                myTriggers[adapterPosition].isTracked = false
                attachedTag.remove(myTriggers[adapterPosition].id)
            }else{
                myTriggers[adapterPosition].isTracked = true
                attachedTag.add(myTriggers[adapterPosition].id)
            }
        }
    }


}