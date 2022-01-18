package edu.rosehulman.chronic.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.models.PainData
import edu.rosehulman.chronic.models.PainDataViewModel

class PainDataAdapter(val fragment: Fragment) : RecyclerView.Adapter<PainDataAdapter.PainDataViewHolder>() {
    val model = ViewModelProvider(fragment.requireActivity()).get(PainDataViewModel::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PainDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calender_item, parent, false)
        return PainDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: PainDataViewHolder, position: Int) {
        holder.bind(model.getObjectAtPosition(position))
    }

    override fun getItemCount(): Int {
        return model.size()
    }

    fun updateDataSet(){
        notifyDataSetChanged()
    }

    fun addObject(ObjectInput: PainData) {
        model.addObject(ObjectInput)
        notifyDataSetChanged()
    }


    inner class PainDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //extract the textviews from the layout and set them to the vars
        val titleText = itemView.findViewById<TextView>(R.id.text_day_description)
        val painLevelText = itemView.findViewById<TextView>(R.id.text_pain_level)
        val daynumText = itemView.findViewById<TextView>(R.id.text_day_number)
        val monthText = itemView.findViewById<TextView>(R.id.text_month_string)




        init {
            itemView.setOnClickListener(){
                //Tracks mapping for index of the particular viewholder, and then  shove it in the right place
                model.updatePosition(adapterPosition)
            }
        }

        fun bind(painObject:PainData){
            //Bind the model object's specific data to the text view for each
            titleText.text = painObject.Title
            painLevelText.text = painObject.PainValue.toString()
            daynumText.text = painObject.Time.day.toString()
            monthText.text = painObject.Time.month.toString()


        }
    }
}