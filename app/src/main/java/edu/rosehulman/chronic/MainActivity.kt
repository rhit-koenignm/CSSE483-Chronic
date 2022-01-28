package edu.rosehulman.chronic

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.databinding.ActivityMainBinding
import edu.rosehulman.chronic.models.UserData

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    val signinLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { /* empty since the auth listener already responds .*/ }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        Log.d(Constants.TAG,"Started App")
        initializeAuthenticationListener(navView)
        Log.d(Constants.TAG,"Auth Complete")


            //Top level destinations get a menu, and all others get back buttons in the app bar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_reminder_list, R.id.nav_pain_tracking, R.id.nav_data
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setupHeaderBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    fun setupHeaderBar(){
            val navigationView = binding.navView
            val headerView = navigationView.getHeaderView(0)
    }

    private fun initializeAuthenticationListener(navView: NavigationView) {
        authStateListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser

            //Check to see if the user is signed in
            if(user == null){
                Log.d(Constants.TAG,"No User Signed in")
                setupAuthUI()
                //If the user is new, add it to firebase.
                pushUserToFireBase()
                //Otherwise, just don't?
            }else{
                Log.d(Constants.TAG,"User Signed in")
                //If the user is new, add it to firebase.
                pushUserToFireBase()
                //Otherwise, just don't?
                setupFromFirebase(navView)
                with(user){
                    Log.d(Constants.TAG, "User: $uid, Email: $email, Displayname: $displayName, PhotoURL: $photoUrl")
                }
            }
        }
    }



    //Add and Remove Authentication State Listeners
    override fun onStart() {
        super.onStart()
        Firebase.auth.addAuthStateListener(authStateListener)

    }

    override fun onStop() {
        super.onStop()
        Firebase.auth.removeAuthStateListener(authStateListener)
    }



    private fun setupAuthUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signinIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .setTheme(R.style.Theme_Chronic)
            .setLogo(R.mipmap.chronic_logo)
            .build()
        signinLauncher.launch(signinIntent)
    }


    fun setupFromFirebase(navView: NavigationView) {
        val headerView = navView.getHeaderView(0)
        val profileImage = headerView.findViewById<ImageView>(R.id.profile_imageView)
        val emailText = headerView.findViewById<TextView>(R.id.email_textView)
        val nameText = headerView.findViewById<TextView>(R.id.name_textview)


        var user = UserData()

        if (Firebase.auth.currentUser != null) {
            Log.d(Constants.TAG,"User is Defined for Setup from Firebase")
            Firebase.firestore.collection(UserData.COLLECTION_PATH).document(Firebase.auth.currentUser?.uid!!).get()
                .addOnSuccessListener { snapshot: DocumentSnapshot? ->
                    if (snapshot != null) {
                        user = snapshot.toObject(UserData::class.java)!!

                        emailText.text = user.Email
                        "${user.firstName} ${user.lastName}".also { nameText.text = it }
                        profileImage.load(user.ProfileURL) {
                            crossfade(true)
                            transformations(CircleCropTransformation())
                        }
                    }
                }
        }
    }

    fun  pushUserToFireBase(){
        with(Firebase.auth.currentUser){

            val username = "Replace Me"
            val photoURLInput = this?.photoUrl.toString()
            val firstNameInput = this?.displayName?.split(" ")?.get(0)
            val lastNameInput = this?.displayName?.split(" ")?.get(1)
            val emailInput = this?.email

            val photoURL = photoURLInput ?: "No URL"
            val firstName = firstNameInput ?: "No First Name"
            val lastName = lastNameInput ?: "No Last Name"
            val email = emailInput ?: "No Email"

            val newUser = UserData(photoURL,username,firstName,lastName, email)

            //Push to the cloud if the document ID does not already exist

            this?.let { Firebase.firestore.collection(UserData.COLLECTION_PATH).document(it.uid).set(newUser) }

//            var userDocument = Firebase.firestore.collection(UserData.COLLECTION_PATH).document(this?.uid!!).get().addOnSuccessListener { snapshot: DocumentSnapshot? ->
//                if(snapshot?.exists() == true){
//                    // do nothing
//                    Log.d(Constants.TAG,"User Already Exists")
//                }else{
//                    Firebase.firestore.collection(UserData.COLLECTION_PATH).document(this.uid).set(newUser)
//
//                    Log.d(Constants.TAG,"New User Added")
//                }
//
//                }
        }
    }
}