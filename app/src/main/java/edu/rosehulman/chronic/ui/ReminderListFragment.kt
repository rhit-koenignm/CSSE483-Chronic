package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.chronic.adapters.ReminderAdapter
import edu.rosehulman.chronic.databinding.FragmentReminderListBinding

class ReminderListFragment: Fragment() {

    private lateinit var binding: FragmentReminderListBinding
    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReminderListBinding.inflate(inflater, container, false)

        adapter = ReminderAdapter(this)

        //set recyclerview and adapter properties
        binding.remindersRecyclerView.adapter = adapter
        adapter.addListener(fragmentName)
        binding.remindersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.remindersRecyclerView.setHasFixedSize(true)

        binding.remindersFab.setOnClickListener {
            adapter.addReminder(null)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeListener(fragmentName)
    }

    companion object {
        const val fragmentName = "ReminderListFragment"
    }
}