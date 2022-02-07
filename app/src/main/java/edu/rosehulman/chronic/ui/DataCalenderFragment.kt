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
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.adapters.PainDataCalenderAdapter
import edu.rosehulman.chronic.adapters.SwipeToDeleteCallback
import edu.rosehulman.chronic.databinding.FragmentDataCalenderBinding
import edu.rosehulman.chronic.models.PainData
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class DataCalenderFragment : Fragment(){

    private lateinit var binding: FragmentDataCalenderBinding
    private lateinit var adapter: PainDataCalenderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataCalenderBinding.inflate(inflater, container, false)
        val root = binding.root


        //Add recycler view
        adapter = PainDataCalenderAdapter(this)
        val newDate = Date(binding.CalenderData.date)
        adapter.addDateListener(fragmentName,newDate,Firebase.auth.uid!!){
            adapter.notifyDataSetChanged()
        }
        adapter.notifyDataSetChanged()
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
                //do nothing
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.CalenderDataRecyclerView)

        updateDataSet()

        binding.FABCalenderData.setOnClickListener(){
            val defaultEntry = PainData(0,"CHANGEME", Timestamp.now(), Timestamp.now())
            adapter.addObject(defaultEntry)
            //Need to update the adapter to tell that it's changed
            adapter.notifyDataSetChanged()
            Log.d(Constants.TAG,"Clicked Add New Entry")
        }

            //Handle the use of the calander itself for filtering
        binding.CalenderData.setOnDateChangeListener { view, year, month, dayOfMonth ->
            Log.d(Constants.TAG,"The date callback is ${month+1}/$dayOfMonth/$year")
            var calender = GregorianCalendar(year, month, dayOfMonth)
            var newDate = Date(calender.timeInMillis)

            Log.d(Constants.TAG,"New Date Selected: ${newDate.toString()}")

            adapter.removeModelListener(fragmentName)
            adapter.addDateListener(fragmentName,newDate,Firebase.auth.uid!!){adapter.notifyDataSetChanged()}
            adapter.notifyDataSetChanged()


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
    override fun onStop() {
        super.onStop()
        adapter.removeModelListener(fragmentName)
    }

}

