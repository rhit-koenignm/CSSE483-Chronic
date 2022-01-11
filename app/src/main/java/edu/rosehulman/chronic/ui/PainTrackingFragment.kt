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
import edu.rosehulman.chronic.databinding.FragmentHomeBinding
import edu.rosehulman.chronic.databinding.FragmentSlideshowBinding

class PainTrackingFragment : Fragment() {

    private lateinit var binding: FragmentSlideshowBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textSlideshow.text = "PainTracking"
        return root
    }
}