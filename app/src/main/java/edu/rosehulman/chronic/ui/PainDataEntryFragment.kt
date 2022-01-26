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

        passInExistingData()
        setupButtons()

        return root
    }

    fun passInExistingData(){
        if(model.size() < 1){
            return
        }
        var currentObject = model.getCurrentObject()

        binding.titleInput.setText(currentObject.title)
        var startDateFormatted = "${currentObject.startTime.toDate().month}/${currentObject.startTime.toDate().day}/${currentObject.startTime.toDate().year}"
        var endDateFormatted = "${currentObject.endTime.toDate().month}/${currentObject.endTime.toDate().day}/${currentObject.endTime.toDate().year}"
        binding.startTimeTextInput.setText(startDateFormatted)
        binding.endTimeTextInput.setText(endDateFormatted)
        binding.painLevelInput.setText(currentObject.painLevel.toString())

    }





    fun setupButtons(){
        binding.addEntryButton.setOnClickListener(){
            var title: String = binding.titleInput.text.toString()
            var startTimeString: String = binding.startTimeTextInput.text.toString()
            var endTimeString: String = binding.endTimeTextInput.text.toString()
            var painDataString: String = binding.painLevelInput.text.toString()

            //Check for nulls, and replace them with default values
            if(title == ""){
                title = "No Title Entered"
            }
            if(startTimeString == ""){
                startTimeString = "1/1/1970"
            }
            if(endTimeString == ""){
                endTimeString = "1/1/1970"
            }
            if(painDataString == ""){
                painDataString = "0"
            }


            Log.d("Chronic","Title: $title, ST: $startTimeString, ET: $endTimeString, PD: $painDataString")

            var startTime: Timestamp = Timestamp.now()
            var endTime: Timestamp = Timestamp.now()

            //TODO Tags + adding time
            try {
                Timestamp(SimpleDateFormat("dd/MM/yyyy").parse(startTimeString)!!).also { startTime = it }
            } catch (e: Error){
                Log.d("Chronic","$e")
            }

            try {
                Timestamp(SimpleDateFormat("dd/MM/yyyy").parse(endTimeString)!!).also { endTime = it }
            } catch (e: Error){
                Log.d("Chronic","$e")
            }



            //Parse the Pain data Integer
            var painDataInt = 0
            try {
                painDataInt = painDataString.toInt()
            } catch (e: NumberFormatException) {
                painDataInt = 0
            }



            val newObject: PainData = PainData(painDataInt,title,startTime, endTime)
            model.addObject(newObject)
            findNavController().popBackStack()
        }
    }

}