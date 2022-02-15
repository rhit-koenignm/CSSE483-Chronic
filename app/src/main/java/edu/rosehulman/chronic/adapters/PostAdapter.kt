package edu.rosehulman.chronic.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.models.Post
import edu.rosehulman.chronic.models.PostViewModel
import edu.rosehulman.chronic.ui.GlobalForumFragment
import edu.rosehulman.chronic.utilities.Constants
import org.w3c.dom.Text

class PostAdapter(val fragment: GlobalForumFragment) : Adapter<PostAdapter.PostViewHolder>() {
    val model = ViewModelProvider(fragment.requireActivity()).get(PostViewModel::class.java)

    fun addListener(fragmentName: String, filterBy: String) {
        model.addListener(fragmentName, filterBy) {
            notifyDataSetChanged()
        }
    }

    fun removeListener(fragmentName: String) {
        model.removeListener(fragmentName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_row_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(model.getPostAt(position))
    }

    fun addPost(post: Post?) {
        if(post != null) {
            model.addPost(post)
        }
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = model.size()

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView = itemView.findViewById<ImageView>(R.id.post_image)
        val postTitleView = itemView.findViewById<TextView>(R.id.post_title_view)
        val postContentPreview = itemView.findViewById<TextView>(R.id.post_content_preview)
        val postAuthorView = itemView.findViewById<TextView>(R.id.post_author_view)

        init {
            itemView.setOnClickListener {
                model.updatePos(adapterPosition)
                Log.d(Constants.TAG, "Pressed post at pos $adapterPosition")
                //itemView.findNavController().popBackStack()
                itemView.findNavController().navigate(R.id.nav_forum_detail)
            }
        }

        fun bind(post: Post) {
            if(post.content.length > 200) {
                val shortContent = post.content.substring(0, 200)
                postContentPreview.text = shortContent + "... "
            } else {
                postContentPreview.text = post.content
            }

            if(post.photoUrl.isNotEmpty()){
                postImageView.load(post.photoUrl)
            } else {
                postImageView.load(fragment.resources.getDrawable(R.drawable.ic_menu_camera))
            }

            postTitleView.text = post.title
            postAuthorView.text = post.author
        }
    }
}