package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.rosehulman.chronic.R
import edu.rosehulman.chronic.databinding.FragmentReminderEditBinding
import edu.rosehulman.chronic.models.Reminder
import edu.rosehulman.chronic.models.ReminderViewModel

class ReminderEditFragment: Fragment() {

    private lateinit var binding: FragmentReminderEditBinding
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var dayButtons: Array<Button>
    private lateinit var buttonBools: MutableList<Boolean>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reminderViewModel = ViewModelProvider(requireActivity()).get(ReminderViewModel::class.java)
        buttonBools = reminderViewModel.getCurrentReminder().daysActive as MutableList<Boolean>

        binding = FragmentReminderEditBinding.inflate(inflater, container, false)

        dayButtons = Array<Button>(7) { android.widget.Button(this.requireContext()) }
        setUpButtons()
        updateView()

        return binding.root
    }

    fun setUpButtons() {
        binding.reminderSaveButton.setOnClickListener {
            var current = reminderViewModel.getCurrentReminder()
            var newTitle = current.title
            var newContent = current.content
            var newHours = current.hours
            var newMinutes = current.minutes
            var activeBool = current.isActive


            if(binding.reminderDetailTitleEdit.text.isNotEmpty()) {
                newTitle = binding.reminderDetailTitleEdit.text.toString()
            }
            if(binding.reminderDetailContentEdit.text.isNotEmpty()) {
                newContent = binding.reminderDetailContentEdit.text.toString()
            }

            newHours = binding.reminderDetailTimepicker.hour
            newMinutes = binding.reminderDetailTimepicker.minute

            var newReminder = Reminder(newTitle, newContent, newHours, newMinutes, activeBool, buttonBools)
            newReminder.id = current.id
            reminderViewModel.updateCurrentReminder(newReminder)
            updateView()

            findNavController().navigate(R.id.nav_reminder_list)
        }

        binding.reminderDeleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to delete this reminder?")
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    reminderViewModel.removeCurrentReminder()
                    findNavController().popBackStack()
                    updateView()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        for (i in 0 until buttonBools.size) {
            val id = resources.getIdentifier("dayButton$i", "id", activity?.packageName)
            dayButtons[i] = binding.root.findViewById<Button>(id)

            dayButtons[i].setOnClickListener {
                buttonBools.set(i, !buttonBools.get(i))
                updateDayButtons()
            }

            updateDayButtons()
        }
    }

    fun updateView() {
        var currentReminder = reminderViewModel.getCurrentReminder()

        binding.reminderDetailTitleEdit.setText(currentReminder.title)
        binding.reminderDetailContentEdit.setText(currentReminder.content)
        binding.reminderDetailTimepicker.hour = currentReminder.hours
        binding.reminderDetailTimepicker.minute = currentReminder.minutes

        updateDayButtons()
    }

    fun updateDayButtons() {
        for(i in 0 until buttonBools.size) {
            if(buttonBools.get(i)) {
                dayButtons[i].setBackgroundColor(resources.getColor(R.color.plum))
            } else {
                dayButtons[i].setBackgroundColor(resources.getColor(R.color.grape))
            }
        }
    }
}