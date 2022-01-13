package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.databinding.FragmentMyTagsBinding
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding

class MyTagsFragment: Fragment() {
    private lateinit var binding: FragmentMyTagsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTagsBinding.inflate(inflater, container, false)


        val tagTypes = resources.getStringArray(R.array.tag_types)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item,tagTypes)
        val autoCompleteText = binding.tagTypeDropdownText
        autoCompleteText.setAdapter(arrayAdapter)

        return binding.root
    }
}