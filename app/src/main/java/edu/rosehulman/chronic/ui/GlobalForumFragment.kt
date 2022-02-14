package edu.rosehulman.chronic.ui

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.adapters.PostAdapter
import edu.rosehulman.chronic.databinding.FragmentForumHomeBinding
import edu.rosehulman.chronic.databinding.FragmentLoadingBinding
import edu.rosehulman.chronic.models.Post
import edu.rosehulman.chronic.utilities.Constants

class GlobalForumFragment: Fragment() {
    private lateinit var binding: FragmentForumHomeBinding
    private lateinit var adapter: PostAdapter
    private var isYourPosts: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = FragmentForumHomeBinding.inflate(inflater,container,false)

        adapter = PostAdapter(this)
        binding.postRecyclerView.adapter = adapter
        updateListener()
        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postRecyclerView.setHasFixedSize(true)

        binding.forumAddFab.setOnClickListener {
        //findNavController().navigate(R.id.nav_forum_edit)
            Log.d(Constants.TAG, "Added new default post")
            val defaultPost = Post(Firebase.auth.uid!!,"Sargent Schultz","Test Post Please Ignore","I see nothing! I know nothing!","")
            adapter.addPost(defaultPost)
        }

        binding.forumYourpostsButton.setOnClickListener {
            Log.d(Constants.TAG, "Pressed your posts buttons")
            isYourPosts = true
            updateListener()
            updateButtons()
        }

        binding.forumGlobalButton.setOnClickListener {
            Log.d(Constants.TAG, "Pressed global posts buttons")
            isYourPosts = false
            updateListener()
            updateButtons()
        }

        updateButtons()
        return binding.root
    }

    fun updateListener() {
        if(isYourPosts) {
            adapter.removeListener(fragmentName + "Time")
            adapter.addListener(fragmentName + "Author", "Author")
        } else {
            adapter.removeListener(fragmentName + "Author")
            adapter.addListener(fragmentName + "Time", "Time")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeListener(fragmentName)
    }

    fun updateButtons() {
        if(!isYourPosts){
            binding.forumGlobalButton.setBackgroundColor(resources.getColor(R.color.plum))
            binding.forumYourpostsButton.setBackgroundColor(resources.getColor(R.color.grape))
//            binding.forumGlobalButton.isSelected = true
//            binding.forumYourpostsButton.isSelected = false
        } else {
            binding.forumGlobalButton.setBackgroundColor(resources.getColor(R.color.grape))
            binding.forumYourpostsButton.setBackgroundColor(resources.getColor(R.color.plum))
//            binding.forumGlobalButton.isSelected = false
//            binding.forumYourpostsButton.isSelected = true
        }
    }

    companion object {
        const val fragmentName = "GlobalForumFragment"
    }
}