package edu.rosehulman.chronic.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.models.Reply
import edu.rosehulman.chronic.models.ReplyViewModel
import edu.rosehulman.chronic.models.Tag

class ReplyAdapter(fragment: Fragment) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    val fragment: Fragment = fragment
    val model = ViewModelProvider(fragment.requireActivity()).get(ReplyViewModel::class.java)

    fun addListener(fragmentName: String, postID: String) {
        model.addListener(fragmentName, postID) {
            notifyDataSetChanged()
        }
    }

    fun removeListener(fragmentName: String) {
        model.removeListener(fragmentName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reply_row_item, parent, false)
        return ReplyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReplyAdapter.ReplyViewHolder, position: Int) {
        holder.bind(model.getReplyAt(position))
    }

    override fun getItemCount() = model.size()

    fun addReply(reply: Reply?) {
        model.addReply(reply)
        this.notifyDataSetChanged()
    }

    // The nice thing is this edit dialog will show up if we are editing an existing tag or creating a new one
//    fun showEditDialog(context: Context, tag: Tag?, currentType: String) {
//        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
//        // Name the dialog
//        if(tag == null) {
//            builder.setTitle("Create a new Tag")
//        } else {
//            builder.setTitle("Edit your tag")
//        }
//
//
//        var tagInputsLayout = LinearLayout(context)
//        tagInputsLayout.orientation = LinearLayout.VERTICAL
//        tagInputsLayout.setPadding(4, 4, 4, 4)
//
//        // Grabbing our filter and tag types arrays
//        var tagTypesArray = fragment.resources.getStringArray(R.array.tag_types)
//        var tagFiltersArray = fragment.resources.getStringArray(R.array.tag_filter_types)
//
//        // Set up the input for title
//        val titleInput = EditText(context)
//        titleInput.inputType = InputType.TYPE_CLASS_TEXT
//        if(tag == null) {
//            // This is if we are making a new tag, otherwise set the type to the current
//            titleInput.setHint("Enter New Tag title")
//        } else {
//            titleInput.setText(tag.title.toString())
//        }
//
//        // Set up the input for the type
//        val spinner: Spinner = Spinner(context)
//        ArrayAdapter.createFromResource(
//            context,
//            R.array.tag_types,
//            android.R.layout.simple_spinner_item
//        ).also { filterAdapter ->
//            //Specify the layout to use when the list of choices appears
//            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner.adapter = filterAdapter
//        }
//
//        // If the tag passed in is null, we want to set the current value to the current type
//        if (tag == null) {
//            spinner.setSelection(
//                when (currentType) {
//                    tagFiltersArray[0] -> 0
//                    tagFiltersArray[1] -> 0
//                    tagFiltersArray[2] -> 1
//                    tagFiltersArray[3] -> 2
//                    else -> {
//                        0
//                    }
//                })
//        } else {
//            spinner.setSelection(
//                when (tag.type) {
//                    tagTypesArray[0] -> 0
//                    tagTypesArray[1] -> 1
//                    tagTypesArray[2] -> 2
//                    else -> {
//                        0
//                    }
//                }
//            )
//        }
//
//        // Adding our title and spinner to the linear layout
//        tagInputsLayout.addView(titleInput)
//        tagInputsLayout.addView(spinner)
//        builder.setView(tagInputsLayout)
//
//        //Set up the buttons
//        builder.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
//            var newTitle = titleInput.text.toString()
//            var newType = spinner.selectedItem.toString()
//            if(tag == null){
//                // Call the adapter's method instead actually
//                this.addTag(Tag(newTitle, newType))
//            } else {
//                model.updateTag(Tag(newTitle, newType, tag.creator, tag.isTracked))
//            }
//            notifyDataSetChanged()
//        })
//
//        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->  dialog.cancel()})
//
//        if(tag != null) {
//            //Delete will only show up for existing tags
//            builder.setNeutralButton(
//                "Delete tag",
//                DialogInterface.OnClickListener { dialog, which ->
//                    // This is where we will delete the tag
//                    model.removeCurrentTag()
//                })
//        }
//        // Showing dialog
//        builder.show()
//    }


    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val replyAuthorTextView = itemView.findViewById<TextView>(R.id.reply_author_title)
        val replyTimeTextView = itemView.findViewById<TextView>(R.id.reply_time_text)
        val replyContentTextView = itemView.findViewById<TextView>(R.id.reply_content_text)
        val replyEditButton = itemView.findViewById<ImageButton>(R.id.reply_edit_button)

        init {
//            model.updatePos(adapterPosition)
//            if(Firebase.auth.uid!! != model.getCurrentReply().authorID) {
//                replyEditButton.isEnabled = false
//            } else {
//                replyEditButton.setOnClickListener {
//                    //TODO: Put call to edit reply function here
//                }
//            }
        }

        fun bind(reply: Reply) {
            replyAuthorTextView.text = reply.author
            replyTimeTextView.text = reply.getDate() + "     " + reply.getTime()
            replyContentTextView.text = reply.content
            if(Firebase.auth.uid!! != reply.authorID) {
                replyEditButton.isEnabled = false
            }
        }
    }
}