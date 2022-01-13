package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.rosehulman.chronic.databinding.FragmentReminderDetailBinding

class ReminderDetailFragment: Fragment() {

    private lateinit var binding: FragmentReminderDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReminderDetailBinding.inflate(inflater, container, false)

        return binding.root
    }
}