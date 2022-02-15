package edu.rosehulman.chronic.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.adapters.ReplyAdapter
import edu.rosehulman.chronic.databinding.FragmentForumDetailBinding
import edu.rosehulman.chronic.models.PostViewModel
import edu.rosehulman.chronic.models.Reply
import edu.rosehulman.chronic.utilities.Constants


class ForumDetailFragment : Fragment(){
    private lateinit var binding: FragmentForumDetailBinding
    private lateinit var model:PostViewModel
    lateinit var adapter: ReplyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = FragmentForumDetailBinding.inflate(inflater,container,false)
        model = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        loadPostDataToFragment()
        setupButtons()

        adapter = ReplyAdapter(this)
        binding.replyRecycler.adapter = adapter
        binding.replyRecycler.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(requireContext())
        binding.replyRecycler.layoutManager = layoutManager
        binding.replyRecycler.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
        adapter.addListener(fragmentName, model.getCurrentPost().id)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeListener(fragmentName)
    }

    private fun setupButtons() {
        binding.shareButton.setOnClickListener {
            val currentObject = model.getCurrentPost()
            val formattedText = "${currentObject.author} talked about ${currentObject.title} on Chronic! Download the App Today!"

            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, formattedText)
            sendIntent.setType("text/plain")

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val currentPost = model.getCurrentPost()
        if (currentPost.authorID == Firebase.auth.uid) {
            binding.forumDetailEditButton.isVisible = true
            binding.forumDetailEditButton.setOnClickListener {
                Log.d(Constants.TAG, "Pressed forum detail edit button")
                findNavController().navigate(R.id.nav_forum_edit)
            }
        } else {
            binding.forumDetailEditButton.isVisible = false
        }

        binding.addReplyButton.setOnClickListener {
            Log.d(Constants.TAG, "Pressed add reply button, launching edit dialog")
            adapter.showEditDialog(this.requireContext(), null)
        }
    }

    private fun loadPostDataToFragment() {
       val currentObject = model.getCurrentPost()

        binding.detailTitleView.setText(currentObject.title)
        binding.detailAuthorView.setText(currentObject.author)
        binding.detailContentView.setText(currentObject.content)
        binding.detailTimeView.setText(currentObject.getDate() + "     " + currentObject.getTime())
        binding.PostImageView.load(currentObject.photoUrl){
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    companion object {
        const val fragmentName = "ForumDetailFragment"
    }
}