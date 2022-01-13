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
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.databinding.FragmentProfileBinding
import edu.rosehulman.chronic.models.UserViewModel

class ProfileFragment : Fragment() {


    private lateinit var binding: FragmentProfileBinding
    private lateinit var model: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {
        model = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setUpButtons()
        updateView()
        return root

    }

    fun setUpButtons() {
        binding.trackingTagsButton.setOnClickListener {
            findNavController().navigate(R.id.nav_my_tags)
        }
        binding.paindataButton.setOnClickListener(){
            findNavController().navigate(R.id.nav_pain_tracking)
        }
        binding.settingsButton.setOnClickListener(){
            findNavController().navigate(R.id.nav_settings)
        }
    }

    fun updateView(){

        //storedImage.load(photoObject.url) {
        //    crossfade(true)
        //    transformations(CircleCropTransformation())

        }
    }
