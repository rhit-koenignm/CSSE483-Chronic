package edu.rosehulman.chronic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.models.MyTagViewModel
import edu.rosehulman.chronic.models.Tag
import edu.rosehulman.chronic.models.UserData
import edu.rosehulman.chronic.ui.MyTagsFragment

class MyTagAdapter(fragment: MyTagsFragment) : RecyclerView.Adapter<MyTagAdapter.MyTagViewHolder>() {

    val model = ViewModelProvider(fragment.requireActivity()).get(MyTagViewModel::class.java)

    fun addListener(fragmentName: String, userID: String, type: String) {
        model.addListener(fragmentName, userID, type) {
            notifyDataSetChanged()
        }
    }

    fun removeListener(fragmentName: String) {
        model.removeListener(fragmentName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        return MyTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyTagViewHolder, position: Int) {
        holder.bind(model.getTagAt(position))
    }

    override fun getItemCount() = model.size()

    fun addTag(tag: Tag?) {
        model.createTag(tag)
        this.notifyDataSetChanged()
    }

    inner class MyTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagTitleTextView = itemView.findViewById<TextView>(R.id.tag_item_title)
        val tagImageView = itemView.findViewById<ImageView>(R.id.tag_item_icon)

        init {
            itemView.setOnLongClickListener {
                model.updatePos(adapterPosition)
                //This is where we'll put our popup that lets us delete or update the tag
                true
            }

            tagImageView.setOnClickListener {
                model.updatePos(adapterPosition)
                model.toggleTracked()
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(tag: Tag){
            tagTitleTextView.text = tag.toString()

            if(tag.isTracked) {
                tagImageView.load(R.drawable.ic_baseline_check_24)
            } else {
                tagImageView.load(R.drawable.ic_baseline_add_24)
            }
        }
    }
}