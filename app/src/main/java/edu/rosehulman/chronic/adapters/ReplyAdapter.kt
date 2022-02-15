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
import androidx.core.view.isVisible
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

    fun updateCurrentReply(reply: Reply?) {
        model.updateCurrentReply(reply)
        this.notifyDataSetChanged()
    }

    // The nice thing is this edit dialog will show up if we are editing an existing tag or creating a new one
    fun showEditDialog(context: Context, reply: Reply?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        // Name the dialog
        if(reply == null) {
            builder.setTitle("Create a new Reply")
        } else {
            builder.setTitle("Edit your reply")
        }


        var replyInputsLayout = LinearLayout(context)
        replyInputsLayout.orientation = LinearLayout.VERTICAL
        replyInputsLayout.setPadding(4, 4, 4, 4)

        // Set up the input for content
        val contentInput = EditText(context)
        contentInput.inputType = InputType.TYPE_CLASS_TEXT
        if(reply == null) {
            // This is if we are making a new reply, otherwise set the content to the current
            contentInput.setHint("Enter New Reply Content")
        } else {
            contentInput.setText(reply.content.toString())
        }

        // Adding our title and spinner to the linear layout
        replyInputsLayout.addView(contentInput)
        builder.setView(replyInputsLayout)

        //Set up the buttons
        builder.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
            var newContent = contentInput.text.toString()
            if(reply == null){
               this.addReply(Reply(content = newContent))
            } else {
               this.updateCurrentReply(Reply(content = newContent))
            }
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->  dialog.cancel()})

        if(reply != null) {
            //Delete will only show up for existing tags
            builder.setNeutralButton(
                "Delete reply",
                DialogInterface.OnClickListener { dialog, which ->
                    // This is where we will delete the tag
                    model.removeCurrentReply()
                })
        }
        // Showing dialog
        builder.show()
    }

    fun showNotAuthorDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(fragment.requireContext())

        builder.setTitle("You are not the author of this reply")
        builder.setMessage("Users can only update and delete replies created by them.")

        builder.setNegativeButton("Ok", DialogInterface.OnClickListener { dialog, which ->  dialog.cancel()})

        builder.show()
    }

    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val replyAuthorTextView = itemView.findViewById<TextView>(R.id.reply_author_title)
        val replyTimeTextView = itemView.findViewById<TextView>(R.id.reply_time_text)
        val replyContentTextView = itemView.findViewById<TextView>(R.id.reply_content_text)
        val replyEditButton = itemView.findViewById<ImageButton>(R.id.reply_edit_button)

        init {
            replyEditButton.setOnClickListener {
                model.updatePos(adapterPosition)
                var reply = model.getCurrentReply()
                if (model.isUserCurrentReplyAuthor()) {
                    showEditDialog(fragment.requireContext(), reply)
                } else {
                    showNotAuthorDialog()
                }
            }
        }

        fun bind(reply: Reply) {
            replyAuthorTextView.text = reply.author
            replyTimeTextView.text = reply.getDate() + "     " + reply.getTime()
            replyContentTextView.text = reply.content
            if(Firebase.auth.uid!! != reply.authorID) {
                replyEditButton.isEnabled = false
                replyEditButton.isVisible = false
            }
        }
    }
}