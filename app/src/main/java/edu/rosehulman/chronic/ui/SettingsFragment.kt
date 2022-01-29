package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import edu.rosehulman.chronic.Constants
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.databinding.FragmentDataBinding
import edu.rosehulman.chronic.databinding.FragmentSettingsBinding
import edu.rosehulman.chronic.models.UserViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var userModel:UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        userModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

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

        val root: View = binding.root
        return root
    }
}