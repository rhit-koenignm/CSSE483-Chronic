package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import edu.rosehulman.chronic.databinding.FragmentPainDataEntryBinding
import edu.rosehulman.chronic.models.PainData
import edu.rosehulman.chronic.models.PainDataViewModel
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PainDataEntryFragment : Fragment() {

    private lateinit var binding: FragmentPainDataEntryBinding
    private lateinit var model: PainDataViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(PainDataViewModel::class.java)
        binding = FragmentPainDataEntryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupButtons()

        return root
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
                startDateString = "01/01/1970"
            }

            if(endDateString == ""){
                endDateString = "01/01/1970"
            }

            if(startTimeString == ""){
                startTimeString = "00:00:00"
            }

            if(endTimeString == ""){
                endTimeString = "00:00:00"
            }



            var startLocalDateTime:LocalDateTime = LocalDateTime.parse("$startDateString : $startTimeString", DateTimeFormatter.ofPattern("dd/MM/yyyy : HH:mm:ss"))
            var endLocalDateTime:LocalDateTime = LocalDateTime.parse("$endDateString : $endTimeString", DateTimeFormatter.ofPattern("dd/MM/yyyy : HH:mm:ss"))
            val newObject: PainData = PainData(painDataInt,title,startLocalDateTime, endLocalDateTime)
            model.addObject(newObject)
            findNavController().popBackStack()
        }
    }

}