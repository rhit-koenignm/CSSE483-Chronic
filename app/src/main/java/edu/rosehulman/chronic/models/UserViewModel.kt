package edu.rosehulman.chronic.models

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserViewModel : ViewModel() {
    var firebaseReference = Firebase.firestore.collection(UserData.COLLECTION_PATH)
    .document(Firebase.auth.currentUser?.uid!!)

    var user: UserData? = null  //Allows local user to be null


    fun hasCompletedSetup(): Boolean {
        return user?.hasCompletedSetup ?: false
    }

    //Send in a callback observer function
    fun getOrMakeUser(observer: () -> Unit) {
        if (user != null) {
            //done for now, user exists, need to get it
            observer()
        } else {
            //User does not exist, need to make it
            firebaseReference.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
                if (snapshot.exists()) {
                    //If the snapshot does indeed exist
                    user = snapshot.toObject(UserData::class.java)
                } else {
                    //There is no corresponding user on firebase
                    with(Firebase.auth.currentUser) {
                        var firstNameInput = this?.displayName?.split(" ")?.get(0)
                        var lastNameInput = this?.displayName?.split(" ")?.get(1)
                        var photoURLInput = this?.photoUrl.toString()
                        var photoURL = photoURLInput ?: "No URL"
                        user = UserData(firstName = firstNameInput!!, lastName = lastNameInput!!, Email = this?.email!!, ProfileURL = photoURL)
                    }
                    //Push the new thing back to firebase
                    firebaseReference.set(user!!)
                }
                observer()
            }

        }
    }

    fun updateUser(firstNameIn:String, lastNameIn:String, emailIn:String, usernameIn:String, profilephotoURLIn:String, newHasCompletedSetup: Boolean) {
        if (user != null) {
            with(user!!) {
                firstName = firstNameIn
                lastName = lastNameIn
                Email = emailIn
                userName = usernameIn
                ProfileURL = profilephotoURLIn
                hasCompletedSetup = newHasCompletedSetup

                //push the data up to firebase
                firebaseReference.set(this)
            }
        }
    }
}

