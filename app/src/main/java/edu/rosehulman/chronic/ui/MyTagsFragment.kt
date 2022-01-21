package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.adapters.MyTagAdapter
import edu.rosehulman.chronic.databinding.FragmentMyTagsBinding
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding

class MyTagsFragment: Fragment() {
    private lateinit var binding: FragmentMyTagsBinding
    lateinit var adapter: MyTagAdapter

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

        adapter = MyTagAdapter(this)
        binding.myTagsRecyler.adapter = adapter
        binding.myTagsRecyler.setHasFixedSize(true)
        binding.myTagsRecyler.layoutManager = LinearLayoutManager(requireContext())
        adapter.addListener(fragmentName, Constants.USER_ID, "All")

        binding.tagsFab.setOnClickListener {
            Log.d(Constants.TAG, "Pressed tags fab")
            // Here is where we'll put a dialog for adding new tags
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeListener(fragmentName)
    }

    companion object {
        const val fragmentName = "MyTagsFragment"
    }
}