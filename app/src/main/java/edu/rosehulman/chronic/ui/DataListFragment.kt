package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.utilities.Constants
import edu.rosehulman.chronic.adapters.PainDataListAdapter
import edu.rosehulman.chronic.utilities.SwipeToDeleteCallback

import edu.rosehulman.chronic.databinding.FragmentDataListBinding
import edu.rosehulman.chronic.models.PainData

class DataListFragment : Fragment() {

    private lateinit var binding: FragmentDataListBinding
    private lateinit var listAdapter: PainDataListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataListBinding.inflate(inflater, container, false)
        val root = binding.root


        //Add recycler view
        listAdapter = PainDataListAdapter(this)
        listAdapter.addModelListener(fragmentName, Firebase.auth.uid!!)
        listAdapter.notifyDataSetChanged()
        //Set Adapter properties
        //Match the adapter class to the xml
        binding.CalenderListRecyclerView.adapter = listAdapter
        //Chose a linear layout manager for rows, grid is for grid
        binding.CalenderListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //Fixed size text view, this makes all happier
        binding.CalenderListRecyclerView.setHasFixedSize(true)
        //Adds nice little gaps around each object in the recylcer view
        binding.CalenderListRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))


        //Handle the Swipe to Delete
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onMove(recyclerView: RecyclerView,  viewHolder: RecyclerView.ViewHolder,  target: RecyclerView.ViewHolder
            ): Boolean {
               //do nothing
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listAdapter.removeAt(viewHolder.adapterPosition)
            }
        }


        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.CalenderListRecyclerView)

        binding.FABListData.setOnClickListener(){
            val defaultEntry = PainData(0,"CHANGEME", Timestamp.now(), Timestamp.now())
            listAdapter.addObject(defaultEntry)
            //Need to update the adapter to tell that it's changed
            listAdapter.notifyDataSetChanged()
            Log.d(Constants.TAG,"CLicked Add New Entry")
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        updateDataSet()
    }

    fun updateDataSet(){
        listAdapter.updateDataSet()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateDataSet()
    }

    companion object{
        const val fragmentName = "DataListFragment"
    }

    override fun onStop() {
        super.onStop()
        listAdapter.removeModelListener(fragmentName = DataCalenderFragment.fragmentName)
    }

}
