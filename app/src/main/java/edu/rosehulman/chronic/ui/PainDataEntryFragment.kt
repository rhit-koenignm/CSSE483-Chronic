package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Timestamp
import edu.rosehulman.chronic.utilities.Constants
import edu.rosehulman.chronic.adapters.PainDataEntryTagAdapter
import edu.rosehulman.chronic.databinding.FragmentPainDataEntryBinding
import edu.rosehulman.chronic.models.PainDataEntryTagViewModel
import edu.rosehulman.chronic.models.PainDataViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class PainDataEntryFragment : Fragment() {

    private lateinit var binding: FragmentPainDataEntryBinding
    private lateinit var painDataModel: PainDataViewModel
    private lateinit var myPainDataEntryTagViewModel: PainDataEntryTagViewModel
    private  var listOfAdapters =  ArrayList<PainDataEntryTagAdapter>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        painDataModel = ViewModelProvider(requireActivity()).get(PainDataViewModel::class.java)
        myPainDataEntryTagViewModel = ViewModelProvider(requireActivity()).get(PainDataEntryTagViewModel::class.java)
        binding = FragmentPainDataEntryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Pull In Existing Data, and handle the adapter setup
        setupExistingData()

        //Add the listener for the user my tags, and populate it in the
        myPainDataEntryTagViewModel.addUserListener(fragmentName){
            myPainDataEntryTagViewModel.addMyTagsByTypeListener(fragmentName){
                pullInExistingMyTags()
        }
        }

        //Setup Button Callbacks
        setupButtons()




        return root
    }

    private fun pullInExistingMyTags() {
        Log.d(Constants.TAG,"Setting up all three tag adapters")
        val treatmentsAdapter = PainDataEntryTagAdapter(this, fragmentName, "Treatments")
        listOfAdapters.add(treatmentsAdapter)
        treatmentsAdapter.notifyDataSetChanged()
        binding.treatmentsRecyclerView.adapter = treatmentsAdapter
        binding.treatmentsRecyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        binding.treatmentsRecyclerView.setHasFixedSize(true)
        binding.treatmentsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.treatmentsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))

        val triggersAdapter = PainDataEntryTagAdapter(this, fragmentName, "Triggers")
        listOfAdapters.add(triggersAdapter)
        triggersAdapter.notifyDataSetChanged()
        binding.triggersRecyclerView.adapter = triggersAdapter
        binding.triggersRecyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        binding.triggersRecyclerView.setHasFixedSize(true)
        binding.triggersRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.triggersRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))

        val symptomsAdapter = PainDataEntryTagAdapter(this, fragmentName, "Symptoms")
        listOfAdapters.add(symptomsAdapter)
        symptomsAdapter.notifyDataSetChanged()
        binding.symptomsRecyclerView.adapter = symptomsAdapter
        binding.symptomsRecyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        binding.symptomsRecyclerView.setHasFixedSize(true)
        binding.symptomsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.symptomsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
    }


    fun setupExistingData(){
        val currentObject = painDataModel.getCurrentObject()

        //Pass over the list of attached tags to the viewmodel
        myPainDataEntryTagViewModel.updateAttachedTags(currentObject.attachedTags)


        //Bind the model object's specific data to the text view for each
        binding.titleInput.setText(currentObject.title)
        binding.painLevelInput.setText(currentObject.painLevel.toString())
        val startDate:LocalDateTime = convertToLocalDateViaInstant(currentObject.startTime.toDate())
        val endDate:LocalDateTime = convertToLocalDateViaInstant(currentObject.endTime.toDate())

        val startDateFormatted = "${startDate.monthValue}/${startDate.dayOfMonth}/${startDate.year}"
        val startTimeFormatted = "${startDate.hour}:${startDate.minute}:${startDate.second}"
        val endDateFormatted ="${endDate.monthValue}/${endDate.dayOfMonth}/${endDate.year}"
        val endTimeFormatted ="${endDate.hour}:${endDate.minute}:${endDate.second}"

        binding.startDateTextInput.setText(startDateFormatted)
        binding.startTimeTextInput.setText(startTimeFormatted)

        binding.endDateTextInput.setText(endDateFormatted)
        binding.endTimeTextInput.setText(endTimeFormatted)
    }

    fun convertToLocalDateViaInstant(dateToConvert: Date): LocalDateTime {
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    fun setupButtons(){
        binding.SubmitButton.setOnClickListener(){
            var title: String = binding.titleInput.text.toString()
            var painDataString: String = binding.painLevelInput.text.toString()
            var startDateString: String = binding.startDateTextInput.text.toString()
            var endDateString: String = binding.endDateTextInput.text.toString()
            var startTimeString: String = binding.startTimeTextInput.text.toString()
            var endTimeString: String = binding.endTimeTextInput.text.toString()


            //Handle empty string inputs
            var painDataInt:Int = 0
            if(painDataString != ""){
                painDataInt = painDataString.toInt()
            }

            if(startDateString == ""){
                startDateString = "1/1/1970"
            }

            if(endDateString == ""){
                endDateString = "1/1/1970"
            }

            if(startTimeString == ""){
                startTimeString = "00:00:00"
            }

            if(endTimeString == ""){
                endTimeString = "00:00:00"
            }

            val startLocalDateTime:LocalDateTime = LocalDateTime.parse("$startDateString : $startTimeString", DateTimeFormatter.ofPattern("M/d/yyyy : H:m:s"))
            val endLocalDateTime:LocalDateTime = LocalDateTime.parse("$endDateString : $endTimeString", DateTimeFormatter.ofPattern("M/d/yyyy : H:m:s"))

            val startDateTime: Date = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())
            val endDateTime: Date = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())

            val startTimestamp:Timestamp = Timestamp(startDateTime)
            val endTimestamp:Timestamp = Timestamp(endDateTime)


            //Handle the tracking and untracking of tags
            var trackedEntryTags = myPainDataEntryTagViewModel.attachedTag;


            painDataModel.updateCurrentObject(title,painDataInt,startTimestamp,endTimestamp,trackedEntryTags)
            findNavController().popBackStack()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        myPainDataEntryTagViewModel.removeTagsByTypeListener(fragmentName)
        myPainDataEntryTagViewModel.removeUserListener(fragmentName)
    }


    companion object {
        const val fragmentName = "PainDataEntryFragment"
    }










}