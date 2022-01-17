package edu.rosehulman.chronic.models

import androidx.lifecycle.ViewModel
import java.sql.Time


class PainDataViewModel : ViewModel() {
        var objectList = ArrayList<PainData>()
        var currentPosition = 0         //Defaults to looking at the zeroth position

        fun getObjectAtPosition(position: Int) = objectList[position]
        fun getCurrentObject() = getObjectAtPosition(currentPosition)

        fun addObject(objectInput: PainData){
            objectList.add(objectInput)
        }

        fun updateCurrentObject(title: String, painvalue: Int, timeValue:Time){
            objectList[currentPosition].Title = title
            objectList[currentPosition].PainValue = painvalue
            objectList[currentPosition].Time = timeValue
        }

        fun removeCurrentObject(){
            objectList.removeAt(currentPosition)
            currentPosition = 0
        }

        fun updatePosition(position: Int){
            currentPosition = position
        }

        fun size() = objectList.size
    }