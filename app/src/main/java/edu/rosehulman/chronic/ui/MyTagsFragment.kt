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
        typeSpinner.onItemSelectedListener = this

        adapter = MyTagAdapter(this)
        binding.myTagsRecyler.adapter = adapter
        binding.myTagsRecyler.setHasFixedSize(true)
        binding.myTagsRecyler.layoutManager = LinearLayoutManager(requireContext())

        Log.d(Constants.TAG, "About to start adding listeners in the fragment")
        adapter.addUserListener(fragmentName, Constants.USER_ID)
        adapter.addListener(fragmentName, Constants.USER_ID, currentType)

        binding.tagsFab.setOnClickListener {
            Log.d(Constants.TAG, "Pressed tags fab")
            pressedTagsFab()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeUserListener(fragmentName)
        adapter.removeListener(fragmentName)
    }

    fun pressedTagsFab() {
        Log.d(Constants.TAG, "Pressed tags fab, inside function")
        adapter.showEditDialog(this.requireContext(),null, currentType)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        //adapter.removeListener(fragmentName)
        var type = parent?.getItemAtPosition(pos)
        Log.d(Constants.TAG, "Getting spinner item selected $type")
        if(type == null) {
            //Do nothing
        } else{
            currentType = type.toString()
            adapter.removeListener(fragmentName)
            adapter.addListener(fragmentName, Constants.USER_ID, currentType)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    companion object {
        const val fragmentName = "MyTagsFragment"
    }

}