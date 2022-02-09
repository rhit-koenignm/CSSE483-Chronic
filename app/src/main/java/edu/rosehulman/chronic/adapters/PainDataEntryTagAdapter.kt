package edu.rosehulman.chronic.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.models.MyTagViewModel
import edu.rosehulman.chronic.models.PainDataEntryTagViewModel
import edu.rosehulman.chronic.models.Tag

class PainDataEntryTagAdapter(fragment: Fragment, fragmentName: String, dataTypeIn:String) : RecyclerView.Adapter<PainDataEntryTagAdapter.PainDataEntryTagViewHolder>() {
        private var dataType: String = dataTypeIn

    val fragment: Fragment = fragment

        // Newly added: this fragment name will allow us to limit functionality if the fragment is ProfileFragment
        val fragmentName = fragmentName
        val model = ViewModelProvider(fragment.requireActivity()).get(PainDataEntryTagViewModel::class.java)

        fun removeListener(fragmentName: String) {
            model.removeListener(fragmentName)
        }


        fun addUserListener(fragmentName: String, observer: () -> Unit) {
            Log.d(Constants.TAG, "Jumping into the addUserListener from adapter")
            model.addUserListener(fragmentName, observer)
        }

        fun removeUserListener(fragmentName: String) {
            model.removeUserListener(fragmentName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PainDataEntryTagViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
            return PainDataEntryTagViewHolder(view)
        }

        override fun onBindViewHolder(holder: PainDataEntryTagViewHolder, position: Int) {
            holder.bind(model.getTypeTagAt(position,dataType))
        }

        override fun getItemCount() = model.getTypeSize(dataType)

        inner class PainDataEntryTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tagTitleTextView = itemView.findViewById<TextView>(R.id.tag_item_title)
            val tagImageView = itemView.findViewById<ImageView>(R.id.tag_item_icon)

            init {
                tagImageView.setOnClickListener {
                    model.updateTypePos(adapterPosition,dataType)
                    model.toggleTypeTracked(adapterPosition,dataType)
                    notifyDataSetChanged()
                }
            }

            fun bind(tag: Tag){
                Log.d(Constants.TAG,"Binding a My Tag to $dataType Adapter in Data Entry Page")
                // If we are in the profile fragment we only care about myTags
                tagTitleTextView.text = tag.toString()

                if(tag.isTracked) {
                    tagImageView.load(R.drawable.ic_baseline_check_24)
                } else {
                    tagImageView.load(R.drawable.ic_baseline_add_24)
                }
            }
        }
}