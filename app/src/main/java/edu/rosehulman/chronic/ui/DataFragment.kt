package edu.rosehulman.chronic.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.chronic.adapters.PainDataAdapter
import edu.rosehulman.chronic.databinding.FragmentDataBinding
import edu.rosehulman.chronic.models.PainData
import java.util.*

class DataFragment : Fragment() {

    private lateinit var binding: FragmentDataBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Add recycler view
        val adapter = PainDataAdapter(this)
        //Set Adapter properties
        //Match the adapter class to the xml
        binding.CalenderDataRecyclerView.adapter = adapter
        //Chose a linear layout manager for rows, grid is for grid
        binding.CalenderDataRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //Fixed size text view, this makes all happier
        binding.CalenderDataRecyclerView.setHasFixedSize(true)
        //Adds nice little gaps around each object in the recylcer view
        binding.CalenderDataRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))


        binding.FABCalenderData.setOnClickListener(){
            var newObject:PainData = PainData("ChangeME",-1,Date())
            adapter.addObject(newObject)
        }


        return root
    }
}