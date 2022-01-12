package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding

class PainTrackingFragment : Fragment() {

    private lateinit var binding: FragmentPaintrackingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaintrackingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textPaintracking.text = "PainTracking"
        return root
    }
}