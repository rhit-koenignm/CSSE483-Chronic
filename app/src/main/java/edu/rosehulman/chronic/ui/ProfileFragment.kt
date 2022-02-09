package edu.rosehulman.chronic.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.adapters.MyTagAdapter
import edu.rosehulman.chronic.databinding.FragmentProfileBinding
import edu.rosehulman.chronic.models.UserData
import edu.rosehulman.chronic.models.UserViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var model: UserViewModel
    private lateinit var adapter: MyTagAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {
        model = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Add recycler view
        adapter = MyTagAdapter(this, fragmentName)

        //Set Adapter properties

        //Match the adapter class to the xml
        binding.TagsRecyclerView.adapter = adapter
        //Chose a linear layout manager for rows, grid is for grid
        //binding.TagsRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        //Fixed size text view, this makes all happier
        binding.TagsRecyclerView.setHasFixedSize(true)
        //Adds nice little gaps around each object in the recycler view
        binding.TagsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        binding.TagsRecyclerView.layoutManager = WrapContentLinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        // Now that we have set up our adapter, we need to set up our listeners
        adapter.addUserListener(fragmentName){
            adapter.addListener(fragmentName, "MyTags")
        }


        readFromFireStore()
        setUpButtons()
        return root
    }

    fun setUpButtons() {
        binding.trackingTagsButton.setOnClickListener {
            findNavController().navigate(R.id.nav_my_tags)
        }
        binding.paindataButton.setOnClickListener() {
            findNavController().navigate(R.id.nav_data)
        }
        binding.settingsButton.setOnClickListener() {
            findNavController().navigate(R.id.nav_settings)
        }
        binding.SubmitPainTrackingButton.setOnClickListener{
            findNavController().navigate(R.id.nav_pain_data_entry)
        }
        binding.LogoutButton.setOnClickListener(){
            Firebase.auth.signOut()
        }

    }

    fun readFromFireStore() {
        var user = UserData()
        Firebase.firestore.collection(UserData.COLLECTION_PATH).document(Firebase.auth.uid!!).get().addOnSuccessListener { snapshot:DocumentSnapshot ->
            user = snapshot.toObject(UserData::class.java)!!
            binding.Email.text = user.Email
            "${user.firstName} ${user.lastName}".also { binding.Name.text = it }
            binding.ProfilePhoto.load(user.ProfileURL){
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }

    }

    // When the fragment is destroyed, remove our listeners
    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeUserListener(fragmentName)
        adapter.removeListener(fragmentName)
    }

    companion object {
        const val fragmentName = "ProfileFragment"
    }



    inner class WrapContentLinearLayoutManager : LinearLayoutManager {
        constructor(context: Context?) : super(context) {}
        constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
        ) {
        }

        constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
        ) : super(context, attrs, defStyleAttr, defStyleRes) {
        }

        override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("TAG", "meet a IOOBE in RecyclerView")
            }
        }
    }

}
