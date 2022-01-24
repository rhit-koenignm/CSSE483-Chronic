package edu.rosehulman.chronic.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.databinding.FragmentPainDataEntryBinding
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding


class PainDataEntryFragment : Fragment() {

    private lateinit var binding: FragmentPainDataEntryBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPainDataEntryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

}