package edu.rosehulman.chronic.adapters
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.models.PainData
import edu.rosehulman.chronic.models.PainDataViewModel

class PainDataAdapter(val fragment: Fragment) : RecyclerView.Adapter<PainDataAdapter.PainDataViewHolder>() {
    val model = ViewModelProvider(fragment.requireActivity()).get(PainDataViewModel::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PainDataViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.calender_item, parent, false)
        return PainDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: PainDataViewHolder, position: Int) {
        holder.bind(model.getObjectAtPosition(position))
    }

    override fun getItemCount(): Int {
        return model.size()
    }

    fun updateDataSet() {
        notifyDataSetChanged()
    }

    fun addObject(ObjectInput: PainData) {
        model.addObject(ObjectInput)
        notifyDataSetChanged()
    }


    fun addModelListener(fragmentName: String, UserID:String){
        model.addListener(fragmentName, UserID){notifyDataSetChanged()}
        Log.d("Chronic","Added Listener in the Adapter")
    }

    fun removeModelListener(fragmentName: String) {
        model.removeListener(fragmentName)
        Log.d("Chronic","Removed Listener in the Adapter")
    }

    fun removeAt(adapterPosition: Int) {
        model.removeAt(adapterPosition)
    }


    inner class PainDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //extract the textviews from the layout and set them to the vars
        val titleText = itemView.findViewById<TextView>(R.id.text_day_description)
        val painLevelText = itemView.findViewById<TextView>(R.id.text_pain_level)
        val startText = itemView.findViewById<TextView>(R.id.text_start_timestamp)
        val endText = itemView.findViewById<TextView>(R.id.text_end_timestamp)


        init {
            itemView.setOnClickListener() {
                //Tracks mapping for index of the particular viewholder, and then  shove it in the right place
                model.updatePosition(adapterPosition)
                Log.d("Chronic","Current Position: $adapterPosition")
            }
            itemView.setOnLongClickListener(){
                model.updatePosition(adapterPosition)
                fragment.findNavController().navigate(R.id.nav_pain_data_entry)
                Log.d("Chronic","Current Position: $adapterPosition")

                true
            }

        }

        fun bind(painObject: PainData) {
            //Bind the model object's specific data to the text view for each
            titleText.text = painObject.title
            painLevelText.text = painObject.painLevel.toString()

            var startDateFormatted = "${painObject.startTime.toDate().month}/${painObject.startTime.toDate().day}/${painObject.startTime.toDate().year}"
            var endDateFormatted = "${painObject.endTime.toDate().month}/${painObject.endTime.toDate().day}/${painObject.endTime.toDate().year}"
            startText.text = startDateFormatted
            endText.text = endDateFormatted


        }
    }




}