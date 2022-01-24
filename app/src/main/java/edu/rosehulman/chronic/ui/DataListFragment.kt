package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.adapters.PainDataAdapter
import edu.rosehulman.chronic.adapters.SwipeToDeleteCallback
import edu.rosehulman.chronic.databinding.FragmentDataListBinding
import edu.rosehulman.chronic.models.PainData

class DataListFragment : Fragment(){

    private lateinit var binding: FragmentDataListBinding
    private lateinit var adapter: PainDataAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataListBinding.inflate(inflater, container, false)
        val root = binding.root


        //Add recycler view
        adapter = PainDataAdapter(this)
        adapter.addModelListener(fragmentName,Constants.USER_ID)
        //Set Adapter properties
        //Match the adapter class to the xml
        binding.CalenderListRecyclerView.adapter = adapter
        //Chose a linear layout manager for rows, grid is for grid
        binding.CalenderListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //Fixed size text view, this makes all happier
        binding.CalenderListRecyclerView.setHasFixedSize(true)
        //Adds nice little gaps around each object in the recylcer view
        binding.CalenderListRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onMove(recyclerView: RecyclerView,  viewHolder: RecyclerView.ViewHolder,  target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.CalenderListRecyclerView)

        binding.FABListData.setOnClickListener(){
            val startTime: Timestamp = Timestamp.now()
            val endTime: Timestamp = Timestamp.now()
            val newObject: PainData = PainData(0,"Brain Pain",startTime, endTime)
            adapter.addObject(newObject)
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        updateDataSet()
    }

    fun updateDataSet(){
        adapter.updateDataSet()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateDataSet()
    }

    companion object{
        const val fragmentName = "DataListFragment"
    }
}
