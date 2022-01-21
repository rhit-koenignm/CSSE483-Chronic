package edu.rosehulman.chronic.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.chronic.adapters.PainDataAdapter
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
        //Set Adapter properties
        //Match the adapter class to the xml
        binding.CalenderDataRecyclerView.adapter = adapter
        //Chose a linear layout manager for rows, grid is for grid
        binding.CalenderDataRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //Fixed size text view, this makes all happier
        binding.CalenderDataRecyclerView.setHasFixedSize(true)
        //Adds nice little gaps around each object in the recylcer view
        binding.CalenderDataRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        updateDataSet()

        binding.FABCalenderData.setOnClickListener(){
            val newObject: PainData = PainData("ChangeME",-1, Date())
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
}

