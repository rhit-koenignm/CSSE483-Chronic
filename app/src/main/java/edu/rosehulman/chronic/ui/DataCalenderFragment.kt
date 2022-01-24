package edu.rosehulman.chronic.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.adapters.PainDataAdapter
import edu.rosehulman.chronic.adapters.SwipeToDeleteCallback
import edu.rosehulman.chronic.databinding.FragmentDataCalenderBinding
import edu.rosehulman.chronic.models.PainData
import java.util.*


class DataCalenderFragment : Fragment(){

    private lateinit var binding: FragmentDataCalenderBinding
    private lateinit var adapter: PainDataAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataCalenderBinding.inflate(inflater, container, false)
        val root = binding.root


        //Add recycler view
        adapter = PainDataAdapter(this)
        adapter.addModelListener(DataListFragment.fragmentName, Constants.USER_ID)
        //Set Adapter properties
        //Match the adapter class to the xml
        binding.CalenderDataRecyclerView.adapter = adapter
        //Chose a linear layout manager for rows, grid is for grid
        binding.CalenderDataRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //Fixed size text view, this makes all happier
        binding.CalenderDataRecyclerView.setHasFixedSize(true)
        //Adds nice little gaps around each object in the recylcer view
        binding.CalenderDataRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))



        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.CalenderDataRecyclerView)

        updateDataSet()

        binding.FABCalenderData.setOnClickListener(){
            val startTime:Timestamp = Timestamp.now()
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
        const val fragmentName = "DataCalenderFragment"
    }
}

