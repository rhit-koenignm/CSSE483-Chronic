package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.rosehulman.chronic.databinding.FragmentForumHomeBinding
import edu.rosehulman.chronic.databinding.FragmentLoadingBinding

class GlobalForumFragment: Fragment() {
    private lateinit var binding: FragmentForumHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForumHomeBinding.inflate(inflater,container,false)
        return binding.root
    }
}