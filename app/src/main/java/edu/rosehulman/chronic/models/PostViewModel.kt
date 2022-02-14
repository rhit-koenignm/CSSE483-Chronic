package edu.rosehulman.chronic.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.utilities.Constants

class PostViewModel: ViewModel() {

    var uid = Firebase.auth.uid!!

    var ref: CollectionReference = Firebase
        .firestore
        .collection(Post.COLLECTION_PATH)

    var posts = ArrayList<Post>()
    var currentPos = 0

    fun getPostAt(pos: Int) = posts[pos]
    fun getCurrentPost() = getPostAt(currentPos)

    val subscriptions = HashMap<String, ListenerRegistration>()
    val userSubscriptions = HashMap<String, ListenerRegistration>()

    fun addListener(fragmentName: String, filterBy: String, observer: () -> Unit) {
        Log.d(Constants.TAG, "Adding listener for $fragmentName")
//        posts.clear()
        if(filterBy.equals("Time")){
            val Timesubscription = ref
                .orderBy(Post.CREATED_KEY, Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot: QuerySnapshot?, error:FirebaseFirestoreException? ->
                    error?.let {
                       Log.d(Constants.TAG, "Error: $error")
                       return@addSnapshotListener
                    }
                    Log.d(Constants.TAG, "In snapshot listener with ${snapshot?.size()} docs")
                    posts.clear()
                    snapshot?.documents?.forEach {
                        posts.add(Post.from(it))
                    }
                    observer()
                }
            subscriptions[fragmentName] = Timesubscription
        } else if(filterBy.equals("Author")) {
            val AuthorSubscription = ref
                .whereEqualTo("authorID", Firebase.auth.uid)
                .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    error?.let {
                        Log.d(Constants.TAG, "Error $error")
                        return@addSnapshotListener
                    }
                    Log.d(Constants.TAG, "In snapshot listener with ${snapshot?.size()} docs")
                    posts.clear()
                    snapshot?.documents?.forEach {
                        posts.add(Post.from(it))
                    }
                    observer()
                }
            observer()
            subscriptions[fragmentName] = AuthorSubscription
        }
    }

    fun removeListener(fragmentName: String) {
        Log.d(Constants.TAG, "Removing listeners for $fragmentName")
        subscriptions[fragmentName]?.remove()
        subscriptions.remove(fragmentName)
    }

    fun addPost(post: Post): Boolean {
        val newPost = post
        if(newPost.title.isEmpty() || newPost.content.isEmpty() || newPost.authorID.isEmpty()) {
            // We can't make a post if any of these are empty OR CAN WE
            return false
        } else {
            ref.add(newPost)
            return true
        }
    }

    fun updateCurrentPost(newTitle: String, newContent: String, newAuthor:String, newPhotoURL:String) {
        posts[currentPos].title = newTitle
        posts[currentPos].content = newContent
        posts[currentPos].author = newAuthor
        posts[currentPos].photoUrl = newPhotoURL
        ref.document(getCurrentPost().id).set(getCurrentPost())
        // note you can use update if you want to overwrite only specific fields
    }

    fun removeCurrentPost() {
        ref.document(getCurrentPost().id).delete()
        currentPos = 0
    }

    fun updatePos(pos: Int) {
        currentPos = pos
    }

    fun clear() {
        posts.clear()
        currentPos = 0
    }

    fun size() = posts.size
}