package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.rosehulman.chronic.databinding.FragmentLoadingBinding

class LoadingFragment : Fragment(){

    private lateinit var binding: FragmentLoadingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoadingBinding.inflate(inflater,container,false)
        return binding.root
    }
}