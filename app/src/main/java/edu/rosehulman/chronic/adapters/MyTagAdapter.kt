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
import edu.rosehulman.chronic.models.Tag

// Author: Natalie Koenig
// Description: MyTagAdapter holds the MyTagViewModels that will show up in the profile page and my tags page. It contains relevant functions to help us interact with the tags.
// Date: 1/31/2022
class MyTagAdapter(fragment: Fragment, fragmentName: String) : RecyclerView.Adapter<MyTagAdapter.MyTagViewHolder>() {

    val fragment: Fragment = fragment

    // Newly added: this fragment name will allow us to limit functionality if the fragment is ProfileFragment
    val fragmentName = fragmentName
    val model = ViewModelProvider(fragment.requireActivity()).get(MyTagViewModel::class.java)

    fun addListener(fragmentName: String, type: String) {
        model.addListener(fragmentName, type) {
            notifyDataSetChanged()
        }
    }

    fun removeListener(fragmentName: String) {
        model.removeListener(fragmentName)
    }

    // This does a listener on the current user so we can grab their tags
    fun addUserListener(fragmentName: String, observer: () -> Unit) {
        Log.d(Constants.TAG, "Jumping into the addUserListener from adapter")
        model.addUserListener(fragmentName, observer)
    }

    fun removeUserListener(fragmentName: String) {
        model.removeUserListener(fragmentName)
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
        var didAdd = model.createTag(tag)
        this.notifyDataSetChanged()
    }


    // The nice thing is this edit dialog will show up if we are editing an existing tag or creating a new one
    fun showEditDialog(context: Context, tag: Tag?, currentType: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        // Name the dialog
        if(tag == null) {
            builder.setTitle("Create a new Tag")
        } else {
            builder.setTitle("Edit your tag")
        }


        var tagInputsLayout = LinearLayout(context)
        tagInputsLayout.orientation = LinearLayout.VERTICAL
        tagInputsLayout.setPadding(4, 4, 4, 4)

        // Grabbing our filter and tag types arrays
        var tagTypesArray = fragment.resources.getStringArray(R.array.tag_types)
        var tagFiltersArray = fragment.resources.getStringArray(R.array.tag_filter_types)

        // Set up the input for title
        val titleInput = EditText(context)
        titleInput.inputType = InputType.TYPE_CLASS_TEXT
        if(tag == null) {
            // This is if we are making a new tag, otherwise set the type to the current
            titleInput.setHint("Enter New Tag title")
        } else {
            titleInput.setText(tag.title.toString())
        }

        // Set up the input for the type
        val spinner: Spinner = Spinner(context)
        ArrayAdapter.createFromResource(
            context,
            R.array.tag_types,
            android.R.layout.simple_spinner_item
        ).also { filterAdapter ->
            //Specify the layout to use when the list of choices appears
            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = filterAdapter
        }

        // If the tag passed in is null, we want to set the current value to the current type
        if (tag == null) {
            spinner.setSelection(
            when (currentType) {
                tagFiltersArray[0] -> 0
                tagFiltersArray[1] -> 0
                tagFiltersArray[2] -> 1
                tagFiltersArray[3] -> 2
                else -> {
                    0
                }
            })
        } else {
            spinner.setSelection(
                when (tag.type) {
                    tagTypesArray[0] -> 0
                    tagTypesArray[1] -> 1
                    tagTypesArray[2] -> 2
                    else -> {
                        0
                    }
                }
            )
        }

        // Adding our title and spinner to the linear layout
        tagInputsLayout.addView(titleInput)
        tagInputsLayout.addView(spinner)
        builder.setView(tagInputsLayout)

        //Set up the buttons
        builder.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
            var newTitle = titleInput.text.toString()
            var newType = spinner.selectedItem.toString()
            if(tag == null){
                // Call the adapter's method instead actually
                this.addTag(Tag(newTitle, newType))
            } else {
                model.updateTag(Tag(newTitle, newType, tag.creator, tag.isTracked))
            }
            notifyDataSetChanged()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->  dialog.cancel()})

        if(tag != null) {
            //Delete will only show up for existing tags
            builder.setNeutralButton(
                "Delete tag",
                DialogInterface.OnClickListener { dialog, which ->
                    // This is where we will delete the tag
                    model.removeCurrentTag()
                })
        }
        // Showing dialog
        builder.show()
    }

    fun showNotAuthorDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(fragment.requireContext())

        builder.setTitle("You are not the owner of this tag")
        builder.setMessage("Users can only update and delete tags created by them.")

        builder.setNegativeButton("Ok", DialogInterface.OnClickListener { dialog, which ->  dialog.cancel()})

        builder.show()
    }

    inner class MyTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagTitleTextView = itemView.findViewById<TextView>(R.id.tag_item_title)
        val tagImageView = itemView.findViewById<ImageView>(R.id.tag_item_icon)

        init {
            if(!fragmentName.equals("ProfileFragment")) {
                itemView.setOnLongClickListener {
                    model.updatePos(adapterPosition)
                    var tag = model.getCurrentTag()
                    if (model.creatorIsUser()) {
                        showEditDialog(fragment.requireContext(), tag, tag.type)
                    } else {
                        showNotAuthorDialog()
                    }

                    //This is where we'll put our popup that lets us delete or update the tag
                    true
                }

                tagImageView.setOnClickListener {
                    //Need to add in the action of tracking or not tracking
                    model.updatePos(adapterPosition)
                    model.toggleTracked()
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        fun bind(tag: Tag){
            // If we are in the profile fragment we only care about myTags
            if(fragmentName.equals("ProfileFragment")){
                tagTitleTextView.text = tag.title
            } else {
                tagTitleTextView.text = tag.toString()
            }

            if(tag.isTracked) {
                tagImageView.load(R.drawable.ic_baseline_check_24)
            } else {
                tagImageView.load(R.drawable.ic_baseline_add_24)
            }
        }
    }
}