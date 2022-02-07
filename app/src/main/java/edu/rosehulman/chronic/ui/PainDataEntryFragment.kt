package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import edu.rosehulman.chronic.databinding.FragmentPainDataEntryBinding
import edu.rosehulman.chronic.models.PainData
import edu.rosehulman.chronic.models.PainDataListViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class PainDataEntryFragment : Fragment() {

    private lateinit var binding: FragmentPainDataEntryBinding
    private lateinit var modelList: PainDataListViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        modelList = ViewModelProvider(requireActivity()).get(PainDataListViewModel::class.java)
        binding = FragmentPainDataEntryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupButtons()
        setupExistingData()

        return root
    }



    fun setupExistingData(){
        val currentObject = modelList.getCurrentObject()

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
        binding.addEntryButton.setOnClickListener(){
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

            val startLocalDateTime:LocalDateTime = LocalDateTime.parse("$startDateString : $startTimeString", DateTimeFormatter.ofPattern("M/d/yyyy : HH:mm:ss"))
            val endLocalDateTime:LocalDateTime = LocalDateTime.parse("$endDateString : $endTimeString", DateTimeFormatter.ofPattern("M/d/yyyy : HH:mm:ss"))

            val startDateTime: Date = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())
            val endDateTime: Date = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())

            val startTimestamp:Timestamp = Timestamp(startDateTime)
            val endTimestamp:Timestamp = Timestamp(endDateTime)

            val newObject: PainData = PainData(painDataInt,title,startTimestamp, endTimestamp)
            modelList.updateCurrentObject(title,painDataInt,startTimestamp,endTimestamp)
            findNavController().popBackStack()
        }
    }











}