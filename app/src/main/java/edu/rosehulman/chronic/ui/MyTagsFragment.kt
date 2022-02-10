package edu.rosehulman.chronic.ui

import android.app.AlertDialog
import android.content.Context
import android.icu.lang.UCharacter
import android.os.Bundle
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.adapters.MyTagAdapter
import edu.rosehulman.chronic.databinding.FragmentMyTagsBinding
import edu.rosehulman.chronic.databinding.FragmentPaintrackingBinding

// Author: Natalie Koenig
// Description: The fragment that will allow the user to see their tags, add/remove them, or create new tags
// Date: 1/31/2022
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

        adapter = MyTagAdapter(this, fragmentName)
        binding.myTagsRecyler.adapter = adapter
        binding.myTagsRecyler.setHasFixedSize(true)
        binding.myTagsRecyler.layoutManager = LinearLayoutManager(requireContext())
        //binding.myTagsRecyler.layoutManager = WrapContentLinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        //adapter.addUserListener(fragmentName)

        adapter.addListener(fragmentName, currentType)

        binding.tagsFab.setOnClickListener {
            Log.d(Constants.TAG, "Pressed tags fab")
            pressedTagsFab()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //adapter.removeUserListener(fragmentName)
        adapter.removeListener(fragmentName)
    }

    fun pressedTagsFab() {
        Log.d(Constants.TAG, "Pressed tags fab, inside function")
        adapter.showEditDialog(this.requireContext(),null, currentType)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        adapter.removeListener(fragmentName)
        var type = parent?.getItemAtPosition(pos)
        Log.d(Constants.TAG, "Getting spinner item selected $type")
        if(type == null) {
            //Do nothing
        } else{
            currentType = type.toString()
            var tagFiltersArray = this.resources.getStringArray(R.array.tag_filter_types)
            var tagTypesArray = this.resources.getStringArray(R.array.tag_types)

            var passedInType =
                when (currentType) {
                    tagFiltersArray[0] -> "All"
                    tagFiltersArray[2] -> tagTypesArray[0]
                    tagFiltersArray[3] -> tagTypesArray[1]
                    tagFiltersArray[4] -> tagTypesArray[2]
                    else -> {
                        "MyTags"
                    }
                }
            adapter.removeListener(fragmentName)
            adapter.addListener(fragmentName, passedInType)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    companion object {
        const val fragmentName = "MyTagsFragment"
    }

    inner class WrapContentLinearLayoutManager : LinearLayoutManager {
        constructor(context: Context?) : super(context) {}
        constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
        ) {
        }

        constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
        ) : super(context, attrs, defStyleAttr, defStyleRes) {
        }

        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("TAG", "meet a IOOBE in RecyclerView")
            }
        }
    }

}