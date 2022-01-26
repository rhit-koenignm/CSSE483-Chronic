package edu.rosehulman.chronic.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ktx.toObject

data class UserData(
    var ProfileURL:String = "https://static.wikia.nocookie.net/tomclancy/images/c/cc/Sean_Connery_The_Hunt_for_Red_October.jpg/revision/latest/scale-to-width-down/282?cb=20150714000007",
    var userName:String = "Steely-Eyed Missile Man",
    var firstName:String = "Marko",
    var lastName:String = "Ramius",
    var Email:String = "RamiusM@Soviet.Union",
    var myTags: Array<String> = arrayOf<String>())
{
    @get:Exclude
    var id = ""

    companion object {
        const val COLLECTION_PATH = "users"

        //These will be used when we grab the user data
        const val ENTRY_COLLECTION_PATH = "entries"
        const val MYPOST_COLLECTION_PATH = "myPosts"
        const val MYTAGS_COLLECTION_PATH = "myTags"
        const val REMINDERS_COLLECTION_PATH = "reminders"

        fun from(snapshot: DocumentSnapshot): UserData {
            val ud = snapshot.toObject(UserData::class.java)!!
            ud.id = snapshot.id
            return ud
        }
    }
}
