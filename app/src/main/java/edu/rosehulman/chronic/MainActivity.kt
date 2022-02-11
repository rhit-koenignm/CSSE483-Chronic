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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import coil.load
import coil.transform.CircleCropTransformation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.databinding.ActivityMainBinding
import edu.rosehulman.chronic.models.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
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
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        Log.d(Constants.TAG,"Started App")
        initializeAuthenticationListener()
        Log.d(Constants.TAG,"Auth Complete")


            //Top level destinations get a menu, and all others get back buttons in the app bar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_reminder_list, R.id.nav_pain_tracking, R.id.nav_data, R.id.nav_settings, R.id.nav_forum_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        setupHeaderBar()
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


    fun setupHeaderBar(userModel: UserViewModel) {
            val navigationView = binding.navView
            val headerView = navigationView.getHeaderView(0)
            val profileImage = headerView.findViewById<ImageView>(R.id.profile_imageView)
            val profileEmail = headerView.findViewById<TextView>(R.id.email_textView)
            val profileName = headerView.findViewById<TextView>(R.id.name_textview)

            profileImage.load(userModel.user?.ProfileURL){
                crossfade(true)
                transformations(CircleCropTransformation())
            }

            profileEmail.text = "${userModel.user?.Email}"
            profileName.text = "${userModel.user?.firstName} ${userModel.user?.lastName}"
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

    private fun initializeAuthenticationListener() {
        authStateListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser

            //Check to see if the user is signed in
            if(user == null){
                Log.d(Constants.TAG,"No User Signed in")
                setupAuthUI()
            }else{
                Log.d(Constants.TAG,"User Signed in")
                with(user){
                    Log.d(Constants.TAG, "User: $uid, Email: $email, Displayname: $displayName, PhotoURL: $photoUrl")
                }
                val userModel = ViewModelProvider(this).get(UserViewModel::class.java)
                userModel.getOrMakeUser{
                    //Not an instant call, so this can cause a race condition that really isn't great
                    //This is a callback function that is being passed into, and will trigger internally in the function
                    //Wow non-linear code

                    if(userModel.hasCompletedSetup()){
                        //Navigate to the quotes list page when adding the splash screen
                        navController.navigate(R.id.nav_profile)
                        setupHeaderBar(userModel)
                    }else{
                        //Navigate to the settings page to fill out all of the user data
                        navController.navigate(R.id.nav_user_edit)
                    }
                }
            }
        }
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

}