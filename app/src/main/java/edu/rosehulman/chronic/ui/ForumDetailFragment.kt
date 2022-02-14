package edu.rosehulman.chronic.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import edu.rosehulman.chronic.databinding.FragmentForumDetailBinding
import edu.rosehulman.chronic.models.PostViewModel


class ForumDetailFragment : Fragment(){
    private lateinit var binding: FragmentForumDetailBinding
    private lateinit var model:PostViewModel

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

        return binding.root
    }

    private fun setupButtons() {
        binding.addReplyButton.setOnClickListener {
            //todo
        }
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

    }

    private fun loadPostDataToFragment() {
       val currentObject = model.getCurrentPost()

        binding.detailTitleView.setText(currentObject.title)
        binding.detailAuthorView.setText(currentObject.author)
        binding.detailContentView.setText(currentObject.content)
        binding.PostImageView.load(currentObject.photoUrl){
            crossfade(true)
            transformations(CircleCropTransformation())
        }


    }

    companion object {
        const val fragmentName = "ForumDetailFragment"
    }
}