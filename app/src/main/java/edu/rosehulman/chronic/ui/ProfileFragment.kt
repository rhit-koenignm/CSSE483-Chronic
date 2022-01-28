package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.adapters.MyTagAdapter
import edu.rosehulman.chronic.adapters.PainDataAdapter
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

        binding.TagsRecyclerView.adapter

        //Add recycler view
        adapter = MyTagAdapter(this)
        //Set Adapter properties
        //Match the adapter class to the xml
        binding.TagsRecyclerView.adapter = adapter
        //Chose a linear layout manager for rows, grid is for grid
        binding.TagsRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        //Fixed size text view, this makes all happier
        binding.TagsRecyclerView.setHasFixedSize(true)
        //Adds nice little gaps around each object in the recylcer view
        binding.TagsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))


        setUpButtons()
        readFromFireStore()
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
    }


    fun readFromFireStore() {
        var user = UserData()
        Firebase.firestore.collection("users").get()
            .addOnSuccessListener { snapshot: QuerySnapshot? ->
                snapshot?.documents?.forEach {
                    user = it.toObject(UserData::class.java)!!
                }

                binding.Email.text = user.Email
                "${user.firstName} ${user.lastName}".also { binding.Name.text = it }
                binding.ProfilePhoto.load(user.ProfileURL)
            }
    }
}
