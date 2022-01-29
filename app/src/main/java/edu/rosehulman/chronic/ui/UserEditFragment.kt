package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.databinding.FragmentUserEditBinding
import edu.rosehulman.chronic.models.UserViewModel


class UserEditFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val userModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        Log.d(Constants.TAG, "User in edit fragment: ${userModel.user}")

        val binding = FragmentUserEditBinding.inflate(inflater, container, false)
        binding.userEditDoneButton.setOnClickListener {
            // Save user info into Firestore.
            var firstNameInput = binding.userEditNameEditText.text.toString().split(" ").get(0)
            var lastNameInput = binding.userEditNameEditText.text.toString().split(" ").get(1)

            userModel.updateUser(
                firstNameIn = firstNameInput,
                lastNameIn = lastNameInput,
                emailIn = binding.userEditEmailEditText.text.toString(),
                usernameIn = binding.userEditNameEditText.text.toString(),
                profilephotoURLIn = binding.userEditProfileImageUrlEditText.text.toString(),
                newHasCompletedSetup = true,
            )
            findNavController().navigate(R.id.nav_pain_tracking)
        }

        userModel.getOrMakeUser {
            with(userModel.user!!) {
                Log.d(Constants.TAG, "$this")
                binding.userEditNameEditText.setText("$firstName $lastName")
                binding.userEditEmailEditText.setText(Email)
                binding.userEditUsernameEditText.setText(userName)
                binding.userEditProfileImageUrlEditText.setText(ProfileURL)
            }
        }
        return binding.root
    }

}
