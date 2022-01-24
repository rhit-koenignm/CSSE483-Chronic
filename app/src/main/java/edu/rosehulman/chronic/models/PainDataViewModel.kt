package edu.rosehulman.chronic.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.Constants
import java.sql.Time


class PainDataViewModel : ViewModel() {
        var objectList = ArrayList<PainData>()
        var currentPosition = 0         //Defaults to looking at the zeroth position

        fun getObjectAtPosition(position: Int) = objectList[position]
        fun getCurrentObject() = getObjectAtPosition(currentPosition)

        fun addObject(objectInput: PainData){
            //objectList.add(objectInput)
            fireBaseReference.add(objectInput)
        }

        fun updateCurrentObject(title: String, painvalue: Int, startTime:Timestamp,  endTime:Timestamp){
            objectList[currentPosition].title = title
            objectList[currentPosition].painLevel = painvalue
            objectList[currentPosition].startTime = startTime
            objectList[currentPosition].endTime = endTime

            fireBaseReference.document(getCurrentObject().id).set(getCurrentObject())
        }

        fun removeCurrentObject(){
            fireBaseReference.document(getCurrentObject().id).delete()
            objectList.remove(getCurrentObject())
            currentPosition = 0
        }

        fun removeAt(adapterPosition: Int) {
            var objectAtPosition = objectList[adapterPosition]
            fireBaseReference.document(objectAtPosition.id).delete()
            objectList.remove(objectAtPosition)
            currentPosition = 0
        }



        fun updatePosition(position: Int){
            currentPosition = position
        }

        fun size() = objectList.size



    //Firebase Stuff

    var fireBaseReference = Firebase.firestore.collection(PainData.COLLECTION_PATH).document(Constants.USER_ID).collection(PainData.ENTRY_COLLECTION_PATH)
    var subscriptions = HashMap<String, ListenerRegistration>()

    fun addListener(fragmentName: String, userID: String, observer: () -> Unit) {
        Log.d("Chronic","Added FireStore Listener in Model")
        val subscription = fireBaseReference
            .orderBy(PainData.SORTTIME, Query.Direction.DESCENDING)
            .addSnapshotListener() { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                error?.let {
                    Log.d("Chronic","Error in adding Snapshot Listener")
                    return@addSnapshotListener
                }

                Log.d("Chronic","Triggered Callback")
                objectList.clear()
                snapshot?.documents?.forEach() {
                    Log.d("Chronic","Adding a data from Firestore to arraylist")
                    objectList.add(PainData.from(it))
                }
                observer()
            }
        subscriptions.put(fragmentName, subscription)
    }


    fun removeListener(fragmentName: String) {
        subscriptions[fragmentName]?.remove()
        subscriptions.remove(fragmentName)
        Log.d("PB","Removed FireStore Listener in Model")
    }


}