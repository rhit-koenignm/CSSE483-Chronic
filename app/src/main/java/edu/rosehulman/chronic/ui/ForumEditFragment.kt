package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.adapters.PostAdapter
import edu.rosehulman.chronic.databinding.FragmentDataBinding
import edu.rosehulman.chronic.databinding.FragmentForumEditBinding
import edu.rosehulman.chronic.databinding.FragmentForumHomeBinding
import edu.rosehulman.chronic.models.PostViewModel
import edu.rosehulman.chronic.models.UserViewModel

class ForumEditFragment : Fragment() {
        private lateinit var binding: FragmentForumEditBinding
        private lateinit var model: PostViewModel
        private lateinit var userModel: UserViewModel

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?

        ): View {
            setHasOptionsMenu(true)
            // Inflate the layout for this fragment
            binding = FragmentForumEditBinding.inflate(inflater,container,false)
            model = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)
            userModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

            setupWithExistingData()
            setupButtons()

            return binding.root
        }

    private fun setupButtons() {
        binding.editSubmitPostButton.setOnClickListener {

            val newTitle =  binding.editTitleText.text.toString()
            val newContent =binding.editDetailContentEdit.text.toString()
            val newPhotoURL = binding.editPhotoUrlText.text.toString()
            val newAuthor = userModel.user!!.userName

            model.updateCurrentPost(newTitle, newContent, newAuthor, newPhotoURL)
            findNavController().navigate(R.id.nav_forum_home)
        }

        binding.editPhotoUrlText.doAfterTextChanged {

        }

    }

    private fun setupWithExistingData() {
        val currentObject = model.getCurrentPost()

        binding.editTitleText.setText(currentObject.title)
        binding.editDetailContentEdit.setText(currentObject.content)
        binding.editPhotoUrlText.setText(currentObject.photoUrl)

    }

    companion object {
            const val fragmentName = "ForumEditFragment"
        }
    }