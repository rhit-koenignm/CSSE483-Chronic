package edu.rosehulman.chronic.models

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import java.sql.Time


class PainDataViewModel : ViewModel() {
        var objectList = ArrayList<PainData>()
        var currentPosition = 0         //Defaults to looking at the zeroth position

        fun getObjectAtPosition(position: Int) = objectList[position]
        fun getCurrentObject() = getObjectAtPosition(currentPosition)

        fun addObject(objectInput: PainData){
            objectList.add(objectInput)
        }

        fun updateCurrentObject(title: String, painvalue: Int, startTime:Timestamp,  endTime:Timestamp){
            objectList[currentPosition].title = title
            objectList[currentPosition].painLevel = painvalue
            objectList[currentPosition].startTime = startTime
            objectList[currentPosition].endTime = endTime
        }

        fun removeCurrentObject(){
            objectList.removeAt(currentPosition)
            currentPosition = 0
        }

        fun removeAtPosition(position:Int){
            objectList.removeAt(position)
            currentPosition = 0
        }

        fun updatePosition(position: Int){
            currentPosition = position
        }

        fun size() = objectList.size
    }