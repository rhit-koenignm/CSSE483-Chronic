package edu.rosehulman.chronic.ui

import android.app.AlertDialog
import android.icu.lang.UCharacter
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.adapters.MyTagAdapter
import edu.rosehulman.chronic.databinding.FragmentMyTagsBinding
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding

class MyTagsFragment: Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentMyTagsBinding
    lateinit var adapter: MyTagAdapter
    var currentType: String = "All"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTagsBinding.inflate(inflater, container, false)

        // This handles the creation and setup for our spinner that handles tag types
        val tagTypes = resources.getStringArray(R.array.tag_types)
        val typeSpinner: Spinner = binding.tagTypeDropdown
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tag_filter_types,
            android.R.layout.simple_spinner_item
        ).also { filterAdapter ->
            //Specify the layout to use when the list of choices appears
            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = filterAdapter
        }

        adapter = MyTagAdapter(this)
        binding.myTagsRecyler.adapter = adapter
        binding.myTagsRecyler.setHasFixedSize(true)
        binding.myTagsRecyler.layoutManager = LinearLayoutManager(requireContext())
        adapter.addListener(fragmentName, Constants.USER_ID, currentType)

        binding.tagsFab.setOnClickListener {
            Log.d(Constants.TAG, "Pressed tags fab")
            pressedTagsFab()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeListener(fragmentName)
    }

    fun pressedTagsFab() {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    companion object {
        const val fragmentName = "MyTagsFragment"
    }

}