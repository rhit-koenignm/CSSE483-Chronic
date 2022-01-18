package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.chronic.adapters.PainDataCalenderAdapter
import edu.rosehulman.chronic.adapters.PainDataListAdapter
import edu.rosehulman.chronic.databinding.FragmentDataCalenderBinding
import edu.rosehulman.chronic.databinding.FragmentDataListBinding
import edu.rosehulman.chronic.models.PainData
import java.util.*

class DataListFragment : Fragment(){

    private lateinit var binding: FragmentDataListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataListBinding.inflate(inflater, container, false)
        val root = binding.root


        //Add recycler view
        val adapter = PainDataListAdapter(this)
        //Set Adapter properties
        //Match the adapter class to the xml
        binding.CalenderListRecyclerView.adapter = adapter
        //Chose a linear layout manager for rows, grid is for grid
        binding.CalenderListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //Fixed size text view, this makes all happier
        binding.CalenderListRecyclerView.setHasFixedSize(true)
        //Adds nice little gaps around each object in the recylcer view
        binding.CalenderListRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))


        binding.FABListData.setOnClickListener(){
            var newObject: PainData = PainData("ChangeME",-1, Date())
            adapter.addObject(newObject)
        }


        return root
    }
}
